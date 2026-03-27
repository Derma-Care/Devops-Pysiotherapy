package com.clinicadmin.service;

import java.util.List;

import com.clinicadmin.dto.TherapistDTO;
import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.TherapistLoginDTO;
import com.clinicadmin.dto.TherapistLoginResponseDTO;

public interface TherapistService {

    ResponseStructure<TherapistDTO> createTherapist(TherapistDTO dto);

    ResponseStructure<TherapistLoginResponseDTO> login(TherapistLoginDTO dto);

    ResponseStructure<TherapistDTO> getBytherapistId(String therapistId);

    ResponseStructure<List<TherapistDTO>> getByClinicIdBranchIdAndTherapistId(
            String clinicId,
            String branchId,
            String therapistId
    );

    ResponseStructure<List<TherapistDTO>> getByClinicIdAndBranchId(
            String clinicId,
            String branchId
    );

    ResponseStructure<TherapistDTO> updateBytherapistId(String therapistId, TherapistDTO dto);

    ResponseStructure<String> deleteBytherapistId(String therapistId);
}