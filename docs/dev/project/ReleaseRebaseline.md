# Release Rebaseline: Representative Core, Breadth, Extensibility

Decision date: 2026-10-20. The user accepted a representative end-to-end V1 and a V1/V2/V3 split, prioritizing
robustness and performance. The original subsystem-oriented baseline is [archived][archive]. It was superseded before
implementation; its planned sprints are not completed work. Current epic labels refer to this new baseline.

## Outcomes and priorities

V1 delivers the documented Night City workflow, including success, useful rejection, recovery/resource release,
a basic All/Exists relationship, and an evolution rule. V2 broadens standard usage and combinations; V3 completes
custom-event extensibility. All releases require correctness, actionable errors, failure recovery, resource hygiene,
and performance in their designs and evidence. Extra hardening cycles are not a concern.

Night City itself will be a separate Maven project consuming the installed library artifact. The root library tests
can retain shared fixture sources; the independent consumer build must use only the artifact/public API and its own
example sources. Build the library first to avoid a circular fixture/consumer dependency. A root reactor restructure
is not required. Local consumption does not require Maven Central; remote availability remains separately evidenced.

Existing implementation and regression tests remain. No requirement IDs/completion marks are changed, no pending
suggestion is newly approved, and no release feature is marked complete. The first selected work is the Night City
workflow design/specification/evidence slice, not independent-instance isolation.

## Disposition of every original scope item

Rows preserve the order of the original roadmap. Capability links identify the representative V1 subset and later
breadth/extension work. A split row is intentional: release scope is capability-level, not whole-topic completion.

### Original V1

- V1-01: Library.
  Current capabilities: [V1-C01], [V2-C01].
- V1-02: Library configuration.
  Current capabilities: [V1-C01], [V2-C02].
- V1-03: Library usage.
  Current capabilities: [V1-C02], [V2-C05].
- V1-04: Lifecycle phases.
  Current capabilities: [V1-C02], [V2-C06].
- V1-05: Configure rules.
  Current capabilities: [V1-C01], [V2-C03].
- V1-06: Workflows: Configuration workflow, Configuring transactions, Configuring Constraints.
  Current capabilities: [V1-C01], [V2-C03], [V2-C05].
- V1-07: Wildcard contexts.
  Current capabilities: [V2-C03].
- V1-08: Configure transactions.
  Current capabilities: [V1-C02], [V2-C05].
- V1-09: Configure constraints.
  Current capabilities: [V1-C01], [V2-C04].
- V1-10: Configure effects.
  Current capabilities: [V2-C06].
- V1-11: Configure intents.
  Current capabilities: [V2-C06].
- V1-12: Execution stages.
  Current capabilities: [V1-C02], [V2-C06].
- V1-13: Evaluation workflow.
  Current capabilities: [V1-C02], [V2-C06].
- V1-14: Preconditions evaluation, Immediate evaluation, Validation evaluation.
  Current capabilities: [V1-C02], [V2-C06].
- V1-15: Intents.
  Current capabilities: [V2-C06].
- V1-16: Indexes.
  Current capabilities: [V1-C03], [V2-C07].
- V1-17: Internal indexing.
  Current capabilities: [V1-C02], [V2-C07].
- V1-18: Transaction lifecycle.
  Current capabilities: [V1-C06], [V2-C08].
- V1-19: Public classes.
  Current capabilities: [V1-C01], [V2-C01], [V2-C05].

### Original V2

- V2-01: Events.
  Current capabilities: [V1-C02], [V1-C04], [V2-C08].
- V2-02: Constraint on a single entity.
  Current capabilities: [V1-C01], [V2-C04].
- V2-03: Constraints on multiple entities.
  Current capabilities: [V1-C03], [V2-C07].
- V2-04: Constraints on entity evolution.
  Current capabilities: [V1-C04], [V2-C08].
- V2-05: Constraints on evolution of multiple entities.
  Current capabilities: [V2-C08].
- V2-06: Constraints on subclasses.
  Current capabilities: [V2-C04].
