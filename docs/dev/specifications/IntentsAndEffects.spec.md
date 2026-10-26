# Intents and Effects Specification

These requirements describe intent and effect behavior, including the selected Sprint 08 contract.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Unchanged legacy `+` marks are preserved. Sprint 08 claims marked `+` have direct passing acceptance coverage;
remaining open semantics and implementation status are recorded in the traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/IntentsAndEffects.design.md) and [specification index](../Specification.md).

## Configuring effects

### Class event effects

User can specify side effect (event handler) for a class event.

### Transaction lifecycle effects

User can specify side effect for transaction lifecycle event (begin, commit, rollback).

### Generic type effects

+ User can specify side effect for generic types using a TypeKey.

### Custom transaction type effects

+ User can specify transaction lifecycle side effect for custom transaction type using a TypeKey.

## Configuring intents

### Intent event and entity configuration

+ User can configure intents for event types and entity classes.

### Intent builders and instances

+ User can configure intents using builders and rule instances in Context and Transaction.

### Intent access and entity handlers

+ Intent supports handlers for access and entity events, such as read, write, change and update.

## Effects

### Entity event effects

User can create a side effect for an entity by configuring an event handler for entity event.

### Effect execution stages

Effects can be assigned to begin, immediate, and validation, as well as the applicable lifecycle stages described by
[built-in execution stages](TransactionsAndEvaluation.spec.md#built-in-execution-stages). Invariants and effects
[default to validation](TransactionsAndEvaluation.spec.md#default-validation-stage).

### Report-time effect timing

Effects in the begin stage follow the
[begin event frequency](TransactionsAndEvaluation.spec.md#frequency-of-begin-events), once per reported event/type key.
Effects in the immediate stage follow the
[immediate event frequency](TransactionsAndEvaluation.spec.md#frequency-of-immediate-events), on every reported trigger.
A begin lifecycle callback is separate from these entity rules.

### Boundary effect timing

Effects in the validation stage execute at checkpoint or commit against folded events, subject to
[validation effect dispatch](TransactionsAndEvaluation.spec.md#validation-effect-dispatch). Distinct entities retain
separate evaluations; folding does not mean once per event type across all entities.

### Effect invocation order

The order of effect invocation follows the resolved declaration order, interleaved with applicable checks. Until a
violation occurs, effects run normally; a successful evaluation runs all applicable checks and effects. An effect can
prepare state required by a later check. Entity encounter order is preserved at boundaries.

### Evaluation failure policy

+ When the effective rules for the current evaluation contain no declared effects, ordinary constraint violations are
collected into its report and remaining checks continue across rules and entities.

+ When declared effects participate, including conditional actions and transaction-local effects, the first ordinary
constraint violation is recorded in the report and stops the entire evaluation. No later check or effect runs,
including for subsequent entities or matching contexts. Effects from unrelated stages do not select this policy.
Effects already executed remain performed. Rules execute in order until failure: effects are not skipped in advance
or deferred until all checks have run.

Unexpected callback failures and authorization denials stop immediately regardless of effect participation. They are
not ordinary violations to collect while continuing execution. The existing exception-based failure signal from
checkpoint/commit is retained after report collection; this contract introduces no public report-returning API.
Explicit rollback retains its separate
[cleanup failure contract](TransactionsAndEvaluation.spec.md#rollback-callback-failure-recovery).

Acceptance: [ordered effect stopping and collection](../../../src/test/java/com/taitl/specs/library_usage/UserCanStopOrderedEffectsAfterViolation.java).

### Combined event effects

+ User can define effects for combined events (CU, UD, CUD) emitted on create/update/delete transitions.

## Intents

### Intent definitions

+ User can define intents on event type and entity class, similarly to invariants, such as 'read', 'write'
intents.

### Intent access enforcement

+ Once an intent guards an event type at a stage for an operation, a reported entity/type requires a matching intent
for that guarded event type. Otherwise the library throws `IntentViolation`. Without an intent guard for that event
type at that stage, this requirement does not deny the event.

### Intent composition

+ Every applicable intent predicate must pass. This applies across parent, wildcard, and concrete contexts, matching
general/specific entity types, multiple declarations within one context, and transaction-local rules. Matching and
ordering follow [context resolution](LibraryAndConfiguration.spec.md#deterministic-context-resolution) and
[entity type matching](EventsAndConstraints.spec.md#entity-type-matching). A passing general intent cannot override a
failing specific intent, and a transaction-local grant cannot bypass an applicable configured restriction.

Alternative permissions must be expressed explicitly within a predicate, for example
`entity -> isOwner(entity) || isAdministrator()`. A denial stops authorization and further execution immediately;
matching does not authorize caching predicate outcomes.

Acceptance: [conjunctive intent narrowing](../../../src/test/java/com/taitl/specs/library_usage/UserCanNarrowApplicableIntents.java).

### Immediate intent validation

+ By default, the system validates intents immediately upon receiving an event.

### Intent execution stages

User can assign intents to begin, immediate, or validation, with immediate as the default. Their timing follows the
[stage lifetimes and frequencies](TransactionsAndEvaluation.spec.md#execution-stages), including continued immediate
checks after a successful checkpoint.
