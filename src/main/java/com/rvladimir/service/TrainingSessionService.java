package com.rvladimir.service;

import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.QuickStartIntervalDTO;
import com.rvladimir.service.dto.QuickStartIntervalResponseDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;

public interface TrainingSessionService {

    TrainingSessionDTO create(CreateTrainingSessionDTO createTrainingSessionDTO);

    /**
     * Atomically creates a TrainingSession and its first SessionExercise for an interval training quick start
     * (HIIT, boxing bag, shadow boxing, etc.).
     *
     * @param dto the quick start interval training data
     * @return a composite response containing the created TrainingSession and SessionExercise
     */
    QuickStartIntervalResponseDTO quickStartInterval(QuickStartIntervalDTO dto);
}
