# Test cases

Coverage of the Players API test assignment. `Automated` points to the class that implements the case;
`Status` is the result against the current test account.

| ID | Area | Scenario | Expected | Automated in | Status |
| --- | --- | --- | --- | --- | --- |
| AUTH-01 | Authentication | Login with valid credentials | `201`, usable token returned | `AuthenticationTest` | pass |
| AUTH-02 | Authentication | Login with a wrong password | `401` | `AuthenticationTest` | pass |
| AUTH-03 | Authentication | Login with an unknown email | `401` | `AuthenticationTest` | pass |
| AUTH-04 | Authentication | Login with an empty payload | `401` | `AuthenticationTest` | pass |
| AUTH-05 | Authentication | Login with swapped email/password values | `401` | `AuthenticationTest` | pass |
| AUTH-06 | Authentication | Login with a created player's credentials | `401` (players are not testers) | `PlayersAccountAccessTest` | pass |
| SEC-01 | Security | Players endpoints without an `Authorization` header | `401` | `PlayersSecurityTest` | pass |
| SEC-02 | Security | Players endpoints with a malformed bearer token | `401` | `PlayersSecurityTest` | pass |
| SEC-03 | Security | Players endpoints with a non-bearer scheme | `401` | `PlayersSecurityTest` | pass |
| FLOW-01 | Assignment flow | Create 12 players, read one, list and sort by name, delete, verify they are gone | All steps succeed | `PlayersCrudFlowTest` | pass |
| FLOW-02 | Assignment flow | Create 101 players and read them back | Every created player is returned | `PlayersBulkTest` | pass |
| VAL-01 | Validation | Created player is returned with the values that were sent | Response matches the request and schema | `PlayersValidationTest` | pass |
| VAL-02 | Validation | Values at the edge of the field rules | Values are stored as provided | `PlayersValidationTest` | pass |
| VAL-03 | Validation | Duplicate email | Current API accepts it (defect D-06) | `PlayersValidationTest` | documented |
| VAL-04 | Validation | Lookup of an unknown email | `400` on the current API (expected `404`, defect D-07) | `PlayersValidationTest` | documented |
| VAL-05 | Validation | Delete with an unknown id | `400` on the current API (expected `404`, defect D-09) | `PlayersValidationTest` | documented |
| VAL-06 | Validation | Delete of a player created in the test | `200` and the record is gone | `PlayersValidationTest` | pass |
| VAL-07 | Validation | Delete without an id | `404` (no route) | `PlayersValidationTest` | pass |
| CON-01 | Contract | Published OpenAPI document describes the used endpoints | Document reachable and complete | `OpenApiContractTest` | pass |
| KDI-01 | Known defect | Login should answer `200` | `200` | `PlayersKnownDefectsTest` | fails (D-01) |
| KDI-02 | Known defect | Login should return the documented token payload | `access_token`, `token_type`, `expires_in`, `scope` | `PlayersKnownDefectsTest` | fails (D-02) |
| KDI-03 | Known defect | Create should not echo the password | No password in the response | `PlayersKnownDefectsTest` | fails (D-04) |
| KDI-04 | Known defect | Create should reject missing required fields | `400` | `PlayersKnownDefectsTest` | fails (D-05) |
| KDI-05 | Known defect | Duplicate email is a conflict | `409` | `PlayersKnownDefectsTest` | fails (D-06) |
| KDI-06 | Known defect | Unknown lookup returns `404` | `404` | `PlayersKnownDefectsTest` | fails (D-07) |
| KDI-07 | Known defect | Unknown delete returns `404` | `404` | `PlayersKnownDefectsTest` | fails (D-09) |
| KDI-08 | Known defect | The same id cannot be deleted twice | second call `404` | `PlayersKnownDefectsTest` | fails (D-10) |
| KDI-09 | Known defect | Accounts of other testers are not readable | `403`/`404` | `PlayersKnownDefectsTest` | fails (D-11) |

## Not automated

- Performance and load behaviour of `getAll` (out of scope for this assignment).
- UI behaviour of the assignment page.
- HTTP Basic credentials from the invitation, since the API does not require them (defect D-15).

## Running the known defects

They are excluded from the default run so the suite stays green:

```bash
mvn test -DexcludedGroups= -Dgroups=known-issues
```
