package physiotherapydoctor.serviceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import physiotherapydoctor.dto.BookingResponse;
import physiotherapydoctor.dto.PhysiotherapyRecordDTO;
import physiotherapydoctor.dto.Response;
import physiotherapydoctor.dto.ResponseStructure;
import physiotherapydoctor.dto.TherapistDashboardResponse;
import physiotherapydoctor.dto.TherapySession;
import physiotherapydoctor.entity.PhysiotherapyRecord;
import physiotherapydoctor.feign.BookingFeign;
import physiotherapydoctor.repository.PhysiotherapydoctorRespository;
import physiotherapydoctor.service.PhysiotherapyService;

@Service
@RequiredArgsConstructor
public class PhysiotherapyServiceImpl implements PhysiotherapyService {

	private final PhysiotherapydoctorRespository repository;
	
	@Autowired
	private  BookingFeign bookingFeign; 


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
	
		    PhysiotherapyRecord dtoData = mapToEntity(dto);
	
		    // ✅ VERY IMPORTANT (FIX)
		    dtoData.setTherapistRecordId(dto.getTherapistRecordId());
	
		    // ✅ Generate session IDs
		    generateSessionIds(dtoData.getTherapySessions());
	
		    // ✅ Set initial session status
		    if (dtoData.getTherapySessions() != null) {
		        for (TherapySession s : dtoData.getTherapySessions()) {
		            s.setStatus("Pending");
		        }
		    }
	
		    // ✅ Set overall status
		    dtoData.setOverallStatus("Pending");
	
		    dtoData.setCreatedAt(dto.getCreatedAt());
	
		    PhysiotherapyRecord saved = repository.save(dtoData);
		    
		    
		    if (dto.getBookingId() != null && !dto.getBookingId().isEmpty()) {
	
		        try {
		            ResponseStructure<BookingResponse> res =
		                    bookingFeign.getBookingById(dto.getBookingId());
	
		            if (res != null && res.getData() != null) {
	
		                BookingResponse oldBooking = res.getData();
	
		                // ✅ Create new object (IMPORTANT)
		                BookingResponse updateRequest = new BookingResponse();
	
		                // ✅ Set required fields
		                updateRequest.setBookingId(oldBooking.getBookingId());
		                updateRequest.setStatus("Active");
	
		                // (optional but safe: copy few important fields)
		                updateRequest.setName(oldBooking.getName());
		                updateRequest.setMobileNumber(oldBooking.getMobileNumber());
	
		                bookingFeign.updateAppointment(updateRequest);
		            }
		        } catch (Exception e) {
		        }
		    }if (dto.getBookingId() != null && !dto.getBookingId().isEmpty()) {
	
		        try {
		            ResponseStructure<BookingResponse> res =
		                    bookingFeign.getBookingById(dto.getBookingId());
	
		            if (res != null && res.getData() != null) {
	
		                BookingResponse oldBooking = res.getData();
	
		                // ✅ Create new object (IMPORTANT)
		                BookingResponse updateRequest = new BookingResponse();
	
		                // ✅ Set required fields
		                updateRequest.setBookingId(oldBooking.getBookingId());
		                updateRequest.setStatus("Active");
	
		                // (optional but safe: copy few important fields)
		                updateRequest.setName(oldBooking.getName());
		                updateRequest.setMobileNumber(oldBooking.getMobileNumber());
	
		                bookingFeign.updateAppointment(updateRequest);
		            }
		        } catch (Exception e) {
		        
		        }
		    }
		  
	
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
		if (dto.getOverallStatus() != null) {
			existing.setOverallStatus(dto.getOverallStatus());
		}
		existing.setUpdatedAt(dto.getUpdatedAt());
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
		entity.setPatientInfo(dto.getPatientInfo());
		entity.setComplaints(dto.getComplaints());
		entity.setAssessment(dto.getAssessment());
		entity.setDiagnosis(dto.getDiagnosis());
		entity.setTreatmentPlan(dto.getTreatmentPlan());
		entity.setTherapySessions(dto.getTherapySessions());
		entity.setExercisePlan(dto.getExercisePlan());
		entity.setProgressNotes(dto.getProgressNotes());
		entity.setFollowUp(dto.getFollowUp());
		entity.setProgressAnalytics(dto.getProgressAnalytics());
		entity.setTreatmentTemplates(dto.getTreatmentTemplates());
		entity.setBookingId(dto.getBookingId());
		entity.setClinicId(dto.getClinicId());
		entity.setBranchId(dto.getBranchId());
		entity.setOverallStatus(dto.getOverallStatus());

