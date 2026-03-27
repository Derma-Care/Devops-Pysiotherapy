package physiotherapydoctor.service;

import physiotherapydoctor.dto.PhysiotherapyRecordDTO;
import physiotherapydoctor.dto.Response;

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
}