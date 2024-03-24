package com.bezkoder.spring.jpa.h2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bezkoder.spring.jpa.h2.model.Case;

public interface CaseRepository extends JpaRepository<Case, Long> {
  List<Case> findByPublished(boolean published);

  List<Case> findByTitleContainingIgnoreCase(String title);
}
