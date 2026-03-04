package com.rvladimir.domain;

/**
 * Enum representing the type/category of an exercise.
 * Used both to classify exercises in the catalog and to describe how an exercise
 * was performed within a training session.
 */
public enum TypeOfExercise {
    /** Strength training, e.g. weightlifting, resistance exercises. */
    STRENGTH,
    /** Cardiovascular training, e.g. running, cycling. */
    CARDIO,
    /** Flexibility training, e.g. stretching, yoga. */
    FLEXIBILITY,
    /** Balance training, e.g. stability exercises, yoga poses. */
    BALANCE,
    /** Hypertrophy training focused on muscle growth. */
    HYPERTROPHY,
    /** Endurance training, e.g. long-distance running, swimming. */
    ENDURANCE,
    /** High-Intensity Interval Training. */
    HIIT,
    /** Plyometric training, e.g. jump squats, box jumps. */
    PLYOMETRIC,
    /** Plyometrics — alias kept for backward compatibility. */
    PLYOMETRICS,
    /** Calisthenics, e.g. push-ups, pull-ups, bodyweight exercises. */
    CALISTHENICS,
    /** Mobility training, e.g. dynamic stretching, foam rolling. */
    MOBILITY,
    /** Sport-specific training. */
    SPORT_SPECIFIC,
    /** Boxing bag training, e.g. heavy bag, speed bag punching. */
    BOXING_BAG,
    /** Shadow boxing, i.e. boxing without a partner or equipment. */
    SHADOW_BOXING,
    /** Other type of exercise not listed above. */
    OTHER
}
