package com.craftgame.backend.service;

import com.craftgame.backend.model.DeliveryDecision;
import com.craftgame.backend.model.GameStateResponse;
import com.craftgame.backend.model.PracticesDecision;
import com.craftgame.backend.model.SprintDecisionRequest;
import com.craftgame.backend.model.SprintResult;
import com.craftgame.backend.model.StaffingDecision;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GameSimulationService {

    private static final int LOW_MORALE_THRESHOLD = 40;
    private static final double ATTRITION_PROBABILITY = 0.5;
    private static final double PRODUCTION_INCIDENT_PROBABILITY = 0.3;
    private static final double TEAM_MEMBER_LEAVES_PROBABILITY = 0.2;
    private static final double SCOPE_INCREASE_PROBABILITY = 0.25;
    private static final int PRODUCTION_INCIDENT_DEFECTS_IMPACT = 3;
    private static final int PRODUCTION_INCIDENT_MORALE_IMPACT = 8;
    private static final int TEAM_MEMBER_LEAVES_VELOCITY_IMPACT = 2;
    private static final int SCOPE_INCREASE_TECH_DEBT_IMPACT = 2;

    private final Random random;
    private GameState state;

    public GameSimulationService(Random random) {
        this.random = random;
        this.state = new GameState();
    }

    public synchronized GameStateResponse startScenario() {
        this.state = new GameState();
        return getState();
    }

    public synchronized GameStateResponse getState() {
        return new GameStateResponse(state.scenario, state.duration, state.metrics(), state.gameOver);
    }

    public synchronized SprintResult runSprint(SprintDecisionRequest request) {
        if (state.gameOver) {
            return new SprintResult(state.metrics(), List.of("Game is already over. Start a new scenario."), true, calculateScore());
        }

        List<String> events = new ArrayList<>();

        applyStaffing(request.staffing(), events);
        applyPractices(request.practices());
        applyDeliveryPressure(request.delivery());

        updateTechnicalDebt(request.delivery().acceptTechnicalDebt(), request.practices().refactoring());
        updateMorale(request.delivery().overtime(), request.practices(), events);
        updateDefects(request.delivery(), request.practices());
        updateVelocity(request.delivery());
        triggerRandomEvents(events);
        updateQualityAndSatisfaction();

        state.sprint++;
        if (state.sprint >= state.duration) {
            state.gameOver = true;
        }

        return new SprintResult(state.metrics(), events, state.gameOver, state.gameOver ? calculateScore() : null);
    }

    private void applyStaffing(StaffingDecision staffing, List<String> events) {
        if (staffing.hireSenior() > 0) {
            state.baseVelocity += staffing.hireSenior() * 2.0;
            state.teamExperience += staffing.hireSenior() * 0.1;
            state.cost += staffing.hireSenior() * 2000;
            events.add("Hired " + staffing.hireSenior() + " senior engineer(s).");
        }

        if (staffing.hireJunior() > 0) {
            state.baseVelocity += staffing.hireJunior() * 1.0;
            state.teamExperience -= staffing.hireJunior() * 0.03;
            state.onboardingLoad += staffing.hireJunior() * 0.08;
            state.cost += staffing.hireJunior() * 1000;
            events.add("Hired " + staffing.hireJunior() + " junior engineer(s), onboarding load increased.");
        }

        if (staffing.fire() > 0) {
            state.baseVelocity -= staffing.fire() * 2.0;
            state.teamExperience -= staffing.fire() * 0.08;
            state.cost -= staffing.fire() * 500;
            events.add("Reduced team by " + staffing.fire() + " engineer(s).");
        }

        state.teamExperience = Math.max(0.3, Math.min(2.0, state.teamExperience));
    }

    private void applyPractices(PracticesDecision practices) {
        if (practices.automatedTesting()) {
            state.automationLevel = Math.min(1.0, state.automationLevel + 0.12);
            state.cost += 200;
        }
        if (practices.ciCd()) {
            state.automationLevel = Math.min(1.0, state.automationLevel + 0.07);
            state.cost += 150;
        }
        if (practices.codeReview()) {
            state.teamExperience = Math.min(2.0, state.teamExperience + 0.03);
            state.cost += 100;
        }
        if (practices.refactoring()) {
            state.codeComplexity = Math.max(0.1, state.codeComplexity - 0.05);
            state.cost += 120;
        }
    }

    private void applyDeliveryPressure(DeliveryDecision delivery) {
        if (delivery.overtime()) {
            state.cost += 300;
            state.burnoutRisk = Math.min(1.0, state.burnoutRisk + 0.15);
        } else {
            state.burnoutRisk = Math.max(0.0, state.burnoutRisk - 0.08);
        }
        if (delivery.skipTests()) {
            state.cost -= 100;
        }
    }

    private void updateTechnicalDebt(boolean acceptTechnicalDebt, boolean refactoring) {
        state.techDebt += acceptTechnicalDebt ? 5 : 0;
        state.techDebt -= refactoring ? 3 : 0;
        state.techDebt = Math.max(0, state.techDebt);
    }

    private void updateMorale(boolean overtime, PracticesDecision practices, List<String> events) {
        state.morale -= overtime ? 10 : 0;
        boolean codeQualityImprovement = practices.codeReview() || practices.refactoring() || practices.automatedTesting();
        state.morale += codeQualityImprovement ? 5 : 0;
        state.morale -= (int) Math.round(state.burnoutRisk * 3);
        state.morale = Math.max(0, Math.min(100, state.morale));

        if (state.morale < LOW_MORALE_THRESHOLD && random.nextDouble() < ATTRITION_PROBABILITY) {
            state.baseVelocity = Math.max(1, state.baseVelocity - 2);
            events.add("Low morale drives attrition, reducing team capacity.");
        }
    }

    private void updateDefects(DeliveryDecision delivery, PracticesDecision practices) {
        double automationFactor = practices.automatedTesting() ? 1.0 : 0.0;
        double defects = state.baseDefects
                * (1 + state.techDebt * 0.02)
                * (delivery.skipTests() ? 1.5 : 1)
                * (1 - automationFactor * 0.5);
        state.defects = Math.max(0, (int) Math.round(defects));
    }

    private void updateVelocity(DeliveryDecision delivery) {
        double teamExperienceFactor = Math.max(0.5, state.teamExperience - state.onboardingLoad * 0.15);
        double velocity = state.baseVelocity
                * teamExperienceFactor
                * (state.morale / 100.0)
                * Math.max(0.1, (1 - state.techDebt * 0.01))
                * (1 + state.automationLevel * 0.1);

        if (delivery.overtime()) {
            velocity *= 1.15;
        }
        if (delivery.skipTests()) {
            velocity *= 1.10;
        }
        if (delivery.acceptTechnicalDebt()) {
            velocity *= 1.05;
        }

        state.baseVelocity = Math.max(1, velocity);
        state.onboardingLoad = Math.max(0.0, state.onboardingLoad - 0.05);
    }

    private void triggerRandomEvents(List<String> events) {
        if (random.nextDouble() < PRODUCTION_INCIDENT_PROBABILITY) {
            state.defects += PRODUCTION_INCIDENT_DEFECTS_IMPACT;
            state.morale = Math.max(0, state.morale - PRODUCTION_INCIDENT_MORALE_IMPACT);
            events.add("Production incident hit the team: defects increased and morale dropped.");
        }
        if (random.nextDouble() < TEAM_MEMBER_LEAVES_PROBABILITY) {
            state.baseVelocity = Math.max(1, state.baseVelocity - TEAM_MEMBER_LEAVES_VELOCITY_IMPACT);
            state.teamExperience = Math.max(0.3, state.teamExperience - 0.1);
            events.add("A team member leaves unexpectedly, reducing delivery capacity.");
        }
        if (random.nextDouble() < SCOPE_INCREASE_PROBABILITY) {
            state.codeComplexity += 0.1;
            state.techDebt += SCOPE_INCREASE_TECH_DEBT_IMPACT;
            events.add("Scope increased this sprint, making delivery harder.");
        }
    }

    private void updateQualityAndSatisfaction() {
        state.quality = 100 - (state.defects * 4) - state.techDebt - (int) Math.round(state.codeComplexity * 10);
        state.quality = Math.max(0, Math.min(100, state.quality));

        int velocitySignal = (int) Math.round(state.baseVelocity) >= 20 ? 2 : -2;
        int qualitySignal = state.quality >= 70 ? 2 : -3;
        int defectSignal = state.defects <= 5 ? 1 : -3;

        state.clientSatisfaction += velocitySignal + qualitySignal + defectSignal;
        state.clientSatisfaction = Math.max(0, Math.min(100, state.clientSatisfaction));
        state.cost += 500;
    }

    private int calculateScore() {
        return (int) Math.round(state.baseVelocity)
                + state.clientSatisfaction
                + state.morale
                - state.cost
                - state.techDebt;
    }
}
