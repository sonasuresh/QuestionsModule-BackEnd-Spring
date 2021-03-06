package com.example.QuestionsModule.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.QuestionsModule.dto.CreateQuestionInfo;
import com.example.QuestionsModule.dto.DeleteQuestionInfo;
import com.example.QuestionsModule.dto.MatchInfo;
import com.example.QuestionsModule.dto.Message;
import com.example.QuestionsModule.dto.UpdateQuestionInfo;
import com.example.QuestionsModule.dto.UpdateQuestionStatusInfo;
import com.example.QuestionsModule.exception.ServiceException;
import com.example.QuestionsModule.model.BestChoice;
import com.example.QuestionsModule.model.Category;
import com.example.QuestionsModule.model.Level;
import com.example.QuestionsModule.model.Match;
import com.example.QuestionsModule.model.MultipleChoice;
import com.example.QuestionsModule.model.Question;
import com.example.QuestionsModule.model.Types;
import com.example.QuestionsModule.service.QuestionService;
import com.google.gson.Gson;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
//create a new question
@RequestMapping("/questions")
public class QuestionController {
	@Autowired
	QuestionService questionService;
	


	@PostMapping("/add")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> addQuestion(@RequestBody CreateQuestionInfo createQuestionInfo) {
		String errorResult = null;
		Question question = null;

		try {

			 questionService.addQuestion(createQuestionInfo);
			 return new ResponseEntity<>(question, HttpStatus.CREATED);
		} catch (ServiceException e) {
			errorResult = e.getMessage();
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);

		}


	}

//fetch questions whose status is active
	@GetMapping("/activated")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> viewActivatedQuestion(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy)
			 {

		String result = null;
		Page<Question> activatedQuestions = null;
		try {
			activatedQuestions = questionService.getactivatedQuestions(pageNo, pageSize, sortBy);
		} catch (ServiceException e) {

			result = e.getMessage();
		}
		if (activatedQuestions != null) {

			return new ResponseEntity<>(activatedQuestions, HttpStatus.OK);
		} else {
			Message message = new Message(result);

			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/match")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getMatch(@RequestParam int qid){
		String result = null;
		List<Match> match = null;
		try {
			match = questionService.getMatches(qid);
		} catch (ServiceException e) {
			result = e.getMessage();
		}
		if (match != null) {
			return new ResponseEntity<>(match, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/bestchoice")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getBestChoice(@RequestParam int qid) {
		String result = null;
		List<BestChoice> bestChoice = null;
		try {
			bestChoice = questionService.getBestChoice(qid);
		} catch (ServiceException e) {
			result = e.getMessage();
		}
		if (bestChoice != null) {
			return new ResponseEntity<>(bestChoice, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/multiplechoice")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getMultipleChoice(@RequestParam int qid){
		String result = null;
		List<MultipleChoice> multipleChoice = null;
		try {
			multipleChoice = questionService.getMultipleChoice(qid);
		} catch (ServiceException e) {
			result = e.getMessage();
		}
		if (multipleChoice != null) {
			return new ResponseEntity<>(multipleChoice, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/levels")
	//@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getLevels() {
		String errorResult = null;
		List<Level> levels = null;
		try {

			levels = questionService.getLevels();
		} catch (ServiceException e) {
			errorResult = e.getMessage();
		}
		if (levels != null) {
			return new ResponseEntity<>(levels, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/types")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getTypes() {
		String errorResult = null;
		List<Types> types = null;
		try {

			types = questionService.getTypes();
		} catch (ServiceException e) {
			errorResult = e.getMessage();
		}
		if (types != null) {
			return new ResponseEntity<>(types, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
		}
	}

//fetch questions whose status is deactive
	@GetMapping("/deactivated")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> viewDeactivatedQuestion(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy)
			 {
		String result = null;
		Page<Question> deactivatedQuestions = null;

		try {
			deactivatedQuestions = questionService.getDeactivatedQuestions(pageNo, pageSize, sortBy);
		} catch (ServiceException e) {
			result = e.getMessage();
		}
		if (deactivatedQuestions != null) {
			return new ResponseEntity<>(deactivatedQuestions, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

//update question details for a particular question
	@PutMapping("/edit")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> editQuestion(@RequestBody UpdateQuestionInfo updateQuestionInfo) throws Exception {
		String updateResult = null;
		String errorResult = null;
		Question question = null;
		try {
			questionService.updateQuestion(updateQuestionInfo);
			return new ResponseEntity<>(updateResult, HttpStatus.OK);

		} catch (ServiceException e) {
			errorResult = e.getMessage();
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);

		}
	}

//update status based on ids(applicable for single and bulk update)
	@PutMapping("/updatestatus")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> update(@RequestBody UpdateQuestionStatusInfo updateQuestionStatusInfo) {
		String updateStatusResult = null;
		String errorResult = null;
		try {

			questionService.updateQuestionStatus(updateQuestionStatusInfo);
			return new ResponseEntity<>(updateStatusResult, HttpStatus.OK);
		} catch (ServiceException e) {
			errorResult = e.getMessage();
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);

		}
	}

//delete question based on set of ids(applicable for single and bulk delete)
	@DeleteMapping("/delete")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> deleteQuestion(@RequestBody DeleteQuestionInfo deleteQuestionInfo) {
		String deleteResult = null;
		String errorResult = null;
		try {
			questionService.deleteQuestion(deleteQuestionInfo);
			return new ResponseEntity<>(deleteResult, HttpStatus.OK);
		} catch (ServiceException e) {
			errorResult = e.getMessage();
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
		}

	}

//search question based on tags
	@GetMapping("/basedontags")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getQuestionBasedOnTags(@RequestParam String tagName, @RequestParam String status,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy){
		String result = null;
		Page<Question> questions = null;

		try {
			questions = questionService.getQuestionBasedOnTags(tagName, status, pageNo, pageSize, sortBy);
		} catch (ServiceException e) {
			result = e.getMessage();
		}
		if (questions != null) {
			return new ResponseEntity<>(questions, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		}
	}

//get question details based on id
	@GetMapping("/")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getQuestion(@RequestParam int id){
		String errorResult = null;
		Optional<Question> question = null;
		try {

			question = questionService.getQuestion(id);

		} catch (ServiceException e) {
			errorResult = e.getMessage();
		}
		if (question != null) {
			return new ResponseEntity<>(question, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/categories")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseEntity<?> getCategories() {
		String errorResult = null;
		List<Category> categories = null;
		try {

			categories = questionService.getCategories();
		} catch (ServiceException e) {
			errorResult = e.getMessage();
		}
		if (categories != null) {
			return new ResponseEntity<>(categories, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
		}
	}

}
