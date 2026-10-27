# Automation Focus

Follow [Process](Process.md), [V1](../project/V1.md), the [backlog](../project/Backlog.md), and
[traceability](../project/TraceabilityMatrix.md). The [rebaseline](../project/ReleaseRebaseline.md) replaces the initial
subsystem-first split with V1 representative core, V2 broader standard usage, and V3 custom-event extensibility.

## Current priority

V1 is complete as a locally installed `0.0.1` artifact and remains unpublished remotely. Sprint 06's selected instance
isolation/configuration slice and Sprint 07's context/type and execution-plan slice are complete, including the
many-row follow-up. Native BitSet combination, compiled single-row selections, and pre-expansion EventField lookup
are implemented. The [archived delivery
plan](../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md)
and [performance topic](../performance/topics/ExecutionPlans.md) retain correctness/concurrency/resource checks,
root/consumer/reactor verification, artifact evidence, and three-process comparisons with explicit limits.

Sprint 08's selected stage/default, checkpoint continuation/failure, intent conjunction, effect-aware report collection,
and runtime custom rule decisions are implemented and verified against its
[acceptance](../project/sprints/Sprint08_V2StagesIntentsAndCustomTransactions.md#acceptance). The
[implementation plan](../planning/P10232601.Sprint08-stages-intents-custom-transactions.plan.md) and
[performance evidence](../performance/runs/20261024T061720Z.Sprint08Transactions.md) capture the result. Mutation after
`begin()`, definition reuse, snapshot semantics, and broader combinations remain separate readiness questions.
Retain compiled inventory/profile and EventField ownership; Sprint 07 adds no numerical gate.

Sprint 09 is complete on 2026-10-27 under its
[review](../project/reviews/Sprint09_OperationalCoverage.review.md) and
[closeout plan](../past/planning/P10272602.Sprint09-closeout.plan.md). Preserve scoped transaction-local managed
indexes,
explicit access/general-event semantics, required-description timing, immutable lazy diagnostics and occurrence
provenance. The final follow-up adds semantic operational-error metadata, non-mutating supplied-intent bindings,
prospective supplied-rule checks, typed standard-event values and ClassValue inferred metadata. Read-only supplied
reuse, cache budgets/generation invalidation, independent ownership and terminal/idempotent facade close are supported.

Root/reactor pass 760 tests and the independent consumer seven, with no failures/skips, actual coverage/static checks
and artifact hygiene. The [closeout performance topic](../performance/topics/Sprint09Closeout.md) retains required and
investigation pairs, exact counters and explicit failure rendering costs. A small successful-path cost remains possible;
no numerical speed gate or arbitrary leak-detection/GC deadline is claimed. The public semantic metadata TODO/suggestion
are archived; programmer preconditions and caller-created exceptions keep Java behavior.

Next select the separate feature-free Sprint 10 V2 hardening/release audit. Review actual release-wide acceptance,
coverage/runtime/dependency/resource limits and distribution status. Sprint 09 functional completion does not release
V2 or authorize external publication. Custom event registration/splitting remains V3, shared mutable indexes are an
existing non-goal, and arbitrary rule mutation/hot reload is unsupported.

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Restore useful verification feedback early and link evidence for each concern. Do not treat inherited marks or
neighboring tests as proof of the representative workflow.

## Subsequent work

The V1 epics now have direct evidence for the selected relationships/evolution, recovery/resource, and independent
consumer slices. [V1 Hardening and Closeout] completed the local release and closeout checks. Broader stage policy stays
in Sprint 08, and custom events belong to V3. The current execution-plan slice preserves lifecycle behavior while
specializing validation-only reporting; it introduces no hot reload or process-wide programmatic defaults registry.

## Intake

Review [TODOs](../todo/) and [approved suggestions](../suggestions/approved/) through the backlog's current owners.
Reassignment preserves approval state and does not admit optional strategy, hot-reload, dashboard, or plugin features.
Robustness and performance matter in every release. Archive evidenced completed work by the existing conventions.

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Relationships and Evolution]: ../project/Backlog.md#relationships-and-evolution
[Failure Recovery and Resource Hygiene]: ../project/Backlog.md#failure-recovery-and-resource-hygiene
[Night City Artifact Consumption and Performance]: ../project/Backlog.md#night-city-artifact-consumption-and-performance
[V1 Hardening and Closeout]: ../project/Backlog.md#v1-hardening-and-closeout
