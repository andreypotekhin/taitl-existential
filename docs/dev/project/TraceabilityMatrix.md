# Capability Traceability

The [roadmap](Roadmap.md) plans outcomes across three releases. The [rebaseline](ReleaseRebaseline.md) maps all 36
original scope items here. Topic requirements and historical completion marks are preserved; completion claims are
backed by the direct acceptance evidence linked in each selected V1 section.

Design readiness and implementation verification are separate. `open` means the remaining selected-capability
decisions/acceptance are not finalized; `partial` means relevant existing tests/implementation exist; `missing` means
the required evidence is absent. Narrower reviewed slices can become ready without settling the entire topic.
Historical evidence is in [Assessment](Assessment.md). Evidence links identify the acceptance tests for each completed
slice; broader behavior retains its existing readiness and verification status.

## Ordinary configuration and class rules

Epic: [Night City Core Workflow](Backlog.md#night-city-core-workflow);
[Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).
Design readiness: ready for selected scope. Verification: partial for the wider capability.

Requirements: Selected ordinary configuration and class-rule cases.

- [LibraryAndConfiguration.spec.md]: [Static facade access], [Singleton access], [Multiple business operations],
  [Multiple context rules], [Operation keys], [Nonempty contexts], [Class event invariants], [Library facade classes],
  [Operation configuration builders], [Context configuration classes].
- [TransactionsAndEvaluation.spec.md]: [Class rule configuration].
- [EventsAndConstraints.spec.md]: [Internal field constraints], [External field constraints],
  [Third-party field constraints], [Two-field constraints], [Multiple-field constraints].

Evidence: [UserCanConfigureEntities.java](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java)
and [creating and relocating cats](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).
Remaining work: broader configuration options remain in their later V1/V2 scope.

## Reported events and commit validation

Epic: [Night City Core Workflow](Backlog.md#night-city-core-workflow);
[Sprint 01](sprints/Sprint01_V1NightCityCoreWorkflow.md).
Design readiness: ready for selected scope. Verification: verified for Sprint 01 paths; broader stage behavior remains partial.

Requirements: Selected entity/commit paths; broader stage behavior remains in V2.

- [TransactionsAndEvaluation.spec.md]: [Starting a transaction], [Committing a transaction],
  [Rolling back a transaction], [Reporting entity modifications], [Reporting through transaction objects],
  [Events require a transaction], [Events require configured rules], [Library usage phases], [Configuration phase],
  [Execution phase], [Validation phase triggers], [Constraint validation and reporting], [Default validation stage],
  [Rule execution order], [Event handler order], [Validation evaluation naming], [Transaction event scope],
  [Commit and checkpoint validation], [Validation event deduplication].
- [EventsAndConstraints.spec.md]: [Reporting class events], [Equal event deduplication],
  [Events require library configuration], [Events require a started transaction], [Events after transaction completion],
  [Configuration after the first event].
- [QuantifiersAndIndexes.spec.md]: [Configuration finalization], [Configuration rule indexing],
  [Runtime event indexing].

Evidence: [UserCanInitiateTransactions.java](../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java),
[UserCanSendEntityEvents.java](../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java),
[UserCanConfigureEntities.java](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java),
and [UserCanUseAnnotatedOperations.java](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseAnnotatedOperations.java).
Remaining work: advanced stage and broader event/deduplication claims remain outside Sprint 01.

## Basic All/Exists relationship

Epic: [Relationships and Evolution](Backlog.md#relationships-and-evolution);
[Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md).
Design readiness: ready for entity expression dispatch and the representative cat/dwelling slice. Verification:
fluent All/Exists dispatch, admitted event endpoints, ordering, source matching, and checkpoint/commit rejection are verified.

Requirements: A representative collection-backed relationship.

- [QuantifiersAndIndexes.spec.md]: [All entity invariants], [Conditional All invariants], [All on entity classes],
  [Exists entity invariants], [Conditional same-class existence], [Conditional cross-class existence],
  [All-Exists composition], [Exists on collections].
- [EventsAndConstraints.spec.md]: [Same-class entity relationships], [Cross-class entity relationships],
  [Same-class existence constraints], [Cross-class existence constraints].

Evidence: [cat and dwelling relationship acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java)
and [entity quantifier acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java),
plus [quantifier unit cases](../../../src/test/java/com/taitl/existential/quantifiers/).
The [Sprint 02 collection/map composition cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java)
verify caller sources, conditional membership, missing/empty/null/mismatched relations, and source-object semantics.
The [dispatch defect](../past/todo/T10212601.Quantifier-rules-skipped-by-event-dispatch.md) is repaired; the cat workflow
uses fluent rules with direct missing-destination and missing-location rejection tests.
See [dispatch performance evidence](../performance/runs/20261021T223641Z.QuantifierDispatch.md).
See [Sprint 02 cardinality evidence](../performance/runs/20261022T013657Z.RelationshipsAndEvolution.md) for pure membership
scan elimination and the retained predicate-source scan. Sprint 02's selected V1 relationship scope is complete.
Remaining work: broader multiplicity, maintained-index behavior, and transition-object quantification remain in V2.

## Explicit entity evolution

Epic: [Relationships and Evolution](Backlog.md#relationships-and-evolution);
[Sprint 02](sprints/Sprint02_V1RelationshipsAndEvolution.md).
Design readiness: ready for the explicit Cat transit slice. Verification: verified for allowed and rejected color changes;
broader type and memo combinations remain partial.

Requirements: Explicit before/after values; broader memo/type/subclass combinations remain in V2.

- [EventsAndConstraints.spec.md]: [Port event type inference], [Transit event endpoints],
  [Entity transition invariants], [Entity porting invariants].

Evidence: [cat relocation acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java)
and [memo-based bi-events](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java).
Explicit create/delete/relocation and invalid-null endpoint recovery are verified by
[relationship evolution acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).
Remaining work: memo/type/subclass breadth belongs to V2.

## Useful violations and errors

Epic: [Failure Recovery and Resource Hygiene](Backlog.md#failure-recovery-and-resource-hygiene);
[Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).
Design readiness: ready for selected V1 validation/report behavior. Verification: verified for the required V1 paths;
broader report metadata remains partial.

Requirements: Selected violation paths and the V1 validation-report requirements.

- [EventsAndConstraints.spec.md]: [Constraint violation exceptions], [Constraint descriptions], [Violation details].
- [TransactionsAndEvaluation.spec.md]: V1 validation-report requirements.

Evidence: [cat workflow](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java),
[operation/rule diagnostics](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java),
and enabled report cases in [EvaluationLogicTest.java](../../../src/test/java/com/taitl/ex/logic/evaluation/EvaluationLogicTest.java).
The three report cases pass in the full V1 build.

## Recovery and resource release

Epic: [Failure Recovery and Resource Hygiene](Backlog.md#failure-recovery-and-resource-hygiene);
[Sprint 03](sprints/Sprint03_V1FailureRecoveryAndResourceHygiene.md).
Design readiness: ready for the selected recovery/shutdown contract. Verification: verified for the selected V1 paths;
global leak reporting remains in V2.

Requirements: Admitted transaction/library exits; cleanup does not imply undoing caller objects.

- [TransactionsAndEvaluation.spec.md]: [Starting a transaction], [Committing a transaction],
  [Rolling back a transaction], [Checkpoint failure recovery], [Rollback callback failure recovery], [Library shutdown],
  [Closing a transaction from its callback].
- [Housekeeping.spec.md]: [Transaction resource release], [Reported event resource release],
  [Transaction-local state release], [Graceful library shutdown], [Transaction recycling], [Rule recycling].

Evidence: [transaction recovery tests](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java)
and [registry test](../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java), plus
[failed begin](../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterBeginFailure.java),
[checkpoint retry](../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterCheckpointFailure.java), and
[shutdown concurrency](../../../src/test/java/com/taitl/specs/library_usage/UserCanShutdownLibrary.java).
The [Sprint 02 relationship cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java)
also verify all three runtime index collections clear through retained closed handles after commit/explicit rollback.
Verification: the selected recovery and shutdown behaviors pass direct acceptance tests. Configuration finalization is
retryable when a context factory fails; see [configuration recovery acceptance](../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java).
The final root build runs all 455 tests with zero failures/errors/skips and its JaCoCo checks pass. Rejected-commit,
rollback, and fresh-recovery timings appear in the [Sprint 04 measurement](../performance/runs/20261022T183337Z.NightCityConsumerWorkflows.md)
and [V1 closeout measurement](../performance/runs/20261022T205600Z.NightCityV1Closeout.md). Broader cache bounds and
observable leak reporting remain in V2/global scope.

## Night City as an artifact consumer

Epic: [Night City Artifact Consumption and Performance](Backlog.md#night-city-artifact-consumption-and-performance);
[Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).
Design readiness: ready for local independent consumption. Verification: verified for the selected consumer and reactor
workflow.

Requirements: Local Maven dependency consumption.

- [Distribution.spec.md]: [Maven dependency consumption].

Evidence: [independent consumer acceptance](../../../examples/night_city/src/test/java/com/taitl/ex/examples/night_city/NightCityStoryTest.java),
[runnable story](../../../examples/night_city/src/main/java/com/taitl/ex/examples/night_city/app/NightCityStory.java),
and [consumer performance record](../performance/runs/20261022T205600Z.NightCityV1Closeout.md). Root install,
standalone consumer verification, reactor installation, artifact inspection, and dependency-tree checks passed for the
local 0.0.1 coordinate.

## Representative performance and hardening

Epic: [Night City Artifact Consumption and Performance](Backlog.md#night-city-artifact-consumption-and-performance);
[Sprint 04](sprints/Sprint04_V1NightCityArtifactAndPerformance.md).
Design readiness: ready for selected V1 workflows. Verification: complete for V1; broader capabilities remain in later
releases.

Requirements: All V1 capabilities and their five quality design obligations; final gate V1 Hardening and Closeout.
Topics: [LibraryAndConfiguration.spec.md], [TransactionsAndEvaluation.spec.md], [QuantifiersAndIndexes.spec.md],
[Housekeeping.spec.md], [Distribution.spec.md].
Evidence: [consumer workflow benchmark](../../../examples/night_city/src/main/java/com/taitl/ex/examples/night_city/app/NightCityWorkflowBenchmark.java),
[Sprint 04 three-sample record](../performance/runs/20261022T183337Z.NightCityConsumerWorkflows.md), the [V1 closeout
record](../performance/runs/20261022T205600Z.NightCityV1Closeout.md), and earlier [relationship cardinality
record](../performance/runs/20261022T013657Z.RelationshipsAndEvolution.md). The two full-workflow sessions have
different source/build identities and are not a direct comparison. There is no numerical gate.

## Independent library instances

Epic: [Instance Isolation and Configuration](Backlog.md#instance-isolation-and-configuration);
[Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).
Design readiness: ready for the Sprint 06 isolation and thread-handoff contract. Verification: acceptance verified.

Requirements: Independent-instance and broader operation/context combinations.

- [LibraryAndConfiguration.spec.md]: [Independent library instances], [Multiple business operations],
  [Multiple operation contexts], [Multiple context rules].

Evidence: [UserCanAccessLibrary.java](../../../src/test/java/com/taitl/specs/library/UserCanAccessLibrary.java)
verifies conflicting rules and flags, concurrent first transactions after setup handoff, and independent simultaneous
finalization. `Ex` and `Creator` remain process-wide by design.

## Configuration options and guarantees

Epic: [Instance Isolation and Configuration](Backlog.md#instance-isolation-and-configuration);
[Sprint 06](sprints/Sprint06_V2InstanceIsolationAndConfiguration.md).
Design readiness: ready for prospective flag changes, first-successful-begin freezing, source precedence, and retryable
finalization. Verification: acceptance verified.

Requirements: Configuration sources/options, plus configuration/injection concurrency and freeze decisions.

- [LibraryAndConfiguration.spec.md]: [Programmatic library options], [Behavioral flags], [Configuration before use],
  [Configuration build failure recovery], [Configuration files], [Classpath configuration],
  [Configuration environment variable], [Initial configuration file].

Evidence: [UserCanConfigureLibrary.java](../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java)
verifies prospective description enforcement, retained-builder freezing, and retry after a context-factory failure.
[ConfigureLibraryTest.java](../../../src/test/java/com/taitl/ex/logic/library/ConfigureLibraryTest.java) verifies invalid
source values do not partially apply. The Sprint 06 performance record separates construction/first begin from
configured transaction cost. New per-instance source APIs and concurrent rule declaration remain deferred.

## Contexts, factories, and ordering

Epic: [Contexts and Type Matching](Backlog.md#contexts-and-type-matching);
[Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).
Design readiness: open. Verification: partial.

Requirements: Broader parent/wildcard/factory and ordering combinations.

- [LibraryAndConfiguration.spec.md]: [Custom context rules], [Parent context rules], [Parent context ordering],
  [Default context factory], [Operation context factory], [Wildcard operation keys], [Wildcard operation matching],
  [Wildcard context matching], [Matching context rules], [Matching context ordering],
  [Operation configuration builders], [Context configuration classes].

Evidence to review: [configuring_contexts].
Remaining work: Resolve overlapping wildcard/parent/exact precedence and factory/builder edge cases.

## Generic/subclass and constraint breadth

Epic: [Contexts and Type Matching](Backlog.md#contexts-and-type-matching);
[Sprint 07](sprints/Sprint07_V2ContextsAndTypeMatching.md).
Design readiness: open. Verification: partial.

Requirements: Remaining class/field/collection/generic/subclass combinations beyond V1.

- [LibraryAndConfiguration.spec.md]: [Generic type invariants].
- [EventsAndConstraints.spec.md]: [Internal field constraints], [External field constraints],
  [Third-party field constraints], [Two-field constraints], [Multiple-field constraints], [Collection size constraints],
  [Collection contents constraints], [Inherited class constraints].

Evidence to review: [TypeKeyTest.java](../../../src/test/java/com/taitl/existential/keys/TypeKeyTest.java).
Remaining work: Specify hierarchy/generic matching and third-party/collection/field combinations beyond the V1 example.

## Custom transactions and access patterns

Epic: [Stages, Intents, and Transaction Customization](Backlog.md#stages-intents-and-transaction-customization);
[Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).
Design readiness: open. Verification: partial.

Requirements: Factories, supplied instances, access rules, memo behavior, and per-call values.

- [LibraryAndConfiguration.spec.md]: [Transaction lifecycle invariants], [Invocation configuration classes],
  [Transaction interaction classes], [Event reporting classes], [Transaction memo registration].
- [TransactionsAndEvaluation.spec.md]: [Access rule configuration], [Transaction lifecycle rule configuration],
  [Reporting entity access], [Reporting through transaction objects], [Rules on transaction objects],
  [Custom transaction factories], [Transaction factory inheritance], [Caller-supplied transactions].
- [EventsAndConstraints.spec.md]: [Additional event values].

Evidence to review: [custom_transactions].
Remaining work: Verify factories, supplied instances, access rules, memo behavior, and per-call values under the full
contract.

## Built-in stages, intents, and effects

Epic: [Stages, Intents, and Transaction Customization](Backlog.md#stages-intents-and-transaction-customization);
[Sprint 08](sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md).
Design readiness: open. Verification: partial.

Requirements: Broader stage timing and all IntentsAndEffects.spec.md requirements for built-in paths.

- [TransactionsAndEvaluation.spec.md]: [Execution-time effects], [Validation-time effects],
  [Assigning execution stages], [Built-in execution stages], [Participating evaluation rules], [Parent rule precedence],
  [Rule execution order], [Event handler order], [Separate stage evaluations], [Preconditions evaluation naming],
  [Immediate evaluation naming], [Assigning precondition rules], [Preconditions evaluation lifetime],
  [Precondition event frequency], [Immediate evaluation lifetime], [Immediate event frequency],
  [Commit and checkpoint validation], [Validation effect dispatch], [Validation event deduplication].
- [IntentsAndEffects.spec.md]: all built-in intent/effect paths.

Evidence to review: [UserCanConfigureIntents.java].
Remaining work: Reconcile stage terminology and cover order, partial effects, authorization failures, and recovery.

## Maintained relationships and indexing

Epic: [Indexes and Operational Coverage](Backlog.md#indexes-and-operational-coverage);
[Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: Maintained indexes and remaining All/Exists combinations; retain internal indexing needed by V1.

- [QuantifiersAndIndexes.spec.md]: [All on entity transitions], [Exists on maps], [Indexed existence lookup],
  [Indexes between rules], [Configuration finalization], [Configuration rule indexing], [Runtime event indexing].

Evidence to review: [indexes](../../../src/test/java/com/taitl/exlogic/unused/indexes/).
Remaining work: Reconcile public index names and verify maintenance plus lookup/scaling and transaction-level results.

## Broader lifecycle and event combinations

Epic: [Indexes and Operational Coverage](Backlog.md#indexes-and-operational-coverage);
[Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: Remaining lifecycle/event combinations beyond the V1 slice.

- [TransactionsAndEvaluation.spec.md]: [Transaction checkpoints], [Validation phase triggers],
  [Independent transaction evaluations], [Commit and checkpoint validation].
- [EventsAndConstraints.spec.md]: [Reporting class events], [Equal event deduplication],
  [Events require library configuration], [Events require a started transaction], [Events after transaction completion],
  [Configuration after the first event], [Port event type inference], [Transit event endpoints],
  [Entity transition invariants], [Entity porting invariants], [Additional event values].

Evidence to review: [UserCanInitiateTransactions.java].
Remaining work: Specify checkpoint reset/continuation, additional values, and broad event
combinations.

## Richer reports, caching, and global hygiene

Epic: [Indexes and Operational Coverage](Backlog.md#indexes-and-operational-coverage);
[Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: partial.

Requirements: Broader reporting, cache, global cleanup, and leak-reporting guarantees.

- [EventsAndConstraints.spec.md]: [Constraint error numbers], [Required constraint descriptions].
- [Housekeeping.spec.md]: [Caching between transactions], [Caching between contexts], [Transaction resource release],
  [Reported event resource release], [Transaction recycling], [Rule recycling], [Memory leak reporting].

Evidence to review: [TypeKeyCacheTest.java](../../../src/test/java/com/taitl/ex/logic/events/TypeKeyCacheTest.java).
Remaining work: Verify metadata, description options, invalidation, boundedness, global close, and admitted leak
reporting.

## Publication readiness

Epic: [Indexes and Operational Coverage](Backlog.md#indexes-and-operational-coverage);
[Sprint 09](sprints/Sprint09_V2IndexesAndOperationalCoverage.md).
Design readiness: open. Verification: missing.

Requirements: Remote distribution prerequisites; remote availability requires its own evidence.

- [Distribution.spec.md]: [Maven dependency consumption].

Evidence to review: not yet available.
Remaining work: Record artifacts/destination/account/signing prerequisites; do not block an unclaimed local-only channel
on publication.

## Custom event contracts and registration

Epic: [Custom Event Contracts and Registration](Backlog.md#custom-event-contracts-and-registration);
[Sprint 11](sprints/Sprint11_V3CustomEventContractsAndRegistration.md).
Design readiness: open. Verification: missing.

Requirements: Custom event declaration, registration, matching, and invalid-registration guarantees.

- [EventsAndConstraints.spec.md]: [Defining custom event types], [Custom event handlers],
  [Custom event context constraints], [Custom event transaction constraints], [Reporting custom events].

Evidence to review: not yet available.
Remaining work: Define registration identity, collisions, ownership, invalid inputs, and relevant concurrency.

## Custom event evaluation and extensions

Epic: [Custom Event Evaluation and Extensions](Backlog.md#custom-event-evaluation-and-extensions);
[Sprint 12](sprints/Sprint12_V3CustomEventEvaluationAndExtensions.md).
Design readiness: open. Verification: missing.

Requirements: Custom invocation and paths through intents, effects, transaction evaluation, and housekeeping.

- [EventsAndConstraints.spec.md]: [Custom event splitting], [Reporting custom events].
- [IntentsAndEffects.spec.md], [TransactionsAndEvaluation.spec.md], and [Housekeeping.spec.md]: custom-event integration
  and cleanup.

Evidence to review: not yet available.
Remaining work: Specify splitting, matching, authorization, callbacks, failure recovery, expansion costs, and cleanup.

## Release hardening gates

[V1 Hardening and Closeout](Backlog.md#v1-hardening-and-closeout),
[V2 Hardening and Closeout](Backlog.md#v2-hardening-and-closeout), and
[V3 Hardening and Closeout](Backlog.md#v3-hardening-and-closeout) verify their complete admitted
capabilities and retained regressions. Actual coverage, required tests, five design obligations, performance/resource
records, consumer results, and artifact checks must agree before closure. These gates do not add feature scope.

[LibraryAndConfiguration.spec.md]: ../specifications/LibraryAndConfiguration.spec.md
[TransactionsAndEvaluation.spec.md]: ../specifications/TransactionsAndEvaluation.spec.md
[EventsAndConstraints.spec.md]: ../specifications/EventsAndConstraints.spec.md
[QuantifiersAndIndexes.spec.md]: ../specifications/QuantifiersAndIndexes.spec.md
[IntentsAndEffects.spec.md]: ../specifications/IntentsAndEffects.spec.md
[Housekeeping.spec.md]: ../specifications/Housekeeping.spec.md
[Distribution.spec.md]: ../specifications/Distribution.spec.md

[UserCanConfigureEntities.java]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java
[UserCanSendEntityEvents.java]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java
[UserCanUseMemoForBiEvents.java]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java
[EvaluationLogicTest.java]: ../../../src/test/java/com/taitl/ex/logic/evaluation/EvaluationLogicTest.java
[TrRegistryTest.java]: ../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java
[CustomTransactionBenchmark.java]:
  ../../../src/test/java/com/taitl/existential/benchmarks/CustomTransactionBenchmark.java
[UserCanAccessLibrary.java]: ../../../src/test/java/com/taitl/specs/library/UserCanAccessLibrary.java
[UserCanConfigureLibrary.java]:
  ../../../src/test/java/com/taitl/specs/library_configuration/UserCanConfigureLibrary.java
[configuring_contexts]: ../../../src/test/java/com/taitl/specs/configuration_workflow/configuring_contexts/
[custom_transactions]: ../../../src/test/java/com/taitl/specs/configuration_workflow/custom_transactions/
[UserCanConfigureIntents.java]: ../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureIntents.java
[UserCanInitiateTransactions.java]:
  ../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java

[Static facade access]: ../specifications/LibraryAndConfiguration.spec.md#static-facade-access
[Singleton access]: ../specifications/LibraryAndConfiguration.spec.md#singleton-access
[Multiple business operations]: ../specifications/LibraryAndConfiguration.spec.md#multiple-business-operations
[Multiple context rules]: ../specifications/LibraryAndConfiguration.spec.md#multiple-context-rules
[Operation keys]: ../specifications/LibraryAndConfiguration.spec.md#operation-keys
[Nonempty contexts]: ../specifications/LibraryAndConfiguration.spec.md#nonempty-contexts
[Class event invariants]: ../specifications/LibraryAndConfiguration.spec.md#class-event-invariants
[Library facade classes]: ../specifications/LibraryAndConfiguration.spec.md#library-facade-classes
[Operation configuration builders]: ../specifications/LibraryAndConfiguration.spec.md#operation-configuration-builders
[Context configuration classes]: ../specifications/LibraryAndConfiguration.spec.md#context-configuration-classes
[Class rule configuration]: ../specifications/TransactionsAndEvaluation.spec.md#class-rule-configuration
[Internal field constraints]: ../specifications/EventsAndConstraints.spec.md#internal-field-constraints
[External field constraints]: ../specifications/EventsAndConstraints.spec.md#external-field-constraints
[Third-party field constraints]: ../specifications/EventsAndConstraints.spec.md#third-party-field-constraints
[Two-field constraints]: ../specifications/EventsAndConstraints.spec.md#two-field-constraints
[Multiple-field constraints]: ../specifications/EventsAndConstraints.spec.md#multiple-field-constraints
[Starting a transaction]: ../specifications/TransactionsAndEvaluation.spec.md#starting-a-transaction
[Committing a transaction]: ../specifications/TransactionsAndEvaluation.spec.md#committing-a-transaction
[Rolling back a transaction]: ../specifications/TransactionsAndEvaluation.spec.md#rolling-back-a-transaction
[Reporting entity modifications]: ../specifications/TransactionsAndEvaluation.spec.md#reporting-entity-modifications
[Reporting through transaction objects]:
  ../specifications/TransactionsAndEvaluation.spec.md#reporting-through-transaction-objects
[Events require a transaction]: ../specifications/TransactionsAndEvaluation.spec.md#events-require-a-transaction
[Events require configured rules]: ../specifications/TransactionsAndEvaluation.spec.md#events-require-configured-rules
[Library usage phases]: ../specifications/TransactionsAndEvaluation.spec.md#library-usage-phases
[Configuration phase]: ../specifications/TransactionsAndEvaluation.spec.md#configuration-phase
[Execution phase]: ../specifications/TransactionsAndEvaluation.spec.md#execution-phase
[Validation phase triggers]: ../specifications/TransactionsAndEvaluation.spec.md#validation-phase-triggers
[Constraint validation and reporting]:
  ../specifications/TransactionsAndEvaluation.spec.md#constraint-validation-and-reporting
[Default validation stage]: ../specifications/TransactionsAndEvaluation.spec.md#default-validation-stage
[Rule execution order]: ../specifications/TransactionsAndEvaluation.spec.md#rule-execution-order
[Event handler order]: ../specifications/TransactionsAndEvaluation.spec.md#event-handler-order
[Validation evaluation naming]: ../specifications/TransactionsAndEvaluation.spec.md#validation-evaluation-naming
[Transaction event scope]: ../specifications/TransactionsAndEvaluation.spec.md#transaction-event-scope
[Commit and checkpoint validation]: ../specifications/TransactionsAndEvaluation.spec.md#commit-and-checkpoint-validation
[Validation event deduplication]: ../specifications/TransactionsAndEvaluation.spec.md#validation-event-deduplication
[Reporting class events]: ../specifications/EventsAndConstraints.spec.md#reporting-class-events
[Equal event deduplication]: ../specifications/EventsAndConstraints.spec.md#equal-event-deduplication
[Events require library configuration]:
  ../specifications/EventsAndConstraints.spec.md#events-require-library-configuration
[Events require a started transaction]:
  ../specifications/EventsAndConstraints.spec.md#events-require-a-started-transaction
[Events after transaction completion]:
  ../specifications/EventsAndConstraints.spec.md#events-after-transaction-completion
[Configuration after the first event]:
  ../specifications/EventsAndConstraints.spec.md#configuration-after-the-first-event
[Configuration finalization]: ../specifications/QuantifiersAndIndexes.spec.md#configuration-finalization
[Configuration rule indexing]: ../specifications/QuantifiersAndIndexes.spec.md#configuration-rule-indexing
[Runtime event indexing]: ../specifications/QuantifiersAndIndexes.spec.md#runtime-event-indexing
[All entity invariants]: ../specifications/QuantifiersAndIndexes.spec.md#all-entity-invariants
[Conditional All invariants]: ../specifications/QuantifiersAndIndexes.spec.md#conditional-all-invariants
[All on entity classes]: ../specifications/QuantifiersAndIndexes.spec.md#all-on-entity-classes
[Exists entity invariants]: ../specifications/QuantifiersAndIndexes.spec.md#exists-entity-invariants
[Conditional same-class existence]: ../specifications/QuantifiersAndIndexes.spec.md#conditional-same-class-existence
[Conditional cross-class existence]: ../specifications/QuantifiersAndIndexes.spec.md#conditional-cross-class-existence
[All-Exists composition]: ../specifications/QuantifiersAndIndexes.spec.md#all-exists-composition
[Exists on collections]: ../specifications/QuantifiersAndIndexes.spec.md#exists-on-collections
[Same-class entity relationships]: ../specifications/EventsAndConstraints.spec.md#same-class-entity-relationships
[Cross-class entity relationships]: ../specifications/EventsAndConstraints.spec.md#cross-class-entity-relationships
[Same-class existence constraints]: ../specifications/EventsAndConstraints.spec.md#same-class-existence-constraints
[Cross-class existence constraints]: ../specifications/EventsAndConstraints.spec.md#cross-class-existence-constraints
[Port event type inference]: ../specifications/EventsAndConstraints.spec.md#port-event-type-inference
[Transit event endpoints]: ../specifications/EventsAndConstraints.spec.md#transit-event-endpoints
[Entity transition invariants]: ../specifications/EventsAndConstraints.spec.md#entity-transition-invariants
[Entity porting invariants]: ../specifications/EventsAndConstraints.spec.md#entity-porting-invariants
[Constraint violation exceptions]: ../specifications/EventsAndConstraints.spec.md#constraint-violation-exceptions
[Constraint descriptions]: ../specifications/EventsAndConstraints.spec.md#constraint-descriptions
[Violation details]: ../specifications/EventsAndConstraints.spec.md#violation-details
[Transaction resource release]: ../specifications/Housekeeping.spec.md#transaction-resource-release
[Reported event resource release]: ../specifications/Housekeeping.spec.md#reported-event-resource-release
[Transaction recycling]: ../specifications/Housekeeping.spec.md#transaction-recycling
[Rule recycling]: ../specifications/Housekeeping.spec.md#rule-recycling
[Maven dependency consumption]: ../specifications/Distribution.spec.md#maven-dependency-consumption
[Independent library instances]: ../specifications/LibraryAndConfiguration.spec.md#independent-library-instances
[Multiple operation contexts]: ../specifications/LibraryAndConfiguration.spec.md#multiple-operation-contexts
[Programmatic library options]: ../specifications/LibraryAndConfiguration.spec.md#programmatic-library-options
[Behavioral flags]: ../specifications/LibraryAndConfiguration.spec.md#behavioral-flags
[Configuration before use]: ../specifications/LibraryAndConfiguration.spec.md#configuration-before-use
[Configuration files]: ../specifications/LibraryAndConfiguration.spec.md#configuration-files
[Classpath configuration]: ../specifications/LibraryAndConfiguration.spec.md#classpath-configuration
[Configuration environment variable]:
  ../specifications/LibraryAndConfiguration.spec.md#configuration-environment-variable
[Initial configuration file]: ../specifications/LibraryAndConfiguration.spec.md#initial-configuration-file
[Custom context rules]: ../specifications/LibraryAndConfiguration.spec.md#custom-context-rules
[Parent context rules]: ../specifications/LibraryAndConfiguration.spec.md#parent-context-rules
[Parent context ordering]: ../specifications/LibraryAndConfiguration.spec.md#parent-context-ordering
[Default context factory]: ../specifications/LibraryAndConfiguration.spec.md#default-context-factory
[Operation context factory]: ../specifications/LibraryAndConfiguration.spec.md#operation-context-factory
[Wildcard operation keys]: ../specifications/LibraryAndConfiguration.spec.md#wildcard-operation-keys
[Wildcard operation matching]: ../specifications/LibraryAndConfiguration.spec.md#wildcard-operation-matching
[Wildcard context matching]: ../specifications/LibraryAndConfiguration.spec.md#wildcard-context-matching
[Matching context rules]: ../specifications/LibraryAndConfiguration.spec.md#matching-context-rules
[Matching context ordering]: ../specifications/LibraryAndConfiguration.spec.md#matching-context-ordering
[Generic type invariants]: ../specifications/LibraryAndConfiguration.spec.md#generic-type-invariants
[Collection size constraints]: ../specifications/EventsAndConstraints.spec.md#collection-size-constraints
[Collection contents constraints]: ../specifications/EventsAndConstraints.spec.md#collection-contents-constraints
[Inherited class constraints]: ../specifications/EventsAndConstraints.spec.md#inherited-class-constraints
[Transaction lifecycle invariants]: ../specifications/LibraryAndConfiguration.spec.md#transaction-lifecycle-invariants
[Invocation configuration classes]: ../specifications/LibraryAndConfiguration.spec.md#invocation-configuration-classes
[Transaction interaction classes]: ../specifications/LibraryAndConfiguration.spec.md#transaction-interaction-classes
[Event reporting classes]: ../specifications/LibraryAndConfiguration.spec.md#event-reporting-classes
[Transaction memo registration]: ../specifications/LibraryAndConfiguration.spec.md#transaction-memo-registration
[Access rule configuration]: ../specifications/TransactionsAndEvaluation.spec.md#access-rule-configuration
[Transaction lifecycle rule configuration]:
  ../specifications/TransactionsAndEvaluation.spec.md#transaction-lifecycle-rule-configuration
[Reporting entity access]: ../specifications/TransactionsAndEvaluation.spec.md#reporting-entity-access
[Rules on transaction objects]: ../specifications/TransactionsAndEvaluation.spec.md#rules-on-transaction-objects
[Custom transaction factories]: ../specifications/TransactionsAndEvaluation.spec.md#custom-transaction-factories
[Transaction factory inheritance]: ../specifications/TransactionsAndEvaluation.spec.md#transaction-factory-inheritance
[Caller-supplied transactions]: ../specifications/TransactionsAndEvaluation.spec.md#caller-supplied-transactions
[Additional event values]: ../specifications/EventsAndConstraints.spec.md#additional-event-values
[Execution-time effects]: ../specifications/TransactionsAndEvaluation.spec.md#execution-time-effects
[Validation-time effects]: ../specifications/TransactionsAndEvaluation.spec.md#validation-time-effects
[Assigning execution stages]: ../specifications/TransactionsAndEvaluation.spec.md#assigning-execution-stages
[Built-in execution stages]: ../specifications/TransactionsAndEvaluation.spec.md#built-in-execution-stages
[Participating evaluation rules]: ../specifications/TransactionsAndEvaluation.spec.md#participating-evaluation-rules
[Parent rule precedence]: ../specifications/TransactionsAndEvaluation.spec.md#parent-rule-precedence
[Separate stage evaluations]: ../specifications/TransactionsAndEvaluation.spec.md#separate-stage-evaluations
[Preconditions evaluation naming]: ../specifications/TransactionsAndEvaluation.spec.md#preconditions-evaluation-naming
[Immediate evaluation naming]: ../specifications/TransactionsAndEvaluation.spec.md#immediate-evaluation-naming
[Assigning precondition rules]: ../specifications/TransactionsAndEvaluation.spec.md#assigning-precondition-rules
[Preconditions evaluation lifetime]:
  ../specifications/TransactionsAndEvaluation.spec.md#preconditions-evaluation-lifetime
[Precondition event frequency]: ../specifications/TransactionsAndEvaluation.spec.md#precondition-event-frequency
[Immediate evaluation lifetime]: ../specifications/TransactionsAndEvaluation.spec.md#immediate-evaluation-lifetime
[Immediate event frequency]: ../specifications/TransactionsAndEvaluation.spec.md#immediate-event-frequency
[Validation effect dispatch]: ../specifications/TransactionsAndEvaluation.spec.md#validation-effect-dispatch
[All on entity transitions]: ../specifications/QuantifiersAndIndexes.spec.md#all-on-entity-transitions
[Exists on maps]: ../specifications/QuantifiersAndIndexes.spec.md#exists-on-maps
[Indexed existence lookup]: ../specifications/QuantifiersAndIndexes.spec.md#indexed-existence-lookup
[Indexes between rules]: ../specifications/QuantifiersAndIndexes.spec.md#indexes-between-rules
[Transaction checkpoints]: ../specifications/TransactionsAndEvaluation.spec.md#transaction-checkpoints
[Independent transaction evaluations]:
  ../specifications/TransactionsAndEvaluation.spec.md#independent-transaction-evaluations
[Constraint error numbers]: ../specifications/EventsAndConstraints.spec.md#constraint-error-numbers
[Required constraint descriptions]: ../specifications/EventsAndConstraints.spec.md#required-constraint-descriptions
[Caching between transactions]: ../specifications/Housekeeping.spec.md#caching-between-transactions
[Caching between contexts]: ../specifications/Housekeeping.spec.md#caching-between-contexts
[Memory leak reporting]: ../specifications/Housekeeping.spec.md#memory-leak-reporting
[Defining custom event types]: ../specifications/EventsAndConstraints.spec.md#defining-custom-event-types
[Custom event handlers]: ../specifications/EventsAndConstraints.spec.md#custom-event-handlers
[Custom event context constraints]: ../specifications/EventsAndConstraints.spec.md#custom-event-context-constraints
[Custom event transaction constraints]:
  ../specifications/EventsAndConstraints.spec.md#custom-event-transaction-constraints
[Reporting custom events]: ../specifications/EventsAndConstraints.spec.md#reporting-custom-events
[Custom event splitting]: ../specifications/EventsAndConstraints.spec.md#custom-event-splitting
