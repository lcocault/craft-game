package com.craftgame.backend.model;

import jakarta.validation.constraints.Min;

public record StaffingDecision(
        @Min(0) int hireSenior,
        @Min(0) int hireJunior,
        @Min(0) int fire
) {
}
