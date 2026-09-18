# Players API automated tests

Java API test framework for the Slotegrator Players API test assignment.

## Tech stack

- Java 21
- Maven
- JUnit 5
- RestAssured
- AssertJ
- JSON Schema Validator
- Docker / Docker Compose

## Covered scenarios

### Main assignment flow

1. Login via `POST /api/tester/login` and verify token response.
2. Create 12 players via `POST /api/automationTask/create`.
3. Verify created player profile via `POST /api/automationTask/getOne`.
4. Fetch all players via `GET /api/automationTask/getAll` and sort the created players by name.
5. Delete created players via `DELETE /api/automationTask/deleteOne/{id}`.
6. Fetch all players again and verify the created players are absent.

### Additional coverage

- Invalid login is rejected.
- Protected players endpoints reject invalid bearer token.
- Player creation response matches the documented schema.
- Basic validation checks for username and password length.
- Duplicate email registration is rejected.
- Automatic cleanup deletes created test data after each test.

## Configuration

The tests are configured through environment variables:

| Variable | Required | Default | Description |
| --- | --- | --- | --- |
| `BASE_URL` | no | `https://testslotegrator.com` | API base URL |
| `TESTER_EMAIL` | yes | - | Tester account email |
| `TESTER_PASSWORD` | yes | - | Tester account password |
| `DEFAULT_CURRENCY` | no | `USD` | Currency used for generated players |
| `GET_ONE_EXPECTED_STATUS` | no | `201` | Expected status for `getOne`; the assignment text says `200`, OpenAPI says `201` |

Create a local `.env` file from `.env.example`:

```bash
cp .env.example .env
```

Then fill in the credentials.

## Run with Docker

```bash
docker compose run --rm api-tests
```

## Run with Maven locally

```bash
mvn test
```

## Notes

- Generated player emails use the reserved `example.test` domain to avoid real mailboxes.
- The API documentation contains a visible inconsistency: the task text expects `200` for `getOne`, while OpenAPI declares `201`. The expected status is configurable through `GET_ONE_EXPECTED_STATUS`.
- The main flow asserts that `getAll` is empty after deleting the created players, as requested in the assignment. If the test account is not isolated and already contains data, clean the environment first or use a dedicated tester account.
