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

### Assignment flow (`players`)

1. `POST /api/tester/login` returns a bearer token.
2. `POST /api/automationTask/create` creates 12 players.
3. `POST /api/automationTask/getOne` returns the profile of a created player.
4. `GET /api/automationTask/getAll` returns the created players, sorted by name.
5. `DELETE /api/automationTask/deleteOne/{id}` removes the created players.
6. `GET /api/automationTask/getAll` no longer lists them.

### Authentication (`authentication`)

- Valid credentials return a token and the authenticated user.
- Unknown email, wrong password and an empty payload are rejected with `401`.

### Security (`security`)

- Players endpoints reject requests without a token, with a malformed token and with a non-bearer scheme (`401`).

### Validation (`validation`)

- Created players are read back with the values that were sent.
- Values at the edge of the documented field rules are stored as provided.
- Duplicate emails are currently accepted by the test API, which is pinned by a test.
- Lookup and deletion of an unknown record are reported as a client error (`400`).

### Contract (`contract`)

- The published OpenAPI document is reachable and still describes the endpoints and security schemes used here.

### Known defects (`known-issues`)

- `PlayersKnownDefectsTest` describes how the API *should* behave. Every failure is a defect listed in
  [`DEFECTS.md`](DEFECTS.md), and `TEST_CASES.md` maps the cases to the classes that implement them.
- The tag is excluded from the default run. Run it explicitly:

```bash
mvn test -Dexcluded.groups= -Dgroups=known-issues
```

- The pipeline runs both suites, reports them separately and fails when either one is red.
- Cases that are intentionally not implemented yet are listed in [`TEST_PLAN.md`](TEST_PLAN.md).

## Notes on the live API

The test account API behaves slightly differently from the assignment text, and the tests follow the live behaviour:

| Call | Assignment text | Live API |
| --- | --- | --- |
| `POST /api/tester/login` | `200` | `201`, token in `accessToken` |
| `POST /api/automationTask/create` | `201` | `201`, created document in `_id` |
| `POST /api/automationTask/getOne` | `200` | `201`, document in `id` |
| `GET /api/automationTask/getAll` | `200` | `200` |
| `DELETE /api/automationTask/deleteOne/{id}` | `200` | `200` |
| Unknown lookup / delete | - | `400` |

The `getOne` status used by the tests can be overridden with `GET_ONE_EXPECTED_STATUS`.

## Configuration

The tests read their configuration from environment variables:

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `BASE_URL` | no | `https://testslotegrator.com` | API base URL |
| `TESTER_EMAIL` | yes | - | Tester account email |
| `TESTER_PASSWORD` | yes | - | Tester account password |
| `DEFAULT_CURRENCY` | no | `USD` | Currency used for generated players |
| `GET_ONE_EXPECTED_STATUS` | no | `201` | Expected status for `getOne` |

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

## Reports

Every run produces the usual Surefire output and a small summary:

- `target/surefire-reports` — raw XML and text results per test class;
- `target/reports/surefire.html` — readable HTML report;
- the job summary on the workflow run page — a table with tests, failures, errors and skipped per class.

The HTML report can also be generated locally after a test run:

```bash
mvn surefire-report:report-only
```
