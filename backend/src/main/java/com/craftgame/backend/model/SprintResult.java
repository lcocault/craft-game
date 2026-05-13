package com.craftgame.backend.model;

import java.util.List;

public record SprintResult(
        GameMetrics metrics,
        List<String> events,
        boolean gameOver,
        Integer finalScore
) {
}
