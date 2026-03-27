package physiotherapydoctor.dto;

import lombok.Data;

@Data
public class Exercise {

    private String name;
    private String sets;
    private String reps;
    private String duration;
    private String instructions;
    private String videoUrl;
    private String thumbnail;
}
