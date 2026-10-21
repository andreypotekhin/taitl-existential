# Release Rebaseline: Representative Core, Breadth, Extensibility

Decision date: 2026-10-20. The user accepted a representative end-to-end V1 and a V1/V2/V3 split, prioritizing
robustness and performance. The original subsystem-oriented baseline is [archived][archive]. It was superseded before
implementation; its planned sprints are not completed work. Current epic labels refer to this new baseline.

## Outcomes and priorities

V1 delivers the documented Night City workflow, including success, useful rejection, recovery/resource release, a basic
All/Exists relationship, and an evolution rule. V2 broadens standard usage and combinations; V3 completes custom-event
extensibility. All releases require correctness, actionable errors, failure recovery, resource hygiene, and performance
in their designs and evidence. Extra hardening cycles are not a concern.

Night City itself will be a separate Maven project consuming the installed library artifact. The root library tests can
retain shared fixture sources; the independent consumer build must use only the artifact/public API and its own example
sources. Build the library first to avoid a circular fixture/consumer dependency. A root reactor restructure is not
required. Local consumption does not require Maven Central; remote availability remains separately evidenced.

Existing implementation and regression tests remain. No requirement behavior/completion marks are changed, no pending
suggestion is newly approved, and no release feature is marked complete. The first selected work is the Night City
workflow design/specification/evidence slice, not independent-instance isolation.

## Disposition of every original scope item

Rows preserve the order of the original roadmap. Capability links identify the representative V1 subset and later
breadth/extension work. A split row is intentional: release scope is capability-level, not whole-topic completion.

### Original V1

- Library. Current capabilities: [Ordinary configuration and class rules], [Independent library instances].
- Library configuration. Current capabilities: [Ordinary configuration and class rules],
  [Configuration options and guarantees].
- Library usage. Current capabilities: [Reported events and commit validation],
  [Custom transactions and access patterns].
- Lifecycle phases. Current capabilities: [Reported events and commit validation],
  [Built-in stages, intents, and effects].
- Configure rules. Current capabilities: [Ordinary configuration and class rules], [Contexts, factories, and ordering].
- Workflows: Configuration workflow, Configuring transactions, Configuring Constraints. Current capabilities:
  [Ordinary configuration and class rules], [Contexts, factories, and ordering],
  [Custom transactions and access patterns].
- Wildcard contexts. Current capabilities: [Contexts, factories, and ordering].
- Configure transactions. Current capabilities: [Reported events and commit validation],
  [Custom transactions and access patterns].
- Configure constraints. Current capabilities: [Ordinary configuration and class rules],
  [Generic/subclass and constraint breadth].
- Configure effects. Current capabilities: [Built-in stages, intents, and effects].
- Configure intents. Current capabilities: [Built-in stages, intents, and effects].
- Execution stages. Current capabilities: [Reported events and commit validation],
  [Built-in stages, intents, and effects].
- Evaluation workflow. Current capabilities: [Reported events and commit validation],
  [Built-in stages, intents, and effects].
- Preconditions evaluation, Immediate evaluation, Validation evaluation. Current capabilities:
  [Reported events and commit validation], [Built-in stages, intents, and effects].
- Intents. Current capabilities: [Built-in stages, intents, and effects].
- Indexes. Current capabilities: [Basic All/Exists relationship], [Maintained relationships and indexing].
- Internal indexing. Current capabilities: [Reported events and commit validation],
  [Maintained relationships and indexing].
- Transaction lifecycle. Current capabilities: [Recovery and resource release],
  [Broader lifecycle and event combinations].
- Public classes. Current capabilities: [Ordinary configuration and class rules], [Independent library instances],
  [Custom transactions and access patterns].

### Original V2

- Events. Current capabilities: [Reported events and commit validation], [Explicit entity evolution],
  [Broader lifecycle and event combinations].
- Constraint on a single entity. Current capabilities: [Ordinary configuration and class rules],
  [Generic/subclass and constraint breadth].
- Constraints on multiple entities. Current capabilities: [Basic All/Exists relationship],
  [Maintained relationships and indexing].
- Constraints on entity evolution. Current capabilities: [Explicit entity evolution],
  [Broader lifecycle and event combinations].
- Constraints on evolution of multiple entities. Current capabilities: [Broader lifecycle and event combinations].
- Constraints on subclasses. Current capabilities: [Generic/subclass and constraint breadth].
- Constraint violation reporting. Current capabilities: [Useful violations and errors],
  [Richer reports, caching, and global hygiene].
