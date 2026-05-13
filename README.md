# craft-game
A serious game to learn the principles of software craftsmanship and their impact on software delivery.

## Step 1 (current): MVP (Backend + Front-End)

The repository contains the Spring Boot backend and a minimal front-end for the simulation game.

### Run locally

```bash
cd backend
./mvnw spring-boot:run
```

If Maven Wrapper is not available in your environment, use:

```bash
mvn spring-boot:run
```

### API endpoints

- `POST /api/game/start`
- `POST /api/game/sprint`
- `GET /api/game/state`

### Front-End

- Main UI: `http://localhost:8080/`
- The UI starts a scenario, submits sprint decisions, and displays metrics/events from backend responses.

### OpenAPI

- JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
