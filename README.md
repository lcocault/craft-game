# craft-game
A serious game to learn the principles of software craftsmanship and their impact on software delivery.

## Step 1 (current): Backend MVP

The repository currently contains the Spring Boot backend for the simulation game.

### Run locally

```bash
cd /home/runner/work/craft-game/craft-game/backend
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

### OpenAPI

- JSON: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
