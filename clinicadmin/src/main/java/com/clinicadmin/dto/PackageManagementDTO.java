package com.clinicadmin.dto;

import java.util.List;

import lombok.Data;

@Data
public class PackageManagementDTO {
	 
	    
	private String packageId;
	
    private String clinicId;
    
    private String branchId;

    private String packageName;

    private List<String> programs;

    private double discountPercentage;

    private String startOfferDate;

    private String endOfferDate;

    private String offerType;

    private int noOfPrograms;		
	}
