package com.clinicadmin.service.impl;


import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.Branch;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.TherapistDTO;
import com.clinicadmin.dto.TherapistLoginDTO;
import com.clinicadmin.dto.TherapistLoginResponseDTO;
import com.clinicadmin.entity.DoctorLoginCredentials;
import com.clinicadmin.entity.Documents;
import com.clinicadmin.entity.Therapist;
import com.clinicadmin.feignclient.AdminServiceClient;
import com.clinicadmin.repository.DoctorLoginCredentialsRepository;
import com.clinicadmin.repository.TherapistRepository;
import com.clinicadmin.service.TherapistService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TherapistServiceImpl implements TherapistService {

    @Autowired
    private TherapistRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    DoctorLoginCredentialsRepository credentialsRepository;
    
    @Autowired
    AdminServiceClient adminServiceClient;
    
    @Autowired
    ObjectMapper objectMapper;
    @Override
    public Response therapistOnboarding(TherapistDTO dto) {

        log.info("Therapist onboarding started for contact number: {}", dto.getContactNumber());

        Response response = new Response();

        
        // ✅ 2. Validate contact number
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Contact number is required");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        String contact = dto.getContactNumber().trim();

        // ✅ 3. Duplicate check (same as nurse existsBy)
        if (repository.existsByContactNumber(contact)) {
            log.warn("Therapist already exists with contact number: {}", contact);
            response.setSuccess(false);
            response.setMessage("Therapist already exists with this mobile number");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        // ✅ 4. Check credentials already exist
        if (credentialsRepository.existsByUsername(contact)) {
            log.warn("Credentials already exist for username: {}", contact);
            response.setSuccess(false);
            response.setMessage("Login credentials already exist for this mobile number");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        // ✅ 5. Get Branch Details (same as nurse)
        log.debug("Fetching branch details for branchId: {}", dto.getBranchId());

        ResponseEntity<Response> res = adminServiceClient.getBranchById(dto.getBranchId());
        Branch br = objectMapper.convertValue(res.getBody().getData(), Branch.class);

        // ✅ 6. Map DTO → Entity
        Therapist therapist = mapToEntity(dto);
        therapist.setBranchName(br.getBranchName());

        // ✅ 7. Generate Therapist ID
        String therapistId = generateTherapistId();
        therapist.setTherapistId(therapistId);

        // ✅ 8. Username & Password
        String username = therapistId;
        String rawPassword = generatePassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // ✅ 9. Save Therapist
        Therapist savedTherapist = repository.save(therapist);

        log.info("Therapist saved successfully with therapistId: {}", savedTherapist.getTherapistId());

        // ✅ 10. Save Credentials (IMPORTANT - same as nurse)
        DoctorLoginCredentials credentials = DoctorLoginCredentials.builder()
                .staffId(savedTherapist.getTherapistId())
                .staffName(savedTherapist.getFullName())
                .hospitalId(savedTherapist.getClinicId())
                .hospitalName(savedTherapist.getClinicName())
                .branchId(savedTherapist.getBranchId())
                .branchName(savedTherapist.getBranchName())
                .username(username)
                .password(encodedPassword)
                .role(dto.getRole())
//                .permissions(savedTherapist.())
                .build();

        credentialsRepository.save(credentials);

        log.info("Login credentials created for therapistId: {}", savedTherapist.getTherapistId());

        // ✅ 11. Map to DTO response
        TherapistDTO savedDTO = mapToDTO(savedTherapist);
        savedDTO.setUserName(username);
        savedDTO.setPassword(rawPassword);

        // ✅ 12. Final response
        response.setSuccess(true);
        response.setData(savedDTO);
        response.setMessage("Therapist added successfully");
        response.setStatus(HttpStatus.CREATED.value());

        log.info("Therapist onboarding completed successfully for therapistId: {}", savedTherapist.getTherapistId());

        return response;
    }    // ================= LOGIN =================
//    @Override
//    public ResponseStructure<TherapistLoginResponseDTO> login(TherapistLoginDTO dto) {
//
//        Therapist user = repository.findByUserName(dto.getUserName())
//                .orElseThrow(() -> new RuntimeException("Invalid username"));
//
//        //  password check
//        if (!dto.getPassword().equals(user.getPassword())) {
//            return ResponseStructure.buildResponse(
//                    null,
//                    "Invalid password",
//                    HttpStatus.UNAUTHORIZED,
//                    401);
//        }
//
//        //  physioType check
//        if (dto.getGetPhysioType() != null && 
//            !dto.getGetPhysioType().equalsIgnoreCase(user.getPhysioType())) {
//
//            return ResponseStructure.buildResponse(
//                    null,
//                    "Invalid physio type",
//                    HttpStatus.UNAUTHORIZED,
//                    401);
//        }
//
//        //  build response DTO
//        TherapistLoginResponseDTO responseDTO = new TherapistLoginResponseDTO();
//        responseDTO.setTherapistId(user.getTherapistId());
//        responseDTO.setClinicId(user.getClinicId());
//        responseDTO.setBranchId(user.getBranchId());
//        responseDTO.setTherapistName(user.getFullName());
//        responseDTO.setPhysioType(user.getPhysioType());
//
//        return ResponseStructure.buildResponse(
//                responseDTO,
//                "Login Success",
//                HttpStatus.OK,
//                200);
//    }
    // ================= GET BY THERAPIST ID =================
    @Override
    public ResponseStructure<TherapistDTO> getBytherapistId(String therapistId) {

        Therapist entity = repository.findByTherapistId(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        return ResponseStructure.buildResponse(
                mapToDTO(entity),
                "Fetched successfully",
                HttpStatus.OK,
                200);
    }

    // ================= GET BY CLINICID BRANCHID AND THERPISTID =================
    @Override
    public ResponseStructure<List<TherapistDTO>> getByClinicIdBranchIdAndTherapistId(
            String clinicId,
            String branchId,
            String therapistId) {

        List<Therapist> list;

        if (therapistId != null && !therapistId.isBlank()) {
            Therapist t = repository.findByTherapistId(therapistId)
                    .orElseThrow(() -> new RuntimeException("Not found"));
            list = List.of(t);

        } else if (clinicId != null && branchId != null) {
            list = repository.findByClinicIdAndBranchId(clinicId, branchId);

        } else if (clinicId != null) {
            list = repository.findByClinicId(clinicId);

        } else {
            list = repository.findAll();
        }

        List<TherapistDTO> dtos = list.stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseStructure.buildResponse(
                dtos,
                dtos.isEmpty() ? "No data found" : "Fetched successfully",
                HttpStatus.OK,
                200);
    }

    // ================= GET BY CLINICID AND BRANCHID =================
    @Override
    public ResponseStructure<List<TherapistDTO>> getByClinicIdAndBranchId(
            String clinicId,
            String branchId) {

        List<TherapistDTO> list = repository
                .findByClinicIdAndBranchId(clinicId, branchId)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return ResponseStructure.buildResponse(
                list,
                "successfully fetched therapists",
                HttpStatus.OK,
                200);
    }

 // ================= UPDATE BY THERAPISTID=================
    @Override
    public ResponseStructure<TherapistDTO> updateBytherapistId(
            String therapistId,
            TherapistDTO dto) {

        Therapist existing = repository.findByTherapistId(therapistId)
                .orElseThrow(() -> new RuntimeException("Therapist not found"));

        //  Basic Info
        if (dto.getFullName() != null) existing.setFullName(dto.getFullName());
        if (dto.getContactNumber() != null) existing.setContactNumber(dto.getContactNumber());
        if (dto.getGender() != null) existing.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) existing.setDateOfBirth(dto.getDateOfBirth());

        //  Clinic Info
        if (dto.getClinicId() != null) existing.setClinicId(dto.getClinicId());
        if (dto.getBranchId() != null) existing.setBranchId(dto.getBranchId());

        //  Professional Info
        if (dto.getQualification() != null) existing.setQualification(dto.getQualification());
        if (dto.getYearsOfExperience() != null) existing.setYearsOfExperience(dto.getYearsOfExperience());

        //  Lists
        if (dto.getServices() != null) existing.setServices(dto.getServices());
        if (dto.getSpecializations() != null) existing.setSpecializations(dto.getSpecializations());
        if (dto.getExpertiseAreas() != null) existing.setExpertiseAreas(dto.getExpertiseAreas());
        if (dto.getTreatmentTypes() != null) existing.setTreatmentTypes(dto.getTreatmentTypes());

        if (dto.getAvailability() != null) existing.setAvailability(dto.getAvailability());

        if (dto.getBio() != null) existing.setBio(dto.getBio());

        // ================= BASE64 ENCODE =================
        if (dto.getDocuments() != null) {

            Documents docs = new Documents();

            if (dto.getDocuments().getLicenseCertificate() != null) {
                docs.setLicenseCertificate(
                        java.util.Base64.getEncoder().encodeToString(
                                dto.getDocuments().getLicenseCertificate().getBytes()
                        )
                );
            }

            if (dto.getDocuments().getDegreeCertificate() != null) {
                docs.setDegreeCertificate(
                        java.util.Base64.getEncoder().encodeToString(
                                dto.getDocuments().getDegreeCertificate().getBytes()
                        )
                );
            }

            if (dto.getDocuments().getProfilePhoto() != null) {
                docs.setProfilePhoto(
                        java.util.Base64.getEncoder().encodeToString(
                                dto.getDocuments().getProfilePhoto().getBytes()
                        )
                );
            }

            existing.setDocuments(docs);
        }

        if (dto.getLanguages() != null) existing.setLanguages(dto.getLanguages());

        if (dto.getRole() != null) existing.setRole(dto.getRole());
        if (dto.getPhysioType() != null) existing.setPhysioType(dto.getPhysioType());

        //  Save
        Therapist updated = repository.save(existing);

        // ================= RESPONSE (DECODE BASE64) =================
        TherapistDTO response = new TherapistDTO();

        response.setTherapistId(updated.getTherapistId());
        response.setClinicId(updated.getClinicId());
        response.setBranchId(updated.getBranchId());
        response.setFullName(updated.getFullName());
        response.setContactNumber(updated.getContactNumber());
        response.setGender(updated.getGender());
        response.setDateOfBirth(updated.getDateOfBirth());
        response.setQualification(updated.getQualification());
        response.setYearsOfExperience(updated.getYearsOfExperience());
        response.setServices(updated.getServices());
        response.setSpecializations(updated.getSpecializations());
        response.setExpertiseAreas(updated.getExpertiseAreas());
        response.setTreatmentTypes(updated.getTreatmentTypes());
        response.setAvailability(updated.getAvailability());
        response.setBio(updated.getBio());

        //  Decode documents before sending
        if (updated.getDocuments() != null) {

            Documents docs = new Documents();

            if (updated.getDocuments().getLicenseCertificate() != null) {
                docs.setLicenseCertificate(
                        new String(java.util.Base64.getDecoder().decode(
                                updated.getDocuments().getLicenseCertificate()
                        ))
                );
            }

            if (updated.getDocuments().getDegreeCertificate() != null) {
                docs.setDegreeCertificate(
                        new String(java.util.Base64.getDecoder().decode(
                                updated.getDocuments().getDegreeCertificate()
                        ))
                );
            }

            if (updated.getDocuments().getProfilePhoto() != null) {
                docs.setProfilePhoto(
                        new String(java.util.Base64.getDecoder().decode(
                                updated.getDocuments().getProfilePhoto()
                        ))
                );
            }

            response.setDocuments(docs);
        }

        response.setLanguages(updated.getLanguages());
        response.setRole(updated.getRole());
        response.setPhysioType(updated.getPhysioType());

        return ResponseStructure.buildResponse(
                response,
                "Therapist updated successfully",
                HttpStatus.OK,
                200);
    }

    // ================= DELETEBY THERPIST ID =================
    @Override
    public ResponseStructure<String> deleteBytherapistId(String therapistId) {

        repository.deleteByTherapistId(therapistId);

        return ResponseStructure.buildResponse(
                therapistId,
                "Deleted successfully",
                HttpStatus.OK,
                200);
    }

    private Therapist mapToEntity(TherapistDTO dto) {

        Therapist entity = new Therapist();

        entity.setClinicId(dto.getClinicId());
        entity.setBranchId(dto.getBranchId());
        entity.setFullName(dto.getFullName());
        entity.setContactNumber(dto.getContactNumber());
        entity.setGender(dto.getGender());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setQualification(dto.getQualification());
        entity.setYearsOfExperience(dto.getYearsOfExperience());
        entity.setServices(dto.getServices());
        entity.setSpecializations(dto.getSpecializations());
        entity.setExpertiseAreas(dto.getExpertiseAreas());
        entity.setTreatmentTypes(dto.getTreatmentTypes());
        entity.setAvailability(dto.getAvailability());
        entity.setBio(dto.getBio());
        entity.setUserName(dto.getUserName());
        entity.setPassword(dto.getPassword());

        // ================= BASE64 ENCODE =================
        if (dto.getDocuments() != null) {

            Documents docs = new Documents();

            if (dto.getDocuments().getLicenseCertificate() != null) {
                docs.setLicenseCertificate(
                    java.util.Base64.getEncoder().encodeToString(
                        dto.getDocuments().getLicenseCertificate().getBytes()
                    )
                );
            }

            if (dto.getDocuments().getDegreeCertificate() != null) {
                docs.setDegreeCertificate(
                    java.util.Base64.getEncoder().encodeToString(
                        dto.getDocuments().getDegreeCertificate().getBytes()
                    )
                );
            }

            if (dto.getDocuments().getProfilePhoto() != null) {
                docs.setProfilePhoto(
                    java.util.Base64.getEncoder().encodeToString(
                        dto.getDocuments().getProfilePhoto().getBytes()
                    )
                );
            }

            entity.setDocuments(docs);
        }

        entity.setLanguages(dto.getLanguages());
        entity.setRole(dto.getRole());
        entity.setPhysioType(dto.getPhysioType());

        return entity;
    }
    private TherapistDTO mapToDTO(Therapist entity) {

        TherapistDTO dto = new TherapistDTO();

        dto.setTherapistId(entity.getTherapistId());
        dto.setClinicId(entity.getClinicId());
        dto.setBranchId(entity.getBranchId());
        dto.setFullName(entity.getFullName());
        dto.setContactNumber(entity.getContactNumber());
        dto.setGender(entity.getGender());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setQualification(entity.getQualification());
        dto.setYearsOfExperience(entity.getYearsOfExperience());
        dto.setServices(entity.getServices());
        dto.setSpecializations(entity.getSpecializations());
        dto.setExpertiseAreas(entity.getExpertiseAreas());
        dto.setTreatmentTypes(entity.getTreatmentTypes());
        dto.setAvailability(entity.getAvailability());
        dto.setBio(entity.getBio());
        dto.setUserName(entity.getUserName());
        dto.setPassword(entity.getPassword());

        // ================= BASE64 DECODE =================
        if (entity.getDocuments() != null) {

            Documents docs = new Documents();

            if (entity.getDocuments().getLicenseCertificate() != null) {
                docs.setLicenseCertificate(
                    new String(java.util.Base64.getDecoder().decode(
                        entity.getDocuments().getLicenseCertificate()
                    ))
                );
            }

            if (entity.getDocuments().getDegreeCertificate() != null) {
                docs.setDegreeCertificate(
                    new String(java.util.Base64.getDecoder().decode(
                        entity.getDocuments().getDegreeCertificate()
                    ))
                );
            }

            if (entity.getDocuments().getProfilePhoto() != null) {
                docs.setProfilePhoto(
                    new String(java.util.Base64.getDecoder().decode(
                        entity.getDocuments().getProfilePhoto()
                    ))
                );
            }

            dto.setDocuments(docs);
        }

        dto.setLanguages(entity.getLanguages());
        dto.setRole(entity.getRole());
        dto.setPhysioType(entity.getPhysioType());

        return dto;
    }

    // ================= HELPERS =================
    private String generateTherapistId() {
        return "THER-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
