package com.clinicadmin.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "physiotherapydoctor-service")
public interface PhysiotherapyFeignClient {

    @PutMapping("/api/physiotherapy-doctor/updateSessionFromTherapist/{therapistRecordId}/{sessionId}")
    void updateSessionStatus(
            @PathVariable String therapistRecordId,
            @PathVariable String sessionId);
}
