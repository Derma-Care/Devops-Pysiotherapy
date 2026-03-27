package com.dermaCare.customerService.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsEntity {

	private long questionId;
	private String question;
	private String type;
}
