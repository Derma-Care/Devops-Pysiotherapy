package com.clinicadmin.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.ProgramWithTherophy;
import com.clinicadmin.dto.Response;
import com.clinicadmin.dto.TheraphyNamesDTO;
import com.clinicadmin.dto.TheraphyProgramWithTheraphyNamesDto;
import com.clinicadmin.dto.TherapyServiceDTO;
import com.clinicadmin.dto.TherophyProgramsDTO;
import com.clinicadmin.entity.TherophyProgramEntity;
import com.clinicadmin.repository.TherophyProgramRepository;
import com.clinicadmin.service.TherophyProgramService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TherophyProgramServiceImpl implements TherophyProgramService {

    private final TherophyProgramRepository repository;
    
    private final TherapyServiceServiceImpl therapyServiceServiceImpl;

    private TherophyProgramEntity mapToEntity(TherophyProgramsDTO dto) {
        return new TherophyProgramEntity(
                dto.getId(),
                dto.getProgramName(),
                dto.getTherophyIds(),
                dto.getClinicId(),
                dto.getBranchId()
        );
    }

    private TherophyProgramsDTO mapToDTO(TherophyProgramEntity entity) {
        return new TherophyProgramsDTO(
                entity.getId(),
                entity.getProgramName(),
                entity.getTherophyIds(),
                entity.getClinicId(),
                entity.getBranchId()
        );
    }
    

    @Override
    public ResponseEntity<Response> create(TherophyProgramsDTO dto) {
        try {
            TherophyProgramEntity saved = repository.save(mapToEntity(dto));

            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .data(mapToDTO(saved))
                            .message("Program created successfully")
                            .status(200)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error creating program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<Response> getById(String id) {
        try {
            TherophyProgramEntity entity = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Program not found"));

            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .data(mapToDTO(entity))
                            .message("Program fetched successfully")
                            .status(200)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(404)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error fetching program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }
    
    @Override
    public ResponseEntity<Response> getByclinicAndBranchIdAndId(String cid,String bid,String id) {
        try {
            TherophyProgramEntity entity = repository.findByClinicIdAndBranchIdAndId(cid, bid, id);
              List<TherapyServiceDTO> lst = new ArrayList<>();
              if(entity != null) {
            	   for(String s:entity.getTherophyIds()) {
            		   TherapyServiceDTO thry = therapyServiceServiceImpl.getTherapyWithExercisesWithId(s);
            		   lst.add(thry);
            	   }}
           ProgramWithTherophy programWithTherophy = new ProgramWithTherophy();
           programWithTherophy.setBranchId(entity.getBranchId());
           programWithTherophy.setClinicId(entity.getClinicId());
           programWithTherophy.setId(entity.getId());
           programWithTherophy.setProgramName(entity.getProgramName());
           programWithTherophy.setTherophyData(lst);
           programWithTherophy.setTotalTherophyIds(lst.size());
            
            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .data(programWithTherophy)
                            .message("Program fetched successfully")
                            .status(200)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(404)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error fetching program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<Response> getByclinicAndBranchId(String cid,String bid) {
        try {
            List<TherophyProgramEntity> entity = repository.findByClinicIdAndBranchId(cid, bid);
              List<TheraphyNamesDTO> lst = new LinkedList<>();
              List<TheraphyProgramWithTheraphyNamesDto> theraphyProgramWithTheraphyNamesDto = new LinkedList<>();
              TheraphyProgramWithTheraphyNamesDto theraphyDto = new TheraphyProgramWithTheraphyNamesDto();
              if(entity != null) {
            	  for(TherophyProgramEntity e : entity) {
            	   for(String s:e.getTherophyIds()) {
            		   TherapyServiceDTO thry = therapyServiceServiceImpl.getById(s);
            		   if(thry != null) {
            		   TheraphyNamesDTO dto = new TheraphyNamesDTO();
            		   dto.setTheraphyId(s);
            		   dto.setTheraphyName(thry.getTherapyName());
            		   lst.add(dto);}
            	   }theraphyDto.setBranchId(e.getBranchId());
            	   theraphyDto.setClinicId(e.getClinicId());
            	   theraphyDto.setId(e.getId());
            	   theraphyDto.setProgramName(e.getProgramName());
            	   theraphyDto.setTherophy(lst);
            	   theraphyProgramWithTheraphyNamesDto.add(theraphyDto);
            	   }}
            if(theraphyProgramWithTheraphyNamesDto != null || !theraphyProgramWithTheraphyNamesDto.isEmpty()) {
            	 return ResponseEntity.ok(
                         Response.builder()
                                 .success(true)
                                 .data(theraphyProgramWithTheraphyNamesDto)
                                 .message("Program fetched successfully")
                                 .status(200)
                                 .build()
                 );
            }
            return ResponseEntity.ok(
                    Response.builder()
                    .success(false)
                    .data(null)
                    .message("Programs not found")
                    .status(404)
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(404)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error fetching program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<Response> getAll() {
        try {
            List<TherophyProgramsDTO> list = repository.findAll()
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .data(list)
                            .message("All programs fetched")
                            .status(200)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error fetching programs: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<Response> update(String id, TherophyProgramsDTO dto) {
        try {
            TherophyProgramEntity existing = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Program not found"));

            existing.setProgramName(dto.getProgramName());
            existing.setTherophyIds(dto.getTherophyIds());
            existing.setClinicId(dto.getClinicId());
            existing.setBranchId(dto.getBranchId());

            TherophyProgramEntity updated = repository.save(existing);

            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .data(mapToDTO(updated))
                            .message("Program updated successfully")
                            .status(200)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(404)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error updating program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<Response> delete(String id) {
        try {
            if (!repository.existsById(id)) {
                throw new RuntimeException("Program not found");
            }

            repository.deleteById(id);

            return ResponseEntity.ok(
                    Response.builder()
                            .success(true)
                            .message("Program deleted successfully")
                            .status(200)
                            .build()
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Response.builder()
                            .success(false)
                            .message(e.getMessage())
                            .status(404)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .success(false)
                            .message("Error deleting program: " + e.getMessage())
                            .status(500)
                            .build()
            );
        }
    }}