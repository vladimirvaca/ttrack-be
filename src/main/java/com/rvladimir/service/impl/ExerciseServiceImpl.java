package com.rvladimir.service.impl;

import com.rvladimir.domain.Exercise;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.service.ExerciseService;
import com.rvladimir.service.dto.ExerciseDTO;
import com.rvladimir.service.mapper.ExerciseMapper;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;

import jakarta.inject.Singleton;

@Singleton
@Transactional
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    @Override
    public ExerciseDTO create(ExerciseDTO exerciseDTO) {
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);
        Exercise savedExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toDto(savedExercise);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExerciseDTO> getAll(Pageable pageable) {
        return exerciseRepository.findAll(pageable)
            .map(exerciseMapper::toDto);
    }
}
