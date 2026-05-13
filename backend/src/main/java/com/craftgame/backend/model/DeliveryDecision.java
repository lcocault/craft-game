package com.craftgame.backend.model;

public record DeliveryDecision(
        boolean overtime,
        boolean skipTests,
        boolean acceptTechnicalDebt
) {
}
