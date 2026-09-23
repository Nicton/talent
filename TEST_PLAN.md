# Test plan and roadmap

The assignment flow and the reachable behaviour of the current API are covered by the automated suite.
The list below is the backlog of cases that are **deliberately not implemented yet**.

## Why the backlog is on hold

The API currently performs no input validation at all (see `DEFECTS.md`: D-04, D-05, D-06, D-18, D-19, D-20).
Field level checks written against that behaviour would only repeat the same failures and add noise, so they are
queued until the basic validation is fixed. The same applies to authorization boundaries (D-11), which currently
let any tester read every account.

## Backlog

| ID | Area | Case | Blocked by |
| --- | --- | --- | --- |
| TP-01 | Validation | Username boundaries: `min-1`, `min`, `min+1`, max length, unicode, reserved characters | D-05 |
| TP-02 | Validation | Password policy: minimum length, complexity, maximum length, unicode, whitespace only | D-05, D-18 |
| TP-03 | Validation | Email normalisation: case, plus addressing, IDN, maximum length, leading/trailing spaces | D-20 |
| TP-04 | Validation | `currency_code` against the supported currency list, case sensitivity | D-19 |
| TP-05 | Listing | `getAll` page size, offset, sorting and filtering parameters once pagination exists | D-14 |
| TP-06 | Booking rules | Duplicate username and duplicate email must not create two accounts (transactional guarantee) | D-06 |
| TP-07 | Concurrency | Parallel create/delete of the same player: no partial state, no double spending of the email | D-06 |
| TP-08 | Authentication | Token lifetime, refresh, revocation, scopes, brute force / rate limiting | D-01, D-02 |
| TP-09 | Authorisation | A tester must not read or modify players owned by another tester | D-11 |
| TP-10 | Delete | Unknown and already deleted ids return `404`, soft vs hard delete behaviour | D-09, D-10, D-17 |
| TP-11 | Error contract | Consistent error payload and status mapping across endpoints | D-01, D-07, D-09 |
| TP-12 | Sensitive data | Responses must never contain password fields | D-04 |
| TP-13 | Contract | `id` type consistency between endpoints and the published schema | D-12, D-13 |
| TP-14 | Design | Reads should use safe methods (`GET`) with path/query parameters instead of `POST` | D-16 |
| TP-15 | Persistence | Values survive a create -> getAll -> getOne round trip, including optional fields | - |
| TP-16 | Performance | Response time baseline for bulk create and listing (out of assignment scope) | - |

## Exit criteria

1. Everything in the matrix above is automated and green.
2. The main suite and the known-defect suite are both green, so the known-defect suite can be dropped.
3. The pipeline blocks on any failure in either suite.
