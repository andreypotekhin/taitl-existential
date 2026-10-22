# Sprint 06: V2 Instance Isolation and Configuration

Status: complete. Review date: 2026-10-22. Epic:
[Instance Isolation and Configuration](../Backlog.md#instance-isolation-and-configuration). Release: [V2](../V2.md).
Design readiness: ready for instance isolation, prospective flags, source precedence, freezing, and admitted concurrency.
Verification: complete for the selected Sprint 06 scope; broader runtime support and new source APIs remain deferred.

## Goal and user outcome

Use independent instances and the broader documented configuration options without unintended shared state.

## Dependencies and readiness

V1 meets its release exit criteria before this release delivers new claims. Allow independent investigation and design
ahead of later sprints. Implementation waits only for its actual behavioral and technical dependencies. Select a ready
capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Completed work

- Implemented independent instance flags, rules, configuration finalization, and transaction/event state while keeping
  the explicit JVM-wide `Ex` facade and `Creator` injection boundaries.
- Verified prospective flag changes, first-successful-begin freezing, atomic source application, failure retry, safe
  thread handoff, simultaneous first transactions, and independent instance finalization.
- Retained the V1 consumer and workflow regressions and recorded setup-versus-transaction performance evidence.

## Acceptance

- Independent instances, sources/flags, freeze behavior, and relevant concurrency cases have direct evidence in
  [UserCanAccessLibrary.java] and [UserCanConfigureLibrary.java].
- Failed finalization remains retryable; finalized instances reject changes with a troubleshooting link. Tests close
  independent instances and transactions deterministically.
- Configuration/setup overhead and configured transaction cost have separate three-sample measurements in the
  [performance record].

## Quality and performance

Correctness is covered by conflicting rules/flags across independent instances and by source-atomicity checks.
Actionable errors explain that finalized configuration cannot change and link to troubleshooting. Failed context
factory construction remains retryable; a failed attempt does not publish a partial configuration. Acceptance tests
close each instance and complete or close transactions, including worker cleanup. Performance evidence measures setup
and steady transactions separately; contention is exercised by deterministic concurrency tests, with no numerical gate
until a comparable baseline exists. See the [performance record](../../performance/runs/20261022T213148Z.InstanceIsolationAndConfiguration.md).

## Demo and validation

From the repository root, validation passed:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify
    mvn -f reactor/pom.xml clean install
    mvn -q -Pbenchmarks -Dtest=InstanceConfigurationBenchmark -Dbenchmark.warmup.iterations=50 -Dbenchmark.measure.iterations=250 test

The root build executed 460 tests with zero failures, errors, or skips; Checkstyle reported zero violations, and PMD,
Failsafe, and JaCoCo checks passed (234 classes analyzed). The independent Night City consumer test passed (1/1),
and the three-project reactor install succeeded. The benchmark command ran three times in separate Maven/test JVMs.
The project-specific `-Dtest=UserCanAccessLibrary,...` selector is not recognized by the current Surefire naming
conventions; these specifications were exercised by the complete build.

## Non-goals

Hot reload, new configuration strategy APIs, and untested runtime support promises.

## Plan and review

The [execution plan](../../planning/P10222606.Sprint06-instance-isolation-and-configuration.plan.md) records the
selected behavior and delivery steps. This closes the selected Sprint 06 slice; per-instance configuration source
APIs, concurrent rule declaration, and broader runtime compatibility claims remain deferred.

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[UserCanAccessLibrary.java]: ../../../src/test/java/com/taitl/specs/library/UserCanAccessLibrary.java
[UserCanConfigureLibrary.java]: ../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java
[performance record]: ../../performance/runs/20261022T213148Z.InstanceIsolationAndConfiguration.md

[review template]: ../templates/SprintReview.md
