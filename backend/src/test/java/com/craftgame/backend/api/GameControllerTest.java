package com.craftgame.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void startAndStateEndpointsShouldWork() throws Exception {
        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenario").value("Smooth Start"));

        mockMvc.perform(get("/api/game/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics.sprint").value(0));
    }

    @Test
    void sprintEndpointShouldAcceptDecisionPayload() throws Exception {
        mockMvc.perform(post("/api/game/start"))
                .andExpect(status().isOk());

        String payload = """
                {
                  "staffing": {"hireSenior": 1, "hireJunior": 0, "fire": 0},
                  "practices": {"automatedTesting": true, "ciCd": true, "codeReview": true, "refactoring": false},
                  "delivery": {"overtime": false, "skipTests": false, "acceptTechnicalDebt": false}
                }
                """;

        mockMvc.perform(post("/api/game/sprint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metrics.sprint").value(1))
                .andExpect(jsonPath("$.events").isArray());
    }

    @Test
    void rootShouldServeMvpFrontEnd() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("index.html"));
    }
}
