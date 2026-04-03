package com.clinicadmin.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapyServiceDTO {

    private int consentType;
    private List<String> exerciseIds;
    private String therapyName;
    private String clinicId;
    private String branchId;
    private int noExerciseIdCount;

}