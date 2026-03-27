package com.dermaCare.customerService.service;
import java.util.List;

import org.springframework.http.ResponseEntity;
import com.dermaCare.customerService.dto.QuestionsByPartDTO;
import com.dermaCare.customerService.dto.QuestionsDTO;
import com.dermaCare.customerService.util.Response;

public interface PhysiotherapyService {
	
	ResponseEntity<Response> create(QuestionsByPartDTO dto);

    ResponseEntity<Response> getAll();

    public ResponseEntity<Response> deleteQuestionByKeyAndId(String key, long questionId);
	            
    public ResponseEntity<Response> updateByKey(String key, QuestionsDTO dto);
	    
    public ResponseEntity<Response> getByKeys(List<String> keys);
	         	   
}
