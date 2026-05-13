package com.craftgame.backend.model;

public record GameStateResponse(
        String scenario,
        int duration,
        GameMetrics metrics,
        boolean gameOver
) {
}
