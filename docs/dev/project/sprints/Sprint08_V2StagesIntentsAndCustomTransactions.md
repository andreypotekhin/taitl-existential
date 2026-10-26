# Sprint 08: V2 Stages, Intents, and Transaction Customization

Status: complete. Dates/capacity: unassigned. Epic:
[Stages, Intents, and Transaction Customization](../Backlog.md#stages-intents-and-transaction-customization). Release:
[V2](../V2.md).
Design readiness: the selected stage/default, checkpoint, intent-composition, failure-reporting, and runtime custom
rule decisions were agreed on 2026-10-23 and implemented with direct acceptance coverage. Mutation after `begin()`,
definition reuse, snapshot semantics, and broader factory/lifecycle combinations remain deferred. Verification:
`mvn clean install` passed on 2026-10-24; the implementation execution plan and benchmark record contain details.

## Goal and user outcome

Use built-in execution stages, intents/effects, and customized transactions with predictable timing and failures.

## Dependencies and readiness

Review [Sprint 07](Sprint07_V2ContextsAndTypeMatching.md) for actual dependencies and evidence. Allow independent
investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and technical
dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

Sprint 07's [execution-plan delivery plan][execution-plan]
owns compiled inventories, context/type ordering, and validation-only specialization with the timing verified in that
sprint. This sprint owns broader stage terminology/defaults, authorization, and callback semantics; retain the shared
ExecutionPlan/EventField architecture when those contracts are extended.

- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [IntentsAndEffects.spec.md] and [IntentsAndEffects.design.md].

## Recorded decisions

- [Built-in stages](../../specifications/TransactionsAndEvaluation.spec.md#built-in-execution-stages) use lowercase
  names in prose; uppercase is reserved for constants. Invariants/effects default to validation; intents to immediate.
- [Successful checkpoints](../../specifications/TransactionsAndEvaluation.spec.md#successful-checkpoint-continuation)
  preserve immediate processing and begin once-per-event/type history.
- [Failed checkpoints](../../specifications/TransactionsAndEvaluation.spec.md#checkpoint-failure-recovery) throw,
  become rollback-only, and detach active ID lookup. Explicit rollback/close remain available; there is no automatic
  rollback callback or compensation.
- [Applicable intents](../../specifications/IntentsAndEffects.spec.md#intent-composition) must all pass, including
  parent/child/type and transaction-local restrictions. Alternatives are explicit predicate disjunctions.
- [Evaluation failure policy](../../specifications/IntentsAndEffects.spec.md#evaluation-failure-policy) preserves
  ordered effects until a violation. Checks-only evaluations collect ordinary violations into a report; evaluations
  with effects stop at the first violation across all remaining work. Unexpected failures and denials stop immediately.
  Checkpoint/commit retain their exception-based failure signal after report collection.
- [Runtime custom rules](../../specifications/TransactionsAndEvaluation.spec.md#runtime-transaction-rule-lifetime)
  use invocation-time information and transaction-local ownership. Endpoint processing is an example, not a core
  abstraction or durability requirement.

## Implementation and superseded evidence

The earlier failed-checkpoint retry expectation was replaced with rollback-only and explicit cleanup coverage in
[UserCanRecoverAfterCheckpointFailure][checkpoint-test]. The new suite also covers successful checkpoint continuation,
intent conjunction/narrowing, ordered effects with conditional/local cases, no-effect aggregation, and custom rule
isolation/disposal. Existing lifecycle/factory tests remain valid for their stated scope.

The [execution plan][implementation-plan] records implementation choices and the exact verification. The focused
performance session [Sprint 08 transaction workflows][performance-run] measures no-op dispatch, empty-handler work,
static context, configured transaction, custom preparation, and a complete custom transaction workflow in three fresh
JVMs. No numerical performance gate was introduced.

## Acceptance

Completed acceptance cases:

- After successful checkpoint, immediate handlers and intents still run; old begin keys stay encountered and new keys
  can run. Repeated successful checkpoints revalidate current values without resetting event folding.
- Failed checkpoint throws, rejects further reporting/memo/checkpoint/commit through handle and ID paths, and permits
  explicit rollback or close. Verify registry detachment, explicit-only callbacks, cleanup despite callback failure,
  abandonment without a secondary registry, and fresh-transaction recovery.
- A passing parent/general intent cannot bypass a failing child/specific or transaction-local restriction. Cover the
  same-context case, explicit disjunctions, missing matching intent for a guarded event type, and immediate denial.
- Without effects, multiple ordinary violations from multiple entities enter one report before the existing failure
  signal. Unexpected callback failures and authorization denials stop immediately instead of continuing collection.
- With effects, a successful `check A -> effect B -> check C` runs in order. Failure of A records a violation and stops
  B, C, and subsequent entities/contexts; failure of C leaves B performed and stops remaining work. Include conditional
  and transaction-local effects, unrelated-stage controls, and explicit rollback's separate cleanup behavior.
- Runtime custom rules use current invocation values, remain isolated across invocations, and release captured
  references on disposal. Dispatch/callback costs and custom transaction preparation are measured separately.

## Quality and performance

Correctness, failure recovery, and resource release are covered by acceptance tests. Existing actionable exception
messages and cleanup paths were preserved. Performance scenarios and comparability limits are recorded in the linked
run report. No baseline supports before/after claims.

## Demo and validation

From the repository root:

    mvn clean install
    mvn -q -Pbenchmarks -Dtest=CustomTransactionBenchmark -Dbenchmark.sample=1 -Dbenchmark.warmup.iterations=5000 -Dbenchmark.measure.iterations=50000 test

The full build ran 523 tests with no failures, errors, or skips; Checkstyle and PMD reported zero violations; JaCoCo
analyzed 271 classes and passed configured coverage checks. No disabled tests were found under `src/test/java`.

## Non-goals

User-defined event registration and automatic compensation of arbitrary external side effects.

## Plan and review

The completed delivery plan and run evidence are linked below. Remaining deferrals are listed above and do not block
the selected Sprint 08 scope.

[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[IntentsAndEffects.spec.md]: ../../specifications/IntentsAndEffects.spec.md
[IntentsAndEffects.design.md]: ../../design/IntentsAndEffects.design.md
[execution-plan]: ../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md
[implementation-plan]: ../../planning/P10232601.Sprint08-stages-intents-custom-transactions.plan.md
[performance-run]: ../../performance/runs/20261024T061720Z.Sprint08Transactions.md

[review template]: ../templates/SprintReview.md

[checkpoint-test]:
  ../../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterCheckpointFailure.java
[custom-test]:
  ../../../../src/test/java/com/taitl/specs/configuration_workflow/custom_transactions/UserCanConfigureCustomTransactions.java
