package com.rvladimir.service;

import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import java.util.List;

/**
 * Service interface for SessionExercise operations.
 */
public interface SessionExerciseService {

    /**
     * Creates a new SessionExercise for a training session.
     * @param trainingSessionId the ID of the training session
     * @param dto the session exercise data
     * @return the created SessionExerciseDTO
     */
    SessionExerciseDTO createSessionExercise(Long trainingSessionId, CreateSessionExerciseDTO dto);

    /**
     * Retrieves all SessionExercises for a training session.
     * @param trainingSessionId the training session ID
     * @return list of session exercises
     */
    List<SessionExerciseDTO> getSessionExercisesByTrainingSession(Long trainingSessionId);
}
