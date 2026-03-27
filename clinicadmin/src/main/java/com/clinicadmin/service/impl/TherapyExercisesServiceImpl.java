package com.clinicadmin.service.impl;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.TherapyExercisesDTO;
import com.clinicadmin.entity.TherapyExercises;
import com.clinicadmin.repository.TherapyExercisesRepository;
import com.clinicadmin.service.TherapyExercisesService;

@Service
public class TherapyExercisesServiceImpl implements TherapyExercisesService {

    @Autowired
    private TherapyExercisesRepository repository;


    // ================= CREATE =================
    @Override
    public ResponseStructure<TherapyExercisesDTO> createTherapyExercises(TherapyExercisesDTO dto) {

        TherapyExercises entity = toEntity(dto);

        entity.setTherapyExercisesId(generateUniqueId());

        TherapyExercises saved = repository.save(entity);

        return ResponseStructure.buildResponse(
                toDTO(saved),
                "Therapy Exercise Created Successfully",
                HttpStatus.CREATED,
                201
        );
    }

   
    // ================= GET BY ID =================
    @Override
    public ResponseStructure<TherapyExercisesDTO> getTherapyExercisesById(
            String therapyExercisesId) {

        TherapyExercises entity = repository.findByTherapyExercisesId(therapyExercisesId)
                .orElseThrow(() -> new RuntimeException("Therapy Exercise Not Found"));

        return ResponseStructure.buildResponse(
                toDTO(entity),
                "Fetched Successfully",
                HttpStatus.OK,
                200
        );
    }

    // ================= GET BY clinicId + branchId =================
    @Override
    public ResponseStructure<List<TherapyExercisesDTO>> getByClinicIdAndBranchId(
            String clinicId, String branchId) {

        List<TherapyExercisesDTO> list = repository
                .findByClinicIdAndBranchId(clinicId, branchId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseStructure.buildResponse(
                list,
                "Fetched Successfully",
                HttpStatus.OK,
                200
        );
    }

    
    
    @Override
    public ResponseStructure<TherapyExercisesDTO> updateTherapyExercisesById(
            String therapyExercisesId, TherapyExercisesDTO dto) {

        TherapyExercises entity = repository.findByTherapyExercisesId(therapyExercisesId)
                .orElseThrow(() -> new RuntimeException("Therapy Exercise Not Found"));

        // ✅ Update fields
        if (dto.getClinicId() != null)
            entity.setClinicId(dto.getClinicId());

        if (dto.getBranchId() != null)
            entity.setBranchId(dto.getBranchId());

        if (dto.getName() != null)
            entity.setName(dto.getName());

        // ✅ Encode before saving
        if (dto.getVideo() != null)
            entity.setVideo(encode(dto.getVideo()));

        if (dto.getImage() != null)
            entity.setImage(encode(dto.getImage()));

        if (dto.getSession() != null)
            entity.setSession(dto.getSession());

        if (dto.getDuration() != null)
            entity.setDuration(dto.getDuration());

        if (dto.getFrequency() != null)
            entity.setFrequency(dto.getFrequency());

        if (dto.getNotes() != null)
            entity.setNotes(dto.getNotes());

        TherapyExercises updated = repository.save(entity);

        // ✅ Decode before sending response
        TherapyExercisesDTO responseDto = new TherapyExercisesDTO();

        responseDto.setTherapyExercisesId(updated.getTherapyExercisesId());
        responseDto.setClinicId(updated.getClinicId());
        responseDto.setBranchId(updated.getBranchId());
        responseDto.setName(updated.getName());

        responseDto.setVideo(decode(updated.getVideo()));   // ✅ decode
        responseDto.setImage(decode(updated.getImage()));   // ✅ decode

        responseDto.setSession(updated.getSession());
        responseDto.setDuration(updated.getDuration());
        responseDto.setFrequency(updated.getFrequency());
        responseDto.setNotes(updated.getNotes());

        return ResponseStructure.buildResponse(
                responseDto,
                "Updated Successfully",
                HttpStatus.OK,
                200
        );
    }
    // ================= GET BY clinicId + branchId + therapyExercisesId =================
    @Override
    public ResponseStructure<TherapyExercisesDTO> getByClinicIdBranchIdAndTherapyId(
            String clinicId, String branchId, String therapyExercisesId) {

        TherapyExercises entity = repository
                .findByClinicIdAndBranchIdAndTherapyExercisesId(
                        clinicId, branchId, therapyExercisesId)
                .orElseThrow(() -> new RuntimeException("Therapy Exercise Not Found"));

        return ResponseStructure.buildResponse(
                toDTO(entity),
                "Fetched Successfully",
                HttpStatus.OK,
                200
        );
    }

    // ================= DELETE =================
    @Override
    public ResponseStructure<String> deleteTherapyExercisesById(
            String therapyExercisesId) {

        TherapyExercises entity = repository.findByTherapyExercisesId(therapyExercisesId)
                .orElseThrow(() -> new RuntimeException("Therapy Exercise Not Found"));

        repository.delete(entity);

        return ResponseStructure.buildResponse(
                "Deleted Successfully",
                "Deleted",
                HttpStatus.OK,
                200
        );
    }
 // ================= CUSTOM ID =================
    private String generateCustomId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return "THER-" + sb.toString();
    }

    private String generateUniqueId() {
        String id;
        do {
            id = generateCustomId();
        } while (repository.existsByTherapyExercisesId(id));
        return id;
    }

    // ================= DTO → ENTITY =================
    private TherapyExercises toEntity(TherapyExercisesDTO dto) {
        TherapyExercises e = new TherapyExercises();

        e.setClinicId(dto.getClinicId());
        e.setBranchId(dto.getBranchId());
        e.setName(dto.getName());

        // ✅ Encode before saving
        e.setVideo(encode(dto.getVideo()));
        e.setImage(encode(dto.getImage()));

        e.setSession(dto.getSession());
        e.setDuration(dto.getDuration());
        e.setFrequency(dto.getFrequency());
        e.setNotes(dto.getNotes());

        return e;
    }

    // ================= ENTITY → DTO =================
    private TherapyExercisesDTO toDTO(TherapyExercises e) {
        TherapyExercisesDTO dto = new TherapyExercisesDTO();

        dto.setTherapyExercisesId(e.getTherapyExercisesId());
        dto.setClinicId(e.getClinicId()); // ⚠️ you missed this earlier
        dto.setBranchId(e.getBranchId());
        dto.setName(e.getName());

        // ✅ Decode before sending response
        dto.setVideo(decode(e.getVideo()));
        dto.setImage(decode(e.getImage()));

        dto.setSession(e.getSession());
        dto.setDuration(e.getDuration());
        dto.setFrequency(e.getFrequency());
        dto.setNotes(e.getNotes());

        return dto;
    }
    private String encode(String value) {
        if (value == null) return null;
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    private String decode(String value) {
        if (value == null) return null;
        return new String(Base64.getDecoder().decode(value));
    }

}