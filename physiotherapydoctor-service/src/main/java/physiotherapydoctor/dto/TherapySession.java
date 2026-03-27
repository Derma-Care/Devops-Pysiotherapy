package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class TherapySession {

    private String sessionDate;
    private List<String> modalitiesUsed;
    private String exercisesDone;
    private String patientResponse;
    private String therapistNotes;
}