		return entity;
	}
	
	@Override
	public Response getTherapistDashboard(String clinicId, String branchId, String therapistId) {

	    Response response = new Response();

	    List<PhysiotherapyRecord> records =
	            repository.findByClinicIdAndBranchIdAndTreatmentPlanTherapistId(
	                    clinicId, branchId, therapistId);

	    if (records.isEmpty()) {
	        response.setSuccess(false);
	        response.setMessage("No records found");
	        response.setStatus(404);
	        return response;
	    }

	    LocalDate today = LocalDate.now();
	    LocalDate weekStart = today.minusDays(7);
	    LocalDate monthStart = today.minusDays(30);

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    int todayCount = 0, weekCount = 0, monthCount = 0;
	    long todayMinutes = 0, weekMinutes = 0, monthMinutes = 0;

	    for (PhysiotherapyRecord record : records) {

	        if (record.getTherapySessions() == null) continue;

	        for (TherapySession session : record.getTherapySessions()) {

	            if (session.getSessionDate() == null) continue;

	            LocalDate sessionDate = LocalDate.parse(session.getSessionDate());

	            long duration = parseDuration(session.getDuration());

	            // ✅ TODAY
	            if (sessionDate.equals(today)) {
	                todayCount++;
	                todayMinutes += duration;
	            }

	            // ✅ WEEK
	            if (!sessionDate.isBefore(weekStart)) {
	                weekCount++;
	                weekMinutes += duration;
	            }

	            // ✅ MONTH
	            if (!sessionDate.isBefore(monthStart)) {
	                monthCount++;
	                monthMinutes += duration;
	            }
	        }
	    }

	    TherapistDashboardResponse dashboard = new TherapistDashboardResponse();
	    dashboard.setTodayPatientCount(todayCount);
	    dashboard.setTodayWorkingMinutes(todayMinutes);

	    dashboard.setWeeklyPatientCount(weekCount);
	    dashboard.setWeeklyWorkingMinutes(weekMinutes);

	    dashboard.setMonthlyPatientCount(monthCount);
	    dashboard.setMonthlyWorkingMinutes(monthMinutes);

	    dashboard.setRecords(records);

	    response.setSuccess(true);
	    response.setData(dashboard);
	    response.setMessage("Dashboard fetched successfully");
	    response.setStatus(200);

	    return response;
	}
	private LocalDate parseDate(String date, DateTimeFormatter formatter) {

	    if (date == null || date.isEmpty()) {
	        throw new RuntimeException("Invalid date");
	    }

	    String cleanDate = date.length() >= 10 ? date.substring(0, 10) : date;

	    return LocalDate.parse(cleanDate, formatter);
	}
	private long extractMinutes(PhysiotherapyRecord record) {

	    long totalMinutes = 0;

	    // ✅ From TherapySessions
	    if (record.getTherapySessions() != null) {

	        totalMinutes = record.getTherapySessions().stream()
	                .filter(session -> session.getDuration() != null)
	                .mapToLong(session -> parseDuration(session.getDuration()))
	                .sum();

	        if (totalMinutes > 0) {
	            return totalMinutes;
	        }
	    }

	    // ✅ Fallback: TreatmentPlan
	    if (record.getTreatmentPlan() != null &&
	        record.getTreatmentPlan().getSessionDuration() != null) {

	        return parseDuration(record.getTreatmentPlan().getSessionDuration());
	    }

	    return 0;
	}
	private long parseDuration(String duration) {

	    if (duration == null || duration.isEmpty()) return 0;

	    duration = duration.toLowerCase().trim();

	    try {
	        long value = Long.parseLong(duration.replaceAll("[^0-9]", ""));

	        // handle hours
	        if (duration.contains("hour")) {
	            return value * 60;
	        }

	        return value;

	    } catch (Exception e) {
	        return 0;
	    }
	}
	private void generateSessionIds(List<TherapySession> sessions) {

	    if (sessions == null || sessions.isEmpty()) return;

	    for (TherapySession session : sessions) {

	        // ✅ Generate UNIQUE sessionId
	        session.setSessionId("SES-" + System.currentTimeMillis());

	        // small delay to avoid same millis
	        try {
	            Thread.sleep(1);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }

	        // ✅ Auto set status if null
	        if (session.getStatus() == null || session.getStatus().isEmpty()) {
	            session.setStatus("Pending");
	        }
	    }
	}
	public void updateSessionStatusFromTherapist(String therapistRecordId, String sessionId) {

	    PhysiotherapyRecord record = repository
	            .findByTherapistRecordId(therapistRecordId)
	            .orElseThrow(() -> new RuntimeException("Record not found"));

	    List<TherapySession> sessions = record.getTherapySessions();

	    if (sessions == null || sessions.isEmpty()) {
	        throw new RuntimeException("No sessions found");
	    }

	    boolean sessionFound = false;

	    for (TherapySession session : sessions) {

	        // ✅ MATCH sessionId safely
	        if (sessionId.equals(session.getSessionId())) {

	            // ✅ Update status
	            session.setStatus("Completed");
	            sessionFound = true;
	            break;
	        }
	    }

	    if (!sessionFound) {
	        throw new RuntimeException("Session not found with ID: " + sessionId);
	    }

	    // ✅ UPDATE OVERALL STATUS
	    record.setOverallStatus(calculateOverallStatus(sessions));

	    repository.save(record);
	    // ======================================================
	    // 🔥 ADD THIS BLOCK (BOOKING UPDATE)
	    // ======================================================
	    if (record.getBookingId() != null && !record.getBookingId().isEmpty()) {

	        try {
	            ResponseStructure<BookingResponse> res =
	                    bookingFeign.getBookingById(record.getBookingId());

	            if (res != null && res.getData() != null) {

	                BookingResponse updateRequest = new BookingResponse();
	                updateRequest.setBookingId(record.getBookingId());

	                // ✅ CORE LOGIC
	                if ("Completed".equalsIgnoreCase(record.getOverallStatus())) {
	                    updateRequest.setStatus("Completed");   // 🔥 Active → Completed
	                } else {
	                    updateRequest.setStatus("Active");
	                }

	                bookingFeign.updateAppointment(updateRequest);
	            }
	        } catch (Exception e) {
	        }
	    }
	}
	private String calculateOverallStatus(List<TherapySession> sessions) {

	    if (sessions == null || sessions.isEmpty()) {
	        return "Pending";
	    }

	    boolean allCompleted = true;
	    boolean anyCompleted = false;

	    for (TherapySession s : sessions) {

	        if ("Completed".equalsIgnoreCase(s.getStatus())) {
	            anyCompleted = true;
	        } else {
	            allCompleted = false;
	        }
	    }

	    if (allCompleted) return "Completed";
	    if (anyCompleted) return "Active";

	    return "PENDING";
	}
	
}
