# Defects found in the Players API

Findings collected while automating the Players API against the test account on `https://testslotegrator.com`.
Every item was reproduced against the live service; the automated checks that pin the expected behaviour live in
`PlayersKnownDefectsTest` (tag `known-issues`, excluded from the default run).

| ID | Area | Expected | Actual | Severity |
| --- | --- | --- | --- | --- |
| D-01 | Login | `POST /api/tester/login` answers `200` (task text and the documented flow) | `201 Created` | Medium |
| D-02 | Login | Body matches the documented `TokenDTO` (`access_token`, `token_type`, `expires_in`, `scope`) | Body is `{ "user": {...}, "accessToken": "..." }` | High |
| D-03 | Login | A failed login is rejected | Works, but the response also exposes the full account profile (role, position, report, feedback, timestamps) that is not part of the login contract | Low |
| D-04 | Create | Response contains the created player only | Response echoes `password_change` and `password_repeat` in clear text | **High** |
| D-05 | Create | Missing/invalid required fields are rejected with `400` | Request with `null` username and email is accepted with `201` | High |
| D-06 | Create | Duplicate email is rejected with `409 Conflict` | Duplicate email is accepted with `201`; two accounts share one email | **High** |
| D-07 | Lookup | Unknown email returns `404` | Returns `400 Bad Request` | Medium |
| D-08 | Lookup | `getOne` with `email: null` is rejected | Returns `201` with an unrelated record whose email is `null` | Medium |
| D-09 | Delete | Unknown id returns `404` | Returns `400 Bad Request` | Medium |
| D-10 | Delete | A second delete of the same id returns `404` | Returns `200 OK` again | Medium |
| D-11 | Isolation | Accounts are visible only to their owner | `getAll` returns the shared collection (other testers' accounts and seeded data such as `email1@test.com`); `getOne` reads a foreign account with `201` | **High (privacy)** |
| D-12 | Contract | `create` and `getOne`/`getAll` describe the same resource | `create`/`deleteOne` return the document as `_id`, `getOne`/`getAll` return it as `id` | Medium |
| D-13 | Contract | `PlayerResponseDTO.id` is documented as `integer` | The value is a string | Low |
| D-14 | Listing | Large collections are paginated | `GET /api/automationTask/getAll` returns the whole collection (verified with 101 additional players, 175 rows, no paging) | Medium |
| D-15 | Security | `BasicAuth` declared in the specification is enforced | The API answers without HTTP Basic credentials | Low |
| D-16 | Design | A read uses a safe method | `getOne` is a `POST` for a read operation | Low |
| D-17 | Delete | Malformed or unknown ids are handled consistently | `abc` and `missing-123` return `400`, while `not-a-number` and `000000000000000000000000` return `200 OK` | Medium |

## Notes

- D-11 also means the last step of the task ("verify the list is empty") cannot be asserted globally: the
  collection is shared, so tests can only verify that the players they created are gone.
- D-05/D-06 are the reason the field validation checks in `PlayersValidationTest` document the current
  behaviour instead of asserting rejections.
