package com.clinicadmin.controller;

import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.TherapyServiceDTO;
import com.clinicadmin.service.TherapyServiceService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clinic-admin")
@RequiredArgsConstructor
public class TherapyServiceController {

    private final TherapyServiceService service;

    // ✅ CREATE
    @PostMapping("/createTherapistService")
    public ResponseEntity<Response> createTherapistService(@RequestBody TherapyServiceDTO dto) {
        Response res = service.createTherapy(dto);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // ✅ GET by clinicId + branchId
    @GetMapping("/getByTherapistServiceClinicIdAndBranchId/{clinicId}/{branchId}")
    public ResponseEntity<Response> getByClinicIdBranchId(
            @PathVariable String clinicId,
            @PathVariable String branchId) {

        Response res = service.getByClinicAndBranch(clinicId, branchId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // ✅ GET by id + clinicId + branchId
    @GetMapping("/getByTherapistServiceIdClinicIdAndBranchId/{id}/{clinicId}/{branchId}")
    public ResponseEntity<Response> getByClinicIdAndBranchId(
            @PathVariable String id,
            @PathVariable String clinicId,
            @PathVariable String branchId) {

        Response res = service.getByIdClinicBranch(id, clinicId, branchId);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // ✅ UPDATE 
    @PutMapping("/updateByTherapistServieId/{id}")
    public ResponseEntity<Response> updateByTherapistServieId(
            @PathVariable String id,
            @RequestBody TherapyServiceDTO dto) {

        Response res = service.updateTherapyById(id, dto);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    // ✅ DELETE 
    @DeleteMapping("/deleteByTherapistServiceId/{id}")
    public ResponseEntity<Response> deleteByTherapistServiceId(
            @PathVariable String id) {

        Response res = service.deleteTherapyById(id);
        return ResponseEntity.status(res.getStatus()).body(res);
    }
}