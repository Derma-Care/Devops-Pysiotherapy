package physiotherapydoctor.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import physiotherapydoctor.dto.PhysiotherapyRecordDTO;
import physiotherapydoctor.dto.Response;
import physiotherapydoctor.entity.PhysiotherapyRecord;
import physiotherapydoctor.repository.PhysiotherapydoctorRespository;
import physiotherapydoctor.service.PhysiotherapyService;

@Service
@RequiredArgsConstructor
public class PhysiotherapyServiceImpl implements PhysiotherapyService {

	private final PhysiotherapydoctorRespository repository;

	// ✅ CREATE
	@Override
	public Response create(PhysiotherapyRecordDTO dto) {

		Response response = new Response();

		if (dto == null) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("Request body is null");
			response.setStatus(400);
			return response;
		}

		PhysiotherapyRecord saved = repository.save(mapToEntity(dto));

		response.setSuccess(true);
		response.setData(saved);
		response.setMessage("Record created successfully");
		response.setStatus(201);

		return response;
	}

	// ✅ GET BY ID
	@Override
	public Response getById(String id) {

		Response response = new Response();

		if (id == null || id.isEmpty()) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("ID is required");
			response.setStatus(400);
			return response;
		}

		Optional<PhysiotherapyRecord> optional = repository.findById(id);

		if (optional.isEmpty()) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("Record not found");
			response.setStatus(404);
			return response;
		}

		response.setSuccess(true);
		response.setData(optional.get());
		response.setMessage("Success");
		response.setStatus(200);

		return response;
	}

	// ✅ GET ALL
	@Override
	public Response getAll() {

		Response response = new Response();

		List<PhysiotherapyRecord> list = repository.findAll();

		if (list.isEmpty()) {
			response.setSuccess(false);
			response.setData(list);
			response.setMessage("No records found");
			response.setStatus(204);
			return response;
		}

		response.setSuccess(true);
		response.setData(list);
		response.setMessage("Success");
		response.setStatus(200);

		return response;
	}

	// ✅ UPDATE
	@Override
	public Response update(String id, PhysiotherapyRecordDTO dto) {

		Response response = new Response();

		if (id == null || id.isEmpty()) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("ID is required");
			response.setStatus(400);
			return response;
		}

		if (dto == null) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("Request body is null");
			response.setStatus(400);
			return response;
		}

		Optional<PhysiotherapyRecord> optional = repository.findById(id);

		if (optional.isEmpty()) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("Record not found");
			response.setStatus(404);
			return response;
		}

		PhysiotherapyRecord existing = optional.get();

		// 🔥 NULL SAFE UPDATE
		if (dto.getAssessment() != null) {
			existing.setAssessment(dto.getAssessment());
		}

		if (dto.getDiagnosis() != null) {
			existing.setDiagnosis(dto.getDiagnosis());
		}

		if (dto.getTreatmentPlan() != null) {
			existing.setTreatmentPlan(dto.getTreatmentPlan());
		}

		if (dto.getTherapySessions() != null) {
			existing.setTherapySessions(dto.getTherapySessions());
		}

		if (dto.getExercisePlan() != null) {
			existing.setExercisePlan(dto.getExercisePlan());
		}

		if (dto.getProgressNotes() != null) {
			existing.setProgressNotes(dto.getProgressNotes());
		}

		if (dto.getFollowUp() != null) {
			existing.setFollowUp(dto.getFollowUp());
		}

		if (dto.getProgressAnalytics() != null) {
			existing.setProgressAnalytics(dto.getProgressAnalytics());
		}

		if (dto.getTreatmentTemplates() != null) {
			existing.setTreatmentTemplates(dto.getTreatmentTemplates());
		}

		PhysiotherapyRecord updated = repository.save(existing);

		response.setSuccess(true);
		response.setData(updated);
		response.setMessage("Updated successfully");
		response.setStatus(200);

		return response;
	}

	// ✅ DELETE
	@Override
	public Response delete(String id) {

		Response response = new Response();

		if (id == null || id.isEmpty()) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("ID is required");
			response.setStatus(400);
			return response;
		}

		if (!repository.existsById(id)) {
			response.setSuccess(false);
			response.setData(null);
			response.setMessage("Record not found");
			response.setStatus(404);
			return response;
		}

		repository.deleteById(id);

		response.setSuccess(true);
		response.setData(null);
		response.setMessage("Deleted successfully");
		response.setStatus(200);

		return response;
	}

	// ---------------- MAPPER ----------------
	private PhysiotherapyRecord mapToEntity(PhysiotherapyRecordDTO dto) {

		PhysiotherapyRecord entity = new PhysiotherapyRecord();

		entity.setAssessment(dto.getAssessment());
		entity.setDiagnosis(dto.getDiagnosis());
		entity.setTreatmentPlan(dto.getTreatmentPlan());
		entity.setTherapySessions(dto.getTherapySessions());
		entity.setExercisePlan(dto.getExercisePlan());
		entity.setProgressNotes(dto.getProgressNotes());
		entity.setFollowUp(dto.getFollowUp());
		entity.setProgressAnalytics(dto.getProgressAnalytics());
		entity.setTreatmentTemplates(dto.getTreatmentTemplates());

		return entity;
	}
}
