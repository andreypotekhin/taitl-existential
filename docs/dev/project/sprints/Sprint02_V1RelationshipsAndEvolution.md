# Sprint 02: V1 Relationships and Evolution

Status: complete. Dates/capacity: unassigned. Epic:
[Relationships and Evolution](../Backlog.md#relationships-and-evolution). Release: [V1](../V1.md).
Design readiness: the selected V1 collection/map composition and explicit evolution contract is recorded.
Verification: all selected acceptance and required build checks passed; V1 closeout was still pending at this sprint's
completion and finished in Sprint 05.

## Goal and user outcome

Validate a representative relationship with basic All/Exists and reject an invalid before/after entity change.

## Dependencies and readiness

Review [Sprint 01](Sprint01_V1NightCityCoreWorkflow.md) for actual dependencies and evidence. Allow independent
investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and technical
dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].

Link the exact requirement sections and acceptance evidence for the selected slice in the existing plan/traceability
entry.

## Delivered scope

Caller-owned collection membership and cat-to-selected-dwelling maps use All-Exists composition, including conditional
membership, equal snapshots, matching source object identity, empty/missing/null/wrong-location rejection, caller
source repair, and explicit rollback. Shared ConfigureCats fixtures apply the same birth/state/evolution rules.
Explicit Transit/Port endpoints cover relocation/create/delete and fail-early corrected-input recovery.

Default set/map membership now delegates to source lookup without a redundant stored-key scan. Predicate-bearing
sources retain original matched-object semantics. Night City value equality now includes class/color/location and
handles null values consistently. Closed transaction handles clear their runtime event/type indexes; operation-aware
validation errors and endpoint remedies are documented. These targeted corrections were exposed by selected tests.

## Acceptance

- The representative relationship and evolution cases pass end to end with direct requirement evidence.
- Failure output identifies the violated rule and relevant operation; resources are released as specified.
- Collection/matching work has stated cardinalities and a measured baseline.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure collection cardinalities and evolution checks, including duplicate-event behavior on the admitted path. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

From the repository root:

    mvn -Dtest=UserCanConstrainEntityRelationships,UserCanCreateAndRelocateCats,UserCanValidateEntityQuantifiers test
    mvn '-Dtest=ExistsTest*' test
    mvn -q -Pbenchmarks -Dtest=RelationshipBenchmark -Dbenchmark.warmup.iterations=2000 -Dbenchmark.measure.iterations=10000 test
    mvn clean install

The [Night City guide](../../../../examples/night_city/Readme.md#caller-supplied-relationships-and-evolution) gives the
caller-supplied map walkthrough. The [execution plan](../../past/planning/P10212602.Sprint02-relationships-and-evolution.plan.md)
records regression evidence, discoveries, exact commands, and limits.

## Non-goals

Full generic/subclass combinations, maintained joins/indexes, and additional event payload customization.

## Plan and review

Review completed 2026-10-21 at revision `9a4dab73628b99fc09b446dc9427b14b0db8e928` with a dirty working tree.
Amazon Corretto 17.0.15; Maven 3.9.10; macOS 15.6.1 arm64. Final `mvn clean install` ran 445 tests, zero failures/errors,
three existing disabled EvaluationLogicTest report cases. Checkstyle reported zero violations; PMD/Failsafe completed;
JaCoCo analyzed 231 executable classes with actual data/report and passed checks. Local installation passed. Both
ordinary and assembled library JARs exclude example/spec-test classes; standalone consumer verification remains Sprint 04.

Correctness: [15 relationship/evolution cases](../../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java),
[existing create/relocation rejection](../../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java),
and 26 quantifier-dispatch cases. Diagnostics identify operation/rules and link remedies. Recovery verifies rejected
input stays unindexed, rejected commit detaches lookup, and explicit rollback releases runtime references without
undoing source maps. Resource assertions inspect all three runtime index collections through retained closed handles.
These claims do not establish arbitrary callback/library-close/global-cache behavior.

[Cardinality performance evidence](../../performance/runs/20261022T013657Z.RelationshipsAndEvolution.md) records three
fresh-JVM baseline and after samples at 16/256/4,096 entries, source hashes, ranges, algorithmic evidence, and limits.
Membership-only set/map scans are eliminated; general key/value predicates retain the source-object recovery scan.
Four deterministic tests verify no stored-key iteration. Timing does not establish allocation or global resource bounds.

Sprint 02 is complete for its admitted V1 scope. Broader multiplicity, maintained indexes, type/transition-object
breadth, and global caches remain later capabilities. At Sprint 02 close, Sprint 03 was the next planned step for
disabled reports and broader failure/close/resource obligations; Sprint 05 later completed V1 closeout.

[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md

[review template]: ../templates/SprintReview.md
