package com.craftgame.backend.service;

import com.craftgame.backend.model.GameMetrics;

class GameState {
    String scenario = "Smooth Start";
    int duration = 6;
    int sprint = 0;

    double baseVelocity = 20;
    double baseDefects = 4;
    int quality = 80;
    int defects = 4;
    int techDebt = 5;
    int morale = 80;
    int cost = 0;
    int clientSatisfaction = 75;

    double teamExperience = 1.0;
    double onboardingLoad = 0.0;
    double codeComplexity = 0.2;
    double automationLevel = 0.1;
    double burnoutRisk = 0.0;

    boolean gameOver = false;

    GameMetrics metrics() {
        return new GameMetrics(
                sprint,
                clampToNonNegative((int) Math.round(baseVelocity)),
                clamp(quality),
                clampToNonNegative(defects),
                clampToNonNegative(techDebt),
                clamp(morale),
                clampToNonNegative(cost),
                clamp(clientSatisfaction)
        );
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private int clampToNonNegative(int value) {
        return Math.max(0, value);
    }
}
