# Players API automated tests

Automated tests for the Slotegrator Players API test assignment, written in Java 21 with RestAssured and JUnit 5.

## Tech stack

- Java 21
- Maven
- JUnit 5
- RestAssured (with JSON Schema Validator)
- AssertJ
- Docker / Docker Compose

## Project layout

```
src/test/java
├── api          REST clients (base client, auth endpoint, players endpoints)
├── config       Environment based test configuration
├── model        Request/response models
├── testdata     Test data builders
├── tests        Test classes grouped by concern
└── util         Shared assertions
src/test/resources/schemas   JSON schemas used for response validation
```

## Covered scenarios

### Assignment flow (`smoke`, `players`)

1. `POST /api/tester/login` returns a bearer token.
2. `POST /api/automationTask/create` creates 12 players.
3. `POST /api/automationTask/getOne` returns the profile of a created player.
4. `GET /api/automationTask/getAll` returns the created players, sorted by name.
5. `DELETE /api/automationTask/deleteOne/{id}` removes the created players.
6. `GET /api/automationTask/getAll` no longer contains them.

### Authentication (`authentication`)

- Valid credentials return a token that matches the documented schema.
- Unknown email and wrong password are rejected with `401`.
- Missing or malformed credentials are rejected with a client error.

### Validation (`validation`)

- Required fields: username, email, password, password confirmation.
- Field rules: username and password minimum length, password confirmation match, email format, duplicate email.
- Negative input: empty, null, whitespace and special characters.
- Boundary input: minimum length, oversized values.
- Lookup and deletion of unknown players return `404`.

### Security (`security`)

- Protected players endpoints reject requests without a token, with a malformed token, and with a non-bearer scheme.

### Contract (`contract`)

- The published OpenAPI document is reachable and still describes the endpoints and security schemes used here.

## Configuration

The tests read their configuration from environment variables:

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `BASE_URL` | no | `https://testslotegrator.com` | API base URL |
| `TESTER_EMAIL` | yes | - | Tester account email |
| `TESTER_PASSWORD` | yes | - | Tester account password |
| `DEFAULT_CURRENCY` | no | `USD` | Currency used for generated players |
| `GET_ONE_EXPECTED_STATUS` | no | `201` | Expected status for `getOne`; the task description says `200`, the published OpenAPI document says `201` |

Create a local `.env` file from `.env.example` and fill in the credentials:

```bash
cp .env.example .env
```

## Running the tests

With Docker Compose:

```bash
docker compose run --rm api-tests
```

Or with a local Maven installation:

```bash
mvn test
```

Run a single group of tests with a JUnit tag, for example:

```bash
mvn test -Dgroups=security
```

## Continuous integration

`.github/workflows/api-tests.yml` runs the suite on pushes and pull requests to `master`/`main` and can also be started manually. Credentials are provided through repository secrets, so no account data is stored in the repository:

- secrets: `TESTER_EMAIL`, `TESTER_PASSWORD`
- variables (optional): `DEFAULT_CURRENCY`, `GET_ONE_EXPECTED_STATUS`

Surefire reports are uploaded as a build artifact.
