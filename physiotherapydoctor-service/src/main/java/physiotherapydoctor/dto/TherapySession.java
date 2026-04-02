package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class TherapySession {
	private String sessionId;
	private String sessionDate;
	private String status;
	private List<String> modalitiesUsed;
	private List<String> exercisesDone;
	private String patientResponse;
	private String duration;
//	private String therapistNotes;
//	private String overallStatus; // ✅ NEW
}