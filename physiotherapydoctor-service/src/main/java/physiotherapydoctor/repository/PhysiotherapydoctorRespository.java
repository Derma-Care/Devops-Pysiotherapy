package physiotherapydoctor.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import physiotherapydoctor.entity.PhysiotherapyRecord;

@Repository
public interface PhysiotherapydoctorRespository extends MongoRepository<PhysiotherapyRecord, String> {
//	List<PhysiotherapyRecord> findByTreatmentPlanTheraphyId(String theraphyId);
//	List<PhysiotherapyRecord> findByClinicIdAndBranchIdAndTreatmentPlanTheraphyId(
//	        String clinicId,
//	        String branchId,
//	        String therapistId
//	);
	List<PhysiotherapyRecord> findByClinicIdAndBranchIdAndTreatmentPlanTherapistId(String clinicId, String branchId,
			String therapistId);
}
