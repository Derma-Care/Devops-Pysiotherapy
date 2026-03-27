package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class TreatmentPlan {

    private List<String> modalities;
    private String manualTherapy;
    private String sessionDuration;
    private String frequency;
    private String totalSessions;
    private String precautions;
}
