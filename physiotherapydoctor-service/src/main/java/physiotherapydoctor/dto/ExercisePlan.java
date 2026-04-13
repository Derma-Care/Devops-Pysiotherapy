package physiotherapydoctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExercisePlan {

	private List<Exercise> exercises;
	private String homeAdvice;
}
