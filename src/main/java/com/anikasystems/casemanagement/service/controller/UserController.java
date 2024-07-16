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

import com.anikasystems.casemanagement.service.model.User;
import com.anikasystems.casemanagement.service.repository.UserRepository;
import com.anikasystems.casemanagement.service.jms.SimpleQueue;

@CrossOrigin(origins = "https://cc-case-management.s3.amazonaws.com")
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  UserRepository userRepository;

  @GetMapping("/users")
  public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String lastname) {
    try {
      List<User> users = new ArrayList<User>();

      if (lastname == null)
        userRepository.findAll().forEach(users::add);
      else
        userRepository.findByLastnameContainingIgnoreCase(lastname).forEach(users::add);

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<User> getCaseById(@PathVariable("id") long id) {
    Optional<User> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/users")
  public ResponseEntity<User> createCase(@RequestBody User userData) {
    try {
      User _user = userRepository.save(new User(userData.getFirstname(), userData.getLastname(), false));

      SimpleQueue queue = new SimpleQueue("users");
      queue.send(userData.getLastname());
      queue.close();

      return new ResponseEntity<>(_user, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<User> updateCase(@PathVariable("id") long id, @RequestBody User inputCase) {
    Optional<User> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      User _user = userData.get();
      _user.setFirstname(inputCase.getFirstname());
      _user.setLastname(inputCase.getLastname());
      _user.setInternal(inputCase.isInternal());
      return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<HttpStatus> deleteCase(@PathVariable("id") long id) {
    try {
      userRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/users")
  public ResponseEntity<HttpStatus> deleteAllUsers() {
    try {
      userRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/users/internal")
  public ResponseEntity<List<User>> findByInternal() {
    try {
      List<User> users = userRepository.findByInternal(true);

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
