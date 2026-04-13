package physiotherapydoctor.service;

import java.util.List;

import physiotherapydoctor.dto.PhysiotherapyRecordDTO;
import physiotherapydoctor.dto.Response;
import physiotherapydoctor.entity.PhysiotherapyRecord;

public interface PhysiotherapyService {

    // CREATE
    Response create(PhysiotherapyRecordDTO dto);

    // GET BY ID
    Response getById(String id);

    // GET ALL
    Response getAll();

    // UPDATE
    Response update(String id, PhysiotherapyRecordDTO dto);

    // DELETE
    Response delete(String id);

  
	Response getTherapistDashboard(String clinicId, String branchId, String therapistId);

	void updateSessionStatusFromTherapist(String therapistRecordId, String sessionId);
}