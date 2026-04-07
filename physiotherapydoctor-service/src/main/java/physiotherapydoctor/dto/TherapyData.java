package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class TherapyData {

	private String id;
	private String therapyName;

	private List<TherapyExercise> exercises;
}
