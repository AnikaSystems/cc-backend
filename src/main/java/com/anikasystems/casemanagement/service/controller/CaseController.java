package com.anikasystems.casemanagement.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.anikasystems.casemanagement.service.model.Case;
import com.anikasystems.casemanagement.service.repository.CaseRepository;
import com.anikasystems.casemanagement.service.jms.SimpleQueue;

@CrossOrigin(origins = "https://cc-case-management.s3.amazonaws.com")
@RestController
@RequestMapping("/api")
public class CaseController {

  @Autowired
  CaseRepository caseRepository;

  @GetMapping("/cases")
  public ResponseEntity<List<Case>> getAllCases(@RequestParam(required = false) String title) {
    try {
      List<Case> cases = new ArrayList<Case>();

      if (title == null)
        caseRepository.findAll().forEach(cases::add);
      else
        caseRepository.findByTitleContainingIgnoreCase(title).forEach(cases::add);

      if (cases.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(cases, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/cases/{id}")
  public ResponseEntity<Case> getCaseById(@PathVariable("id") long id) {
    Optional<Case> caseData = caseRepository.findById(id);

    if (caseData.isPresent()) {
      return new ResponseEntity<>(caseData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/cases")
  public ResponseEntity<Case> createCase(@RequestBody Case caseData) {
    try {
      Case _case = caseRepository.save(new Case(caseData.getTitle(), caseData.getDescription(), false));

      SimpleQueue queue = new SimpleQueue("cases");
      queue.send(caseData.getTitle());
      queue.close();

      return new ResponseEntity<>(_case, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/cases/{id}")
  public ResponseEntity<Case> updateCase(@PathVariable("id") long id, @RequestBody Case inputCase) {
    Optional<Case> caseData = caseRepository.findById(id);

    if (caseData.isPresent()) {
      Case _case = caseData.get();
      _case.setTitle(inputCase.getTitle());
      _case.setDescription(inputCase.getDescription());
      _case.setPublished(inputCase.isPublished());
      return new ResponseEntity<>(caseRepository.save(_case), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/cases/{id}")
  public ResponseEntity<HttpStatus> deleteCase(@PathVariable("id") long id) {
    try {
      caseRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/cases")
  public ResponseEntity<HttpStatus> deleteAllCases() {
    try {
      caseRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/cases/published")
  public ResponseEntity<List<Case>> findByPublished() {
    try {
      List<Case> cases = caseRepository.findByPublished(true);

      if (cases.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(cases, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