- Universal Quantifier. Current capabilities: [Basic All/Exists relationship], [Maintained relationships and indexing].
- Existence Quantifier. Current capabilities: [Basic All/Exists relationship], [Maintained relationships and indexing].
- Effects. Current capabilities: [Built-in stages, intents, and effects].
- Housekeeping. Current capabilities: [Recovery and resource release], [Richer reports, caching, and global hygiene].
- Transaction Cleanup. Current capabilities: [Recovery and resource release],
  [Richer reports, caching, and global hygiene].
- Global Cleanup. Current capabilities: [Recovery and resource release], [Richer reports, caching, and global hygiene].
- Constraint enforcement. Current capabilities: [Reported events and commit validation],
  [Basic All/Exists relationship], [Explicit entity evolution].
- Side effect execution. Current capabilities: [Built-in stages, intents, and effects].
- Custom Event Types. Current capabilities: [Custom event contracts and registration],
  [Custom event evaluation and extensions].
- Maven dependency. Current capabilities: [Night City as an artifact consumer], [Publication readiness].

## Epic-level changes

The old V1 library/configuration/context work moves primarily to Instance Isolation and Configuration and Contexts and
Type Matching. The admitted V1 transaction/evaluation subset moves into the workflow and recovery epics; broader
stage/custom-transaction work moves to Stages, Intents, and Transaction Customization and Indexes and Operational
Coverage. The old V2 constraints/quantifiers/reporting/cleanup work splits between representative V1 and broader V2.
Independent consumption moves to Night City Artifact Consumption and Performance and uses Night City; custom events move
to Custom Event Contracts and Registration and Custom Event Evaluation and Extensions. Each release retains hardening.
The [backlog](Backlog.md) preserves original intake approval states under current owners.

The baseline remains the [same library purpose](Polaris.md). Sprints are undated; no capacity/deadline is invented.
Current outcomes and gates: [V1](V1.md), [V2](V2.md), [V3](V3.md), and [traceability](TraceabilityMatrix.md).

[archive]: ../past/planning/release-baseline-2026-10-20/Roadmap.md
[Ordinary configuration and class rules]: TraceabilityMatrix.md#ordinary-configuration-and-class-rules
[Reported events and commit validation]: TraceabilityMatrix.md#reported-events-and-commit-validation
[Basic All/Exists relationship]: TraceabilityMatrix.md#basic-allexists-relationship
[Explicit entity evolution]: TraceabilityMatrix.md#explicit-entity-evolution
[Useful violations and errors]: TraceabilityMatrix.md#useful-violations-and-errors
[Recovery and resource release]: TraceabilityMatrix.md#recovery-and-resource-release
[Night City as an artifact consumer]: TraceabilityMatrix.md#night-city-as-an-artifact-consumer
[Representative performance and hardening]: TraceabilityMatrix.md#representative-performance-and-hardening
[Independent library instances]: TraceabilityMatrix.md#independent-library-instances
[Configuration options and guarantees]: TraceabilityMatrix.md#configuration-options-and-guarantees
[Contexts, factories, and ordering]: TraceabilityMatrix.md#contexts-factories-and-ordering
[Generic/subclass and constraint breadth]: TraceabilityMatrix.md#genericsubclass-and-constraint-breadth
[Custom transactions and access patterns]: TraceabilityMatrix.md#custom-transactions-and-access-patterns
[Built-in stages, intents, and effects]: TraceabilityMatrix.md#built-in-stages-intents-and-effects
[Maintained relationships and indexing]: TraceabilityMatrix.md#maintained-relationships-and-indexing
[Broader lifecycle and event combinations]: TraceabilityMatrix.md#broader-lifecycle-and-event-combinations
[Richer reports, caching, and global hygiene]: TraceabilityMatrix.md#richer-reports-caching-and-global-hygiene
[Publication readiness]: TraceabilityMatrix.md#publication-readiness
[Custom event contracts and registration]: TraceabilityMatrix.md#custom-event-contracts-and-registration
[Custom event evaluation and extensions]: TraceabilityMatrix.md#custom-event-evaluation-and-extensions

Documentation task evidence: [completed rebaseline plan](../past/planning/P10202602.Three-release-workflow.plan.md). No
feature implementation or independent consumer result is asserted by this planning update.
