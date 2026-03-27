package com.clinicadmin.dto;

import lombok.Data;

@Data
public class TherapyExercisesDTO {

    private String therapyExercisesId;

    private String clinicId;
    private String branchId;

    private String name;
    private String video;
    private String image;
    private String session;
    private String duration;
    private String frequency;
    private String notes;
}