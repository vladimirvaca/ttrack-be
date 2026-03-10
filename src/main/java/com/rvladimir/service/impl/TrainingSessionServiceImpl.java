package com.rvladimir.service.impl;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.TrainingSessionService;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.QuickStartIntervalDTO;
import com.rvladimir.service.dto.QuickStartIntervalResponseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.service.mapper.SessionExerciseMapper;
import com.rvladimir.service.mapper.TrainingSessionMapper;
import com.rvladimir.web.error.ValidationException;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Singleton
@Transactional
public class TrainingSessionServiceImpl implements TrainingSessionService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String FIELD_USER_ID = "userId";
    private static final String ERROR_NOT_FOUND = "NOT_FOUND";
    private static final String DEFAULT_INTERVAL_LABEL = "INTERVAL";

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserRepository userRepository;
    private final TrainingSessionMapper trainingSessionMapper;
    private final SessionExerciseRepository sessionExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final SessionExerciseMapper sessionExerciseMapper;

    public TrainingSessionServiceImpl(
            TrainingSessionRepository trainingSessionRepository,
            UserRepository userRepository,
            TrainingSessionMapper trainingSessionMapper,
            SessionExerciseRepository sessionExerciseRepository,
            ExerciseRepository exerciseRepository,
            SessionExerciseMapper sessionExerciseMapper) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userRepository = userRepository;
        this.trainingSessionMapper = trainingSessionMapper;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.exerciseRepository = exerciseRepository;
        this.sessionExerciseMapper = sessionExerciseMapper;
    }

    @Override
    public TrainingSessionDTO create(CreateTrainingSessionDTO createTrainingSessionDTO) {
        if (!userRepository.existsById(createTrainingSessionDTO.getUserId())) {
            throw new ValidationException(USER_NOT_FOUND, FIELD_USER_ID, ERROR_NOT_FOUND);
        }

        TrainingSession trainingSession = trainingSessionMapper.toEntity(createTrainingSessionDTO);
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);
        return trainingSessionMapper.toDto(savedTrainingSession);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuickStartIntervalResponseDTO quickStartInterval(QuickStartIntervalDTO dto) {
        if (!userRepository.existsById(dto.getUserId())) {
            throw new ValidationException(USER_NOT_FOUND, FIELD_USER_ID, ERROR_NOT_FOUND);
        }

        TrainingSession session = buildIntervalSession(dto);
        TrainingSession savedSession = trainingSessionRepository.save(session);

        SessionExercise exercise = buildIntervalExercise(dto, savedSession);
        SessionExercise savedExercise = sessionExerciseRepository.save(exercise);

        TrainingSessionDTO sessionDTO = trainingSessionMapper.toDto(savedSession);
        SessionExerciseDTO exerciseDTO = sessionExerciseMapper.toDto(savedExercise);
        return new QuickStartIntervalResponseDTO(sessionDTO, exerciseDTO);
    }

    private TrainingSession buildIntervalSession(QuickStartIntervalDTO dto) {
        TrainingSession session = new TrainingSession();
        String label = DEFAULT_INTERVAL_LABEL;
        if (dto.getTypeOfExercise() != null) {
            label = dto.getTypeOfExercise();
        }
        session.setName("Quick Start – " + label);
        session.setStatus(TrainingSession.Status.STARTED);
        session.setCreatedAt(LocalDateTime.now());
        com.rvladimir.domain.User user = new com.rvladimir.domain.User();
        user.setId(dto.getUserId());
        session.setUser(user);
        return session;
    }

    private SessionExercise buildIntervalExercise(QuickStartIntervalDTO dto, TrainingSession session) {
        SessionExercise exercise = new SessionExercise();
        exercise.setRounds(dto.getRounds());
        exercise.setSprints(dto.getSprints());
        exercise.setDuration(dto.getDuration());
        exercise.setRestTime(dto.getRestTime());
        exercise.setStatus(SessionExercise.Status.STARTED);
        exercise.setExerciseOrder(1);
        exercise.setTrainingSession(session);
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setNotes(dto.getNotes());
        if (dto.getTypeOfExercise() != null) {
            exercise.setTypeOfExercise(TypeOfExercise.valueOf(dto.getTypeOfExercise()));
        }
        if (dto.getExerciseId() != null) {
            Optional<Exercise> catalogExercise = exerciseRepository.findById(dto.getExerciseId());
            catalogExercise.ifPresent(exercise::setExercise);
        }
        return exercise;
    }
}
