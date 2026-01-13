package com.rvladimir.service.mapper;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;

import jakarta.inject.Singleton;

import java.time.LocalDateTime;

@Singleton
public class TrainingSessionMapper {

    /**
     * Convert CreateTrainingSessionDTO to TrainingSession entity
     * @param createTrainingSessionDTO the DTO to convert
     * @return the TrainingSession entity
     */
    public TrainingSession toEntity(CreateTrainingSessionDTO createTrainingSessionDTO) {
        if (createTrainingSessionDTO == null) {
            return null;
        }
        TrainingSession trainingSession = new TrainingSession();
        trainingSession.setId(null);
        trainingSession.setStatus(TrainingSession.Status.STARTED);
        trainingSession.setCreatedAt(LocalDateTime.now());
        User user = new User();
        user.setId(createTrainingSessionDTO.getUserId());
        trainingSession.setUser(user);
        return trainingSession;
    }

    /**
     * Convert TrainingSession entity to TrainingSessionDTO
     * @param trainingSession the entity to convert
     * @return the TrainingSessionDTO
     */
    public TrainingSessionDTO toDto(TrainingSession trainingSession) {
        if (trainingSession == null) {
            return null;
        }
        return new TrainingSessionDTO(
            trainingSession.getId(),
            trainingSession.getName(),
            trainingSession.getDescription(),
            trainingSession.getStatus(),
            trainingSession.getUser().getId(),
            trainingSession.getCreatedAt()
        );
    }
}
