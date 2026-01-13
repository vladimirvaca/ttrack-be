package com.rvladimir.service.impl;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.TrainingSessionService;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.service.mapper.TrainingSessionMapper;
import com.rvladimir.web.error.ValidationException;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
@Transactional
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserRepository userRepository;
    private final TrainingSessionMapper trainingSessionMapper;

    public TrainingSessionServiceImpl(
            TrainingSessionRepository trainingSessionRepository,
            UserRepository userRepository,
            TrainingSessionMapper trainingSessionMapper) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userRepository = userRepository;
        this.trainingSessionMapper = trainingSessionMapper;
    }

    @Override
    public TrainingSessionDTO create(CreateTrainingSessionDTO createTrainingSessionDTO) {
        if (!userRepository.existsById(createTrainingSessionDTO.getUserId())) {
            throw new ValidationException("User not found", "userId", "NOT_FOUND");
        }

        TrainingSession trainingSession = trainingSessionMapper.toEntity(createTrainingSessionDTO);
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);
        return trainingSessionMapper.toDto(savedTrainingSession);
    }
}
