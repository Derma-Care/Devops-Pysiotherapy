package com.clinicadmin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.clinicadmin.entity.TherophyProgramEntity;

public interface TherophyProgramRepository extends MongoRepository<TherophyProgramEntity, String> {

	TherophyProgramEntity findByClinicIdAndBranchIdAndId(String cid,String bid,String id);
}