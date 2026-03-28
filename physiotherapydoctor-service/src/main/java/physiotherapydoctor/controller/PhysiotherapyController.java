package physiotherapydoctor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import physiotherapydoctor.dto.PhysiotherapyRecordDTO;
import physiotherapydoctor.dto.Response;
import physiotherapydoctor.service.PhysiotherapyService;

@RestController
@RequestMapping("/physiotherapy-doctor")
@RequiredArgsConstructor
public class PhysiotherapyController {

	private final PhysiotherapyService service;

	// ✅ CREATE
	@PostMapping("/physiotherapy-record/create")
	public ResponseEntity<Response> create(@RequestBody PhysiotherapyRecordDTO dto) {

		Response response = service.create(dto);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	// ✅ GET BY ID
	@GetMapping("/physiotherapy-recordgetById/{id}")
	public ResponseEntity<Response> getById(@PathVariable String id) {

		Response response = service.getById(id);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	// ✅ GET ALL
	@GetMapping("/physiotherapy-record/getAll")
	public ResponseEntity<Response> getAll() {

		Response response = service.getAll();
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	// ✅ UPDATE
	@PutMapping("/physiotherapy-record/updateById/{id}")
	public ResponseEntity<Response> update(@PathVariable String id, @RequestBody PhysiotherapyRecordDTO dto) {

		Response response = service.update(id, dto);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	// ✅ DELETE
	@DeleteMapping("/physiotherapy-record/deleteById/{id}")
	public ResponseEntity<Response> delete(@PathVariable String id) {

		Response response = service.delete(id);
		return ResponseEntity.status(response.getStatus()).body(response);
	}
	@GetMapping("physiotherapy-record/dashboard/{clinicId}/{branchId}/{therapistId}")
	public ResponseEntity<Response> getDashboard(
	        @PathVariable String clinicId,
	        @PathVariable String branchId,
	        @PathVariable String therapistId) {

	    Response response = service.getTherapistDashboard(clinicId, branchId, therapistId);
	    return ResponseEntity.status(response.getStatus()).body(response);
	}
}
