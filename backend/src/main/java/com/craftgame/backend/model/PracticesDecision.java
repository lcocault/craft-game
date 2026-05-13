package com.craftgame.backend.model;

public record PracticesDecision(
        boolean automatedTesting,
        boolean ciCd,
        boolean codeReview,
        boolean refactoring
) {
}
