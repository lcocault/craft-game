package com.craftgame.backend.model;

public record GameMetrics(
        int sprint,
        int velocity,
        int quality,
        int defects,
        int techDebt,
        int morale,
        int cost,
        int clientSatisfaction
) {
}
