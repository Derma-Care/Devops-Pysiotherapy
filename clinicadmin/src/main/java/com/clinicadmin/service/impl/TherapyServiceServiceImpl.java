package com.clinicadmin.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.TherapyServiceDTO;
import com.clinicadmin.entity.TherapyService;
import com.clinicadmin.repository.TherapyServiceRepository;
import com.clinicadmin.service.TherapyServiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TherapyServiceServiceImpl implements TherapyServiceService {
	
    @Autowired
    private  TherapyServiceRepository repository;

    //  CREATETHERAPY
    @Override
    public Response createTherapy(TherapyServiceDTO dto) {

        TherapyService therapy = mapToEntity(dto);
        TherapyService saved = repository.save(therapy);

        Response response = new Response();
        response.setSuccess(true);
        response.setData(mapToDTO(saved));
        response.setMessage("Created successfully");
        response.setStatus(HttpStatus.CREATED.value());

        return response;
    }

    //  GET BY clinicId + branchId
    @Override
    public Response getByClinicAndBranch(String clinicId, String branchId) {

        List<TherapyService> list = repository.findByClinicIdAndBranchId(clinicId, branchId);

        List<TherapyServiceDTO> dtos = list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        Response response = new Response();
        response.setSuccess(true);
        response.setData(dtos);
        response.setMessage("Fetched successfully");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    //  GET BY id + clinicId + branchId
    @Override
    public Response getByIdClinicBranch(String id, String clinicId, String branchId) {

        Optional<TherapyService> optional =
                repository.findByIdAndClinicIdAndBranchId(id, clinicId, branchId);

        Response response = new Response();

        if (optional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Data not found");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return response;
        }

        response.setSuccess(true);
        response.setData(mapToDTO(optional.get()));
        response.setMessage("Fetched successfully");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

 //  UPDATE BY ID ONLY
    @Override
    public Response updateTherapyById(String id, TherapyServiceDTO dto) {

        Optional<TherapyService> optional = repository.findById(id);

        Response response = new Response();

        if (optional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Data not found");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return response;
        }

        TherapyService existing = optional.get();

        // reuse update logic
        updateEntityFromDTO(existing, dto);

        TherapyService updated = repository.save(existing);

        response.setSuccess(true);
        response.setData(mapToDTO(updated));
        response.setMessage("Updated successfully");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    // ✅ DELETE
    @Override
    public Response deleteTherapyById(String id) {

        Optional<TherapyService> optional = repository.findById(id);

        Response response = new Response();

        if (optional.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Data not found");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return response;
        }

        repository.delete(optional.get());

        response.setSuccess(true);
        response.setMessage("Deleted successfully");
        response.setStatus(HttpStatus.OK.value());

        return response;
    }

    // ================== MAPPERS ==================

    private TherapyService mapToEntity(TherapyServiceDTO dto) {
        TherapyService therapy = new TherapyService();

        therapy.setConsentType(dto.getConsentType());
        therapy.setExerciseIds(dto.getExerciseIds());
        therapy.setTherapyName(dto.getTherapyName());
        therapy.setClinicId(dto.getClinicId());
        therapy.setBranchId(dto.getBranchId());

        return therapy;
    }

    private TherapyServiceDTO mapToDTO(TherapyService therapy) {
        TherapyServiceDTO dto = new TherapyServiceDTO();

        dto.setConsentType(therapy.getConsentType());
        dto.setExerciseIds(therapy.getExerciseIds());
        dto.setTherapyName(therapy.getTherapyName());
        dto.setClinicId(therapy.getClinicId());
        dto.setBranchId(therapy.getBranchId());

        // ✅ AUTO COUNT LOGIC
        if (therapy.getExerciseIds() != null) {
            dto.setNoExerciseIdCount((therapy.getExerciseIds().size()));
        } else {
            dto.setNoExerciseIdCount(0);
        }

        return dto;
    }

    //  UPDATE SAFE METHOD
    private void updateEntityFromDTO(TherapyService entity, TherapyServiceDTO dto) {

        if (dto.getConsentType() != 0) {
            entity.setConsentType(dto.getConsentType());
        }

        if (dto.getExerciseIds() != null) {
            entity.setExerciseIds(dto.getExerciseIds());
        }

        if (dto.getTherapyName() != null) {
            entity.setTherapyName(dto.getTherapyName());
        }
    }
}