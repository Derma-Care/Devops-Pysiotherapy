package physiotherapydoctor.dto;

import lombok.Data;

@Data
public class PatientInfo {
    private String patientId;
    private String name;
    private String mobileNumber;
    private int age;
    private String sex;
}