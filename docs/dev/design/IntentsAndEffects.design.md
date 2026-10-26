# Intents and Effects Design

The [specification](../specifications/IntentsAndEffects.spec.md) defines observable behavior. This document explains
authorization composition, effect ordering, and failure handling. Implementation evidence and remaining delivery
decisions belong in [Sprint 08](../project/sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md) and
[traceability](../project/TraceabilityMatrix.md#built-in-stages-intents-and-effects).

## User outcome

Apply general restrictions and more specific restrictions together, then execute declared effects at predictable
stages without making further changes after an evaluation has failed.

## Authorization composition

An intent guards an event type at its assigned stage. Once that event type is guarded, an applicable intent must
cover the reported entity/type. Every applicable intent predicate must pass. Matching follows the existing context
resolution, type compatibility, and event-family rules; declaration order determines evaluation order, not an override
priority. Parent, wildcard, concrete-type, inherited-type, and transaction-local predicates all constrain access.

For example, a parent can require membership in the current tenant while a child requires ownership of the entity.
Both must hold. A passing general predicate cannot bypass a failing specific predicate. The same rule applies to
multiple intents within one context and to runtime custom transaction rules. Alternative permissions are expressed
inside a predicate, such as `isOwner(entity) || isAdministrator()`. This keeps narrowing predictable without a second
implicit permission-combination language.

Intents default to immediate evaluation; invariants and effects default to validation. An explicit stage assignment
controls timing. Entity rules at begin and transaction lifecycle callbacks have different triggers, described in
[stage timing](TransactionsAndEvaluation.design.md#stage-timing). Stage names are lowercase in prose; uppercase is
reserved for constants such as `StageName.IMMEDIATE`.

## Ordered effects

Effects share the resolved declaration order with checks. They are not moved into a separate pass after all checks:
an effect may prepare state required by a later check. A successful evaluation executes all applicable work in order.
Boundary evaluation preserves entity encounter order and the existing event-folding contract; it does not regroup
entities by type to batch effects. Matching a declaration through several paths does not multiply its execution.

## Evaluation failure policy

The effective rules for the current evaluation determine whether effects participate, including transaction-local
rules and conditional actions. An effect in an unrelated stage does not change this evaluation's policy. Declared
actions determine participation; a predicate is expected to be free of side effects, and the library does not inspect
arbitrary callback bodies to prove that property.

Without effects, ordinary constraint violations accumulate in a report while remaining checks continue across rules
and entities. This provides multiple useful diagnostics from one validation attempt. With effects, execution remains
normal until the first violation; that violation is recorded and stops the entire evaluation, including subsequent
entities and contexts. No later effect or dependent check runs. Effects executed before the failure remain performed.

For `check A -> effect B -> check C`, a passing A permits B and C to run. A failing A stops both B and C when effects
participate. If C fails after B runs, B is not undone. Skipping B but continuing C could manufacture another violation;
continuing B after A failed would perform additional changes in a rejected attempt. Stopping avoids both outcomes.

Unexpected callback failures and authorization denials stop immediately in either mode. They are not ordinary
constraint violations to collect while proceeding with more work. Checkpoint and commit keep the existing
exception-based failure signal after report collection; no public report-returning API is introduced. Explicit
rollback's continue-through-failures cleanup behavior remains separate from this evaluation policy.

## Ownership, diagnostics, and recovery

Context rules belong to their configured library instance. Runtime transaction rules, captured values, overlays, and
reports belong to the active transaction binding and are released on disposal. Shared matching metadata contains no
reported payloads or runtime custom-rule closures. A single handle is not safe for concurrent mutation; supported
independent transactions retain separate mutable state. User callbacks run outside shared matching-cache locks.

Diagnostics distinguish missing authorization, a rejected predicate, and an unexpected callback failure. They retain
the original cause and identify the operation, stage, and rule where available. Failed checkpoint and commit handles
are rollback-only under the [transaction ownership design](TransactionsAndEvaluation.design.md#lifecycle-ownership).
Recovery uses explicit rollback or close, correction of caller-owned state, and a fresh transaction. Neither cleanup
nor effect dispatch promises compensation of external side effects.

## Performance

Retain the compiled inventory and EventField architecture for ordered handler selection. Effect participation can be
derived from the effective rule inventory rather than rediscovered for every reported entity; local rules must be
included. Selection caches store applicable work, never authorization outcomes or validation results. Every required
predicate still evaluates the current entity and invocation values at its configured stage.

Measure dispatch separately from predicate/action work, including denied intents, multiple reported violations,
stopping at the first violation with effects, repeated events, and runtime transaction preparation. Use the
[performance guide](../performance/Readme.md); no numerical gate follows from the selected policy.