- V2-07: Constraint violation reporting.
  Current capabilities: [V1-C05], [V2-C09].
- V2-08: Universal Quantifier.
  Current capabilities: [V1-C03], [V2-C07].
- V2-09: Existence Quantifier.
  Current capabilities: [V1-C03], [V2-C07].
- V2-10: Effects.
  Current capabilities: [V2-C06].
- V2-11: Housekeeping.
  Current capabilities: [V1-C06], [V2-C09].
- V2-12: Transaction Cleanup.
  Current capabilities: [V1-C06], [V2-C09].
- V2-13: Global Cleanup.
  Current capabilities: [V1-C06], [V2-C09].
- V2-14: Constraint enforcement.
  Current capabilities: [V1-C02], [V1-C03], [V1-C04].
- V2-15: Side effect execution.
  Current capabilities: [V2-C06].
- V2-16: Custom Event Types.
  Current capabilities: [V3-C01], [V3-C02].
- V2-17: Maven dependency.
  Current capabilities: [V1-C07], [V2-C10].

## Epic-level changes

The old V1 library/configuration/context work moves primarily to V2-E01/E02. The admitted V1 transaction/evaluation
subset moves into the workflow and recovery epics; broader stage/custom-transaction work moves to V2-E03/E04.
The old V2 constraints/quantifiers/reporting/cleanup work splits between representative V1 and broader V2. Independent
consumption moves to V1-E04 and uses Night City; custom events move to V3-E01/E02. Each release retains hardening.
The [backlog](Backlog.md) preserves original intake approval states under current owners.

The baseline remains the [same library purpose](Polaris.md). Sprints are undated; no capacity/deadline is invented.
Current outcomes and gates: [V1](V1.md), [V2](V2.md), [V3](V3.md), and [traceability](TraceabilityMatrix.md).

[archive]: ../past/planning/release-baseline-2026-10-20/Roadmap.md
[V1-C01]: TraceabilityMatrix.md#v1-c01-ordinary-configuration-and-class-rules
[V1-C02]: TraceabilityMatrix.md#v1-c02-reported-events-and-commit-validation
[V1-C03]: TraceabilityMatrix.md#v1-c03-basic-allexists-relationship
[V1-C04]: TraceabilityMatrix.md#v1-c04-explicit-entity-evolution
[V1-C05]: TraceabilityMatrix.md#v1-c05-useful-violations-and-errors
[V1-C06]: TraceabilityMatrix.md#v1-c06-recovery-and-resource-release
[V1-C07]: TraceabilityMatrix.md#v1-c07-night-city-as-an-artifact-consumer
[V1-C08]: TraceabilityMatrix.md#v1-c08-representative-performance-and-hardening
[V2-C01]: TraceabilityMatrix.md#v2-c01-independent-library-instances
[V2-C02]: TraceabilityMatrix.md#v2-c02-configuration-options-and-guarantees
[V2-C03]: TraceabilityMatrix.md#v2-c03-contexts-factories-and-ordering
[V2-C04]: TraceabilityMatrix.md#v2-c04-genericsubclass-and-constraint-breadth
[V2-C05]: TraceabilityMatrix.md#v2-c05-custom-transactions-and-access-patterns
[V2-C06]: TraceabilityMatrix.md#v2-c06-built-in-stages-intents-and-effects
[V2-C07]: TraceabilityMatrix.md#v2-c07-maintained-relationships-and-indexing
[V2-C08]: TraceabilityMatrix.md#v2-c08-broader-lifecycle-and-event-combinations
[V2-C09]: TraceabilityMatrix.md#v2-c09-richer-reports-caching-and-global-hygiene
[V2-C10]: TraceabilityMatrix.md#v2-c10-publication-readiness
[V3-C01]: TraceabilityMatrix.md#v3-c01-custom-event-contracts-and-registration
[V3-C02]: TraceabilityMatrix.md#v3-c02-custom-event-evaluation-and-extensions


Documentation task evidence: [completed rebaseline plan](../past/planning/P10202602.Three-release-workflow.plan.md).
No feature implementation or independent consumer result is asserted by this planning update.
