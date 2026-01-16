package com.rvladimir.repository;

import com.rvladimir.domain.Exercise;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
