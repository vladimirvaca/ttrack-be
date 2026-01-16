package com.rvladimir.domain;

import com.rvladimir.constants.TtrackConstants;

import io.micronaut.serde.annotation.Serdeable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise", schema = TtrackConstants.TTRACK_SCHEMA)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Column(nullable = false)
    private String image;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum Type {
        STRENGTH,
        CARDIO,
        FLEXIBILITY,
        BALANCE,
        HYPERTROPHY,
        HIIT,
        PLYOMETRICS
    }
}
