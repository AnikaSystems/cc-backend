package com.anikasystems.casemanagement.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anikasystems.casemanagement.service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  List<User> findByInternal(boolean internal);

  List<User> findByLastnameContainingIgnoreCase(String lastname);
}
