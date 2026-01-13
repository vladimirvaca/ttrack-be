package com.rvladimir.service;

import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;

public interface TrainingSessionService {

    TrainingSessionDTO create(CreateTrainingSessionDTO createTrainingSessionDTO);
}
