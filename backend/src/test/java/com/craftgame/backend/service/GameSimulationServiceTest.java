package com.craftgame.backend.service;

import com.craftgame.backend.model.DeliveryDecision;
import com.craftgame.backend.model.PracticesDecision;
import com.craftgame.backend.model.SprintDecisionRequest;
import com.craftgame.backend.model.SprintResult;
import com.craftgame.backend.model.StaffingDecision;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class GameSimulationServiceTest {

    @Test
    void startScenarioShouldResetToSmoothStartDefaults() {
        GameSimulationService service = new GameSimulationService(new Random(0));

        var state = service.startScenario();

        assertThat(state.scenario()).isEqualTo("Smooth Start");
        assertThat(state.duration()).isEqualTo(6);
        assertThat(state.metrics().sprint()).isEqualTo(0);
        assertThat(state.metrics().morale()).isEqualTo(80);
        assertThat(state.metrics().techDebt()).isEqualTo(5);
    }

    @Test
    void sprintShouldProgressAndRespectBounds() {
        GameSimulationService service = new GameSimulationService(new Random(1));
        service.startScenario();

        SprintResult result = service.runSprint(new SprintDecisionRequest(
                new StaffingDecision(0, 0, 0),
                new PracticesDecision(true, true, true, true),
                new DeliveryDecision(false, false, false)
        ));

        assertThat(result.metrics().sprint()).isEqualTo(1);
        assertThat(result.metrics().morale()).isBetween(0, 100);
        assertThat(result.metrics().quality()).isBetween(0, 100);
        assertThat(result.metrics().techDebt()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void gameShouldEndAfterSixSprintsAndReturnScore() {
        GameSimulationService service = new GameSimulationService(new Random(2));
        service.startScenario();

        SprintResult finalResult = null;
        for (int i = 0; i < 6; i++) {
            finalResult = service.runSprint(new SprintDecisionRequest(
                    new StaffingDecision(0, 0, 0),
                    new PracticesDecision(false, false, false, false),
                    new DeliveryDecision(false, false, false)
            ));
        }

        assertThat(finalResult).isNotNull();
        assertThat(finalResult.gameOver()).isTrue();
        assertThat(finalResult.finalScore()).isNotNull();
    }

    @Test
    void simulatesAttritionWhenMoraleIsLow() {
        GameSimulationService service = new GameSimulationService(new Random(0));
        service.startScenario();

        SprintResult result = null;
        for (int i = 0; i < 5; i++) {
            result = service.runSprint(new SprintDecisionRequest(
                    new StaffingDecision(0, 0, 0),
                    new PracticesDecision(false, false, false, false),
                    new DeliveryDecision(true, false, false)
            ));
        }

        assertThat(result).isNotNull();
        assertThat(result.events()).contains("Low morale drives attrition, reducing team capacity.");
    }
}
