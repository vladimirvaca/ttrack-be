package com.rvladimir.service;

import com.rvladimir.service.dto.ExerciseDTO;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

public interface ExerciseService {

    ExerciseDTO create(ExerciseDTO exerciseDTO);

    Page<ExerciseDTO> getAll(Pageable pageable);
}
