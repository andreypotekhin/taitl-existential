# Capability Traceability

The [roadmap](Roadmap.md) plans outcomes across three releases. The [rebaseline](ReleaseRebaseline.md) maps all 36
original scope items here. Topic requirement IDs and inherited completion marks are preserved; no entry below
claims that newly planned acceptance has passed.

Design readiness and implementation verification are separate. `open` means the remaining selected-capability
decisions/acceptance are not finalized; `partial` means relevant existing tests/implementation exist; `missing` means
the required evidence is absent. Narrower reviewed slices can become ready without settling the entire topic.
Historical evidence is in [Assessment](Assessment.md). Tests linked below are starting evidence, not proof of every
listed requirement. The concrete selected requirement IDs and test methods must be reconciled when work is admitted.

## V1-C01: Ordinary configuration and class rules

Epic: [V1-E01](Backlog.md#v1-e01); [Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).
Design readiness: open. Verification: partial.

Requirements: LC-001–002, LC-004, LC-006, LC-015, LC-020, LC-026, LC-029–031; TE-001; EC-009–013 (selected class-rule
cases).
Topics: [LC], [TE].
Evidence to review: [UserCanConfigureEntities.java][wrap-01].
Remaining work: Finalize the exact-context Night City walkthrough, defaults, rule timing, and acceptance cases.

## V1-C02: Reported events and commit validation

Epic: [V1-E01](Backlog.md#v1-e01); [Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).
Design readiness: open. Verification: partial.

Requirements: TE-004–006, TE-008, TE-010–015, TE-017–018, TE-026, TE-029–030, TE-034, TE-036, TE-042, TE-044;
EC-001–006 and QI-013–015 (selected entity/commit paths).
Topics: [TE], [EC], [QI].
Evidence to review: [UserCanSendEntityEvents.java][wrap-02].
Remaining work: Verify the selected event/deduplication/validation path without assuming the full advanced-stage
contract.

## V1-C03: Basic All/Exists relationship

Epic: [V1-E02](Backlog.md#v1-e02); [Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md).
Design readiness: open. Verification: partial.

Requirements: QI-001–003, QI-005–009; EC-016–019 (representative collection-backed relationship).
Topics: [QI], [EC].
Evidence to review: [quantifiers](../../../src/test/java/com/taitl/existential/quantifiers/).
Remaining work: Add commit-level success, missing relationship, and selected empty/conditional cases.

## V1-C04: Explicit entity evolution

Epic: [V1-E02](Backlog.md#v1-e02); [Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md).
Design readiness: open. Verification: partial.

Requirements: EC-007–008, EC-020–021 (explicit before/after values).
Topics: [EC].
Evidence to review: [UserCanUseMemoForBiEvents.java][wrap-03].
Remaining work: Define and verify an allowed and rejected change; memo/type/subclass breadth belongs to V2.

## V1-C05: Useful violations and errors

Epic: [V1-E03](Backlog.md#v1-e03); [Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).
Design readiness: open. Verification: partial.

Requirements: EC-024–025, EC-027; V1 validation-report requirements.
Topics: [EC], [TE].
Evidence to review: [EvaluationLogicTest.java][wrap-04].
Remaining work: Repair disabled report cases and prove rule/operation details and remedies on selected failure paths.

## V1-C06: Recovery and resource release

Epic: [V1-E03](Backlog.md#v1-e03); [Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).
Design readiness: open. Verification: partial.

Requirements: TE-004–006, TE-011; HK-003–006 (admitted transaction/library exits).
Topics: [TE], [HK].
Evidence to review: [TrRegistryTest.java][wrap-05].
Remaining work: Specify post-failure state, recovery, retained references, and cleanup without implying caller-object
undo.

## V1-C07: Night City as an artifact consumer

Epic: [V1-E04](Backlog.md#v1-e04); [Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).
Design readiness: open. Verification: missing.

Requirements: DIST-001 (local Maven dependency consumption).
Topics: [DIST].
Evidence to review: not yet available.
Remaining work: Create Night City POM/demo; verify against the installed JAR without root source/test class directories.

## V1-C08: Representative performance and hardening

Epic: [V1-E04](Backlog.md#v1-e04); [Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).
Design readiness: open. Verification: partial.

Requirements: All V1 capabilities and their five quality design obligations; final gate V1-E05.
Topics: [LC], [TE], [QI], [HK], [DIST].
Evidence to review: [CustomTransactionBenchmark.java][wrap-06].
Remaining work: Add representative workload/cardinality and resource evidence; historical timings are not full workflow
proof.

## V2-C01: Independent library instances

Epic: [V2-E01](Backlog.md#v2-e01); [Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).
Design readiness: open. Verification: partial.

Requirements: LC-003–006 (independent-instance and broader operation/context combinations).
Topics: [LC].
Evidence to review: [UserCanAccessLibrary.java][wrap-07].
Remaining work: Define per-instance versus global facilities and replace the existing isolation TODOs with direct
acceptance.

## V2-C02: Configuration options and guarantees

Epic: [V2-E01](Backlog.md#v2-e01); [Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).
Design readiness: open. Verification: partial.

Requirements: LC-007–013; configuration/injection concurrency and freeze decisions.
Topics: [LC].
Evidence to review: [UserCanConfigureLibrary.java][wrap-08].
Remaining work: Retain existing cases and finalize sources/precedence/flags, failed initialization, and supported
concurrency.

## V2-C03: Contexts, factories, and ordering

Epic: [V2-E02](Backlog.md#v2-e02); [Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).
Design readiness: open. Verification: partial.

Requirements: LC-014, LC-016–019, LC-021–025, LC-030–031 (broader combinations).
Topics: [LC].
Evidence to review: [configuring_contexts][wrap-09].
Remaining work: Resolve overlapping wildcard/parent/exact precedence and factory/builder edge cases.

## V2-C04: Generic/subclass and constraint breadth

Epic: [V2-E02](Backlog.md#v2-e02); [Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).
Design readiness: open. Verification: partial.

Requirements: LC-028; EC-009–015 (remaining combinations), EC-023.
Topics: [LC], [EC].
Evidence to review: [TypeKeyTest.java](../../../src/test/java/com/taitl/existential/keys/TypeKeyTest.java).
Remaining work: Specify hierarchy/generic matching and third-party/collection/field combinations beyond the V1 example.

## V2-C05: Custom transactions and access patterns

Epic: [V2-E03](Backlog.md#v2-e03); [Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).
Design readiness: open. Verification: partial.

Requirements: LC-027, LC-032–035; TE-002–003, TE-009–010, TE-020–023; EC-022.
Topics: [LC], [TE], [EC].
Evidence to review: [custom_transactions][wrap-10].
Remaining work: Verify factories, supplied instances, access rules, memo behavior, and per-call values under the full
contract.

## V2-C06: Built-in stages, intents, and effects

Epic: [V2-E03](Backlog.md#v2-e03); [Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).
Design readiness: open. Verification: partial.

Requirements: TE-016, TE-019, TE-024–025, TE-027–033, TE-037–044 (broader timing); all IE requirements for built-in
paths.
Topics: [TE], [IE].
Evidence to review: [UserCanConfigureIntents.java][wrap-11].
Remaining work: Reconcile stage terminology and cover order, partial effects, authorization failures, and recovery.

## V2-C07: Maintained relationships and indexing

Epic: [V2-E04](Backlog.md#v2-e04); [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: QI-004, QI-010–015 and remaining All/Exists combinations; retain internal indexing needed by V1.
Topics: [QI].
Evidence to review: [indexes](../../../src/test/java/com/taitl/exlogic/unused/indexes/).
Remaining work: Reconcile public index names and verify maintenance plus lookup/scaling and transaction-level results.

## V2-C08: Broader lifecycle and event combinations

Epic: [V2-E04](Backlog.md#v2-e04); [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: TE-007, TE-017, TE-035, TE-042 and remaining lifecycle combinations; EC-001–008, EC-020–022 beyond the V1
slice.
Topics: [TE], [EC].
Evidence to review: [UserCanInitiateTransactions.java][wrap-12].
Remaining work: Specify checkpoint reset/continuation, nested independence, additional values, and broad event
combinations.

## V2-C09: Richer reports, caching, and global hygiene

Epic: [V2-E04](Backlog.md#v2-e04); [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: EC-026, EC-028; HK-001–007 (broader resource/cache/global/leak-reporting guarantees).
Topics: [EC], [HK].
Evidence to review: [TypeKeyCacheTest.java](../../../src/test/java/com/taitl/ex/logic/events/TypeKeyCacheTest.java).
Remaining work: Verify metadata, description options, invalidation, boundedness, global close, and admitted leak
reporting.

## V2-C10: Publication readiness

Epic: [V2-E04](Backlog.md#v2-e04); [Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: missing.

Requirements: DIST-001 remote distribution prerequisites; remote availability requires its own evidence.
Topics: [DIST].
Evidence to review: not yet available.
Remaining work: Record artifacts/destination/account/signing prerequisites; do not block an unclaimed local-only channel
on publication.

## V3-C01: Custom event contracts and registration

Epic: [V3-E01](Backlog.md#v3-e01); [Sprint 11](sprints/Sprint11_V3CustomEventContractsAndRegistration.md).
Design readiness: open. Verification: missing.

Requirements: EC-029–032, EC-034 and custom registration/matching guarantees.
Topics: [EC].
Evidence to review: not yet available.
Remaining work: Define registration identity, collisions, ownership, invalid inputs, and relevant concurrency.

## V3-C02: Custom event evaluation and extensions

Epic: [V3-E02](Backlog.md#v3-e02); [Sprint 12](sprints/Sprint12_V3CustomEventEvaluationAndExtensions.md).
Design readiness: open. Verification: missing.

Requirements: EC-033–034 and the remaining custom-event invocation claim; custom paths through IE/TE/HK.
Topics: [EC], [IE], [TE], [HK].
Evidence to review: not yet available.
Remaining work: Specify splitting, matching, authorization, callbacks, failure recovery, expansion costs, and cleanup.

## Release hardening gates

[V1-E05](Backlog.md#v1-e05), [V2-E05](Backlog.md#v2-e05), and [V3-E03](Backlog.md#v3-e03) verify their complete admitted
capabilities and retained regressions. Actual coverage, required tests, five design obligations, performance/resource
records, consumer results, and artifact checks must agree before closure. These gates do not add feature scope.

[LC]: ../specifications/LibraryAndConfiguration.spec.md
[TE]: ../specifications/TransactionsAndEvaluation.spec.md
[EC]: ../specifications/EventsAndConstraints.spec.md
[QI]: ../specifications/QuantifiersAndIndexes.spec.md
[IE]: ../specifications/IntentsAndEffects.spec.md
[HK]: ../specifications/Housekeeping.spec.md
[DIST]: ../specifications/Distribution.spec.md

[wrap-01]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java
[wrap-02]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java
[wrap-03]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java
[wrap-04]: ../../../src/test/java/com/taitl/ex/logic/evaluation/EvaluationLogicTest.java
[wrap-05]: ../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java
[wrap-06]: ../../../src/test/java/com/taitl/existential/benchmarks/CustomTransactionBenchmark.java
[wrap-07]: ../../../src/test/java/com/taitl/specs/library/UserCanAccessLibrary.java
[wrap-08]: ../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java
[wrap-09]: ../../../src/test/java/com/taitl/specs/configuration_workflow/configuring_contexts/
[wrap-10]: ../../../src/test/java/com/taitl/specs/configuration_workflow/custom_transactions/
[wrap-11]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureIntents.java
[wrap-12]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java
