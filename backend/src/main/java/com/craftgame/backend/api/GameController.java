package com.craftgame.backend.api;

import com.craftgame.backend.model.GameStateResponse;
import com.craftgame.backend.model.SprintDecisionRequest;
import com.craftgame.backend.model.SprintResult;
import com.craftgame.backend.service.GameSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
@Tag(name = "Game", description = "Game lifecycle and simulation endpoints")
public class GameController {

    private final GameSimulationService simulationService;

    public GameController(GameSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start scenario", description = "Starts the Smooth Start scenario and returns initial state")
    public ResponseEntity<GameStateResponse> start() {
        return ResponseEntity.ok(simulationService.startScenario());
    }

    @PostMapping("/sprint")
    @Operation(summary = "Run sprint", description = "Applies decisions and simulates a sprint")
    public ResponseEntity<SprintResult> sprint(@Valid @RequestBody SprintDecisionRequest decisionRequest) {
        return ResponseEntity.ok(simulationService.runSprint(decisionRequest));
    }

    @GetMapping("/state")
    @Operation(summary = "Get game state", description = "Returns current game state")
    public ResponseEntity<GameStateResponse> state() {
        return ResponseEntity.ok(simulationService.getState());
    }
}
