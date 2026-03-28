package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class PhysiotherapyRecordDTO {
	private String id;
	private PatientInfo patientInfo; // ✅ NEW
	private Complaints complaints; // ✅ NEW
	private Assessment assessment;
	private Diagnosis diagnosis;
	private TreatmentPlan treatmentPlan;
	private List<TherapySession> therapySessions;
	private ExercisePlan exercisePlan;
	private ProgressNotes progressNotes;
	private FollowUp followUp;
	private ProgressAnalytics progressAnalytics;
	private List<TreatmentTemplate> treatmentTemplates;
}
