package com.craftgame.backend.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SprintDecisionRequest(
        @Valid @NotNull StaffingDecision staffing,
        @Valid @NotNull PracticesDecision practices,
        @Valid @NotNull DeliveryDecision delivery
) {
}
