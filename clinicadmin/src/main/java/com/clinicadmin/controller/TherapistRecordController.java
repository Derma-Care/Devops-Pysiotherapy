package com.clinicadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.TherapistRecordDTO;
import com.clinicadmin.service.TherapistRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clinic-admin")
@RequiredArgsConstructor
public class TherapistRecordController {
	
@Autowired
    private  TherapistRecordService service;

    //  POST API
    @PostMapping("/saveRecord")
    public ResponseEntity<ResponseStructure<TherapistRecordDTO>> saveRecord(
            @RequestBody TherapistRecordDTO dto) {

        ResponseStructure<TherapistRecordDTO> response = service.saveRecord(dto);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }

    //  GET API 
    @GetMapping("/getRecordByClinicIdBranchIdAndtherapistRecordId/{clinicId}/{branchId}/{therapistRecordId}")
    public ResponseEntity<ResponseStructure<TherapistRecordDTO>> getRecordByClinicIdBranchIdAndtherapistRecordId(
            @PathVariable String clinicId,
            @PathVariable String branchId,
            @PathVariable String therapistRecordId) {

        ResponseStructure<TherapistRecordDTO> response =
                service.getByIds(clinicId, branchId, therapistRecordId);

        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }
}