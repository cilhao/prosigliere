# Prosigliere

A REST API for managing blog posts and comments, built with **Spring Boot 4** and a layered architecture (controller,
service, repository, DTOs, and MapStruct mappers).

The API exposes CRUD-style operations for posts, supports adding comments to posts, returns structured error payloads,
and paginates the post listing endpoint to keep responses lightweight as the dataset grows.

## Tech stack

| Layer         | Technology                                        |
|---------------|---------------------------------------------------|
| Runtime       | Java 21                                           |
| Framework     | Spring Boot 4.0.6 (Web MVC, Data JPA, Validation) |
| Database      | H2 (in-memory)                                    |
| Migrations    | Liquibase                                         |
| Mapping       | MapStruct                                         |
| Boilerplate   | Lombok                                            |
| API docs      | SpringDoc OpenAPI (Swagger UI)                    |
| Observability | Spring Boot Actuator                              |
| Build         | Maven (wrapper included)                          |
| Tests         | JUnit 5, MockMvc                                  |

## API overview

All endpoints are served under `/api/posts` with `Content-Type: application/json`. Null fields are omitted from JSON
responses.

| Method | Path                       | Description                                          |
|--------|----------------------------|------------------------------------------------------|
| `GET`  | `/api/posts`               | List posts (paginated summaries with comment counts) |
| `POST` | `/api/posts`               | Create a new post                                    |
| `GET`  | `/api/posts/{id}`          | Get a post with full content and comments            |
| `POST` | `/api/posts/{id}/comments` | Add a comment to a post                              |

**Pagination** (`GET /api/posts`):

- `page` — zero-based page index (default: `0`)
- `size` — items per page (default: `5`, maximum: `10`)

Example response shape:

```json
{
  "content": [
    {
      "id": 1,
      "title": "Hello World",
      "numberOfComments": 2
    }
  ],
  "totalElements": 12,
  "totalPages": 3,
  "number": 0,
  "size": 5
}
```

**Error responses** (`400`, `404`) return a consistent `ErrorResponse` body with `timestamp`, `status`, `error`,
`message`, `path`, and optional `validationErrors`.

## Prerequisites

- **JDK 21** or newer (`java -version`)
- **Git** (optional, for cloning the repository)
- A terminal with network access (Maven wrapper downloads dependencies on first run)

No external database installation is required; the project uses an embedded H2 instance at runtime.

## How to run

### 1. Clone and enter the project

```bash
git clone <repository-url>
cd prosigliere
```

### 2. Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8080**.

### 3. Explore the API

| Resource     | URL                                   |
|--------------|---------------------------------------|
| Swagger UI   | http://localhost:8080/swagger-ui      |
| OpenAPI JSON | http://localhost:8080/api-docs        |
| Health check | http://localhost:8080/actuator/health |
| App info     | http://localhost:8080/actuator/info   |
| H2 console   | http://localhost:8080/h2-console      |

H2 console connection settings (defaults from `application.yaml`):

- **JDBC URL:** `jdbc:h2:mem:prosigliere`
- **Username:** `sa`
- **Password:** *(empty)*

### 4. Quick manual test with curl

Create a post:

```bash
curl -s -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"title":"My first post","content":"Hello from Prosigliere!"}'
```

List posts (first page):

```bash
curl -s "http://localhost:8080/api/posts"
```

Add a comment (replace `{id}` with the post id):

```bash
curl -s -X POST http://localhost:8080/api/posts/1/comments \
  -H "Content-Type: application/json" \
  -d '{"content":"Great read!"}'
```

Get post details:

```bash
curl -s http://localhost:8080/api/posts/1
```

### 5. Run tests

```bash
./mvnw test
```

Integration tests use **MockMvc** against a full Spring context with the in-memory H2 database.

## Project structure

```
src/main/java/dev/ercilio/prosigliere/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Spring Data JPA
├── domain/entity/  # JPA entities
├── dto/            # Request/response records
├── mapper/         # MapStruct mappers
├── exception/      # Global error handling
└── config/         # OpenAPI configuration

src/main/resources/
├── application.yaml
└── db/changelog/   # Liquibase migrations
```

## IMPROVEMENTS

Potential next steps to evolve the project:

- **Production database** — Replace in-memory H2 with PostgreSQL or MySQL and externalize configuration via Spring
  profiles (`dev`, `qa`, `prod`).
- **Environment variables for sensitive data** — Externalize credentials and secrets (database URLs, usernames,
  passwords, API keys) via environment variables or a secrets manager instead of hardcoding them in `application.yaml`.
- **Service-layer unit tests** — Add focused tests with Mockito for `BlogPostService`, complementing the existing
  MockMvc integration tests.
- **Comment listing endpoint** — Expose `GET /api/posts/{id}/comments` with pagination if comments per post can grow
  large.
- **Update and delete operations** — Support editing posts/comments and soft-delete for audit trails.
- **Authentication & authorization** — Secure write endpoints with Spring Security (JWT or OAuth2).
- **API versioning** — Prefix routes with `/api/v1` to allow non-breaking evolution.
- **Docker support** — Add a `Dockerfile` and `docker-compose.yml` for repeatable local and CI environments.
- **CI pipeline** — Run `./mvnw verify` on every push with test coverage reporting.
- **Prometheus metrics via Actuator** — Expose the `/actuator/prometheus` endpoint (Micrometer Prometheus registry) for
  integration with Grafana and broader observability (dashboards, alerting, SLO tracking).
- **Disable Swagger/H2 in production** — Gate interactive docs and the H2 console behind profiles.
- **Caching** — Cache paginated list results (e.g. Caffeine or Redis) for read-heavy workloads.
- **Rate limiting** — Protect public endpoints from abuse.
- **OpenAPI examples** — Enrich SpringDoc annotations with request/response examples for every status code.
