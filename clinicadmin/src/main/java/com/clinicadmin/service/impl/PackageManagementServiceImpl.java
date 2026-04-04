package com.clinicadmin.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.PackageManagementDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.entity.PackageManagement;
import com.clinicadmin.repository.PackageManagementRepository;
import com.clinicadmin.service.PackageManagementService;

@Service
public class PackageManagementServiceImpl implements PackageManagementService {

    @Autowired
    private PackageManagementRepository repository;

    // ✅ CREATE
    @Override
    public Response createPackage(PackageManagementDTO dto) {

        Response response = new Response();

        try {
            PackageManagement entity = mapToEntity(dto);
            entity.setPackageId(generatePackageId());

            PackageManagement saved = repository.save(entity);

            PackageManagementDTO responseDto = mapToDTO(saved);

            response.setSuccess(true);
            response.setData(responseDto);
            response.setMessage("Package created successfully");
            response.setStatus(HttpStatus.CREATED.value());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public Response getByClinicAndBranch(String clinicId, String branchId) {

        Response response = new Response();

        try {
            List<PackageManagement> list =
                    repository.findByClinicIdAndBranchId(clinicId, branchId);

            // ✅ Convert Entity List → DTO List
            List<PackageManagementDTO> dtoList = list.stream()
                    .map(this::mapToDTO)
                    .toList();

            response.setSuccess(true);
            response.setData(dtoList);
            response.setMessage("Packages fetched successfully");
            response.setStatus(HttpStatus.OK.value());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    // ✅ GET by clinicId + branchId + packageId
    @Override
    public Response getByClinicBranchAndPackageId(String clinicId, String branchId, String packageId) {

        Response response = new Response();

        try {
            Optional<PackageManagement> optional =
                    repository.findByClinicIdAndBranchIdAndPackageId(clinicId, branchId, packageId);

            if (optional.isPresent()) {

                PackageManagementDTO dto = mapToDTO(optional.get());

                response.setSuccess(true);
                response.setData(dto);
                response.setMessage("Package found");
                response.setStatus(HttpStatus.OK.value());

            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Package not found");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public Response updatePackage(String packageId, PackageManagementDTO dto) {

        Response response = new Response();

        try {
            Optional<PackageManagement> optional = repository.findByPackageId(packageId);

            if (optional.isPresent()) {

                PackageManagement entity = optional.get();

                // ✅ Use separate update mapper
                updateEntityFromDTO(entity, dto);

                PackageManagement updated = repository.save(entity);

                response.setSuccess(true);
                response.setData(mapToDTO(updated));
                response.setMessage("Package updated successfully");
                response.setStatus(HttpStatus.OK.value());

            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Package not found");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    // ✅ DELETE
    @Override
    public Response deletePackage(String packageId) {

        Response response = new Response();

        try {
            Optional<PackageManagement> optional = repository.findByPackageId(packageId);

            if (optional.isPresent()) {

                repository.delete(optional.get());

                response.setSuccess(true);
                response.setData(null);
                response.setMessage("Package deleted successfully");
                response.setStatus(HttpStatus.OK.value());

            } else {
                response.setSuccess(false);
                response.setData(null);
                response.setMessage("Package not found");
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    // ================= MAPPERS =================

    private PackageManagement mapToEntity(PackageManagementDTO dto) {

        PackageManagement entity = new PackageManagement();

        entity.setPackageName(dto.getPackageName());
        entity.setClinicId(dto.getClinicId());
        entity.setBranchId(dto.getBranchId());       
        entity.setPrograms(dto.getPrograms());

        // ✅ Apply discount logic
        double finalDiscount = applyDiscountLogic(
                dto.getStartOfferDate(),
                dto.getEndOfferDate(),
                dto.getDiscountPercentage()
        );

        entity.setDiscountPercentage(finalDiscount);

        entity.setStartOfferDate(dto.getStartOfferDate());
        entity.setEndOfferDate(dto.getEndOfferDate());
        entity.setOfferType(dto.getOfferType());

        return entity;
    }

    private PackageManagementDTO mapToDTO(PackageManagement entity) {

        PackageManagementDTO dto = new PackageManagementDTO();

        dto.setPackageId(entity.getPackageId());
        dto.setClinicId(entity.getClinicId());
        dto.setBranchId(entity.getBranchId());
        dto.setPackageName(entity.getPackageName());
        dto.setPrograms(entity.getPrograms());

        // ✅ IMPORTANT LINE (Missing in your case)
        dto.setNoOfPrograms(
            entity.getPrograms() != null ? entity.getPrograms().size() : 0
        );

        dto.setDiscountPercentage(entity.getDiscountPercentage());
        dto.setStartOfferDate(entity.getStartOfferDate());
        dto.setEndOfferDate(entity.getEndOfferDate());
        dto.setOfferType(entity.getOfferType());

        return dto;
    }

    // ================= BUSINESS LOGIC =================

    private double applyDiscountLogic(String startDate, String endDate, double discount) {

        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);
        LocalDate today = LocalDate.now();

        if (today.isBefore(start) || today.isAfter(end)) {
            return 0.0;
        }

        return discount;
    }

    private LocalDate parseDate(String dateStr) {

        String[] formats = {
                "yyyy-MM-dd",
                "dd-MM-yyyy",
                "MM-dd-yyyy",
                "yyyy/MM/dd",
                "dd/MM/yyyy"
        };

        for (String format : formats) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
            } catch (DateTimeParseException e) {
                // try next
            }
        }

        throw new RuntimeException("Invalid date format: " + dateStr);
    }

    private String generatePackageId() {
        return "PKG-" + System.currentTimeMillis();
    }
    private void updateEntityFromDTO(PackageManagement entity, PackageManagementDTO dto) {

        // ✅ Update only required fields
        if (dto.getPackageName() != null) {
            entity.setPackageName(dto.getPackageName());
        }

        if (dto.getPrograms() != null) {
            entity.setPrograms(dto.getPrograms());
        }

        if (dto.getStartOfferDate() != null) {
            entity.setStartOfferDate(dto.getStartOfferDate());
        }

        if (dto.getEndOfferDate() != null) {
            entity.setEndOfferDate(dto.getEndOfferDate());
        }

        if (dto.getOfferType() != null) {
            entity.setOfferType(dto.getOfferType());
        }

        // ✅ Apply discount logic ONLY when needed
        if (dto.getDiscountPercentage() != 0 ||
            dto.getStartOfferDate() != null ||
            dto.getEndOfferDate() != null) {

            double finalDiscount = applyDiscountLogic(
                    entity.getStartOfferDate(),
                    entity.getEndOfferDate(),
                    dto.getDiscountPercentage()
            );

            entity.setDiscountPercentage(finalDiscount);
        }
    }
}