package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class Complaints {
	private String complaintDetails;
	private String painAssessmentImage;
	private List<String> reportImages;
	private String selectedTherapy;
	private List<String> theraphyAnswers;
	private String duration;
}
