# Sprint Review

Sprint/epic/release: links. Review date: actual date. Revision and dirty state: actual values.

## Outcome and acceptance evidence

Record demonstrated behavior and completed requirement IDs, linking specification tests and the execution plan.
Distinguish implemented, verified, incomplete, and blocked work.

## Commands and environment

Record exact commands, JDK/Maven versions and other relevant runtime settings. Include executed/passed/failed/skipped
totals and disabled-test reasons. Inspect coverage/check outputs; a skipped check is not passing evidence.

Typical commands from the repository root, selected for the work:

    mvn -Dtest=RelevantSpecification test
    mvn clean install
    mvn -Pbenchmarks test

## Example and artifact review

Record relevant Night City assertions and verify that example/test classes are absent from published library artifacts.
For distribution work, link the separate consumer result and distinguish local install from remote availability.

## Design obligations

Record how correctness, actionable errors, failure recovery, resource hygiene, and performance were verified against
the selected capability's design. Link missing evidence and blockers; do not infer one concern from another.

## Performance and resources

Link dated [performance records](../../performance/Readme.md), their baseline comparisons, and any resource checks.
Explain why measurement is inapplicable when it is not needed; do not fabricate previous measurements.

## Remaining work and decisions

Name release blockers, missing evidence, approved deferrals, follow-up epic/sprint links, and design/specification
changes.
Record lessons learned and reconcile backlog, traceability, milestones, and execution-plan outcomes.
