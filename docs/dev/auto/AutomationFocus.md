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

Sprint 09's transaction-local managed indexes and known-resource shutdown diagnostics are implemented and verified in
[the completed delivery plan](../past/planning/P10252601.Sprint09-managed-indexes-and-diagnostics.plan.md). Preserve scoped
policy opt-outs, explicit Tr access, replacement without resurrection, folded multi-valued extraction, and deterministic
cleanup. Shared entity indexes remain deferred. Select the next ready broader event/report/cache capability from
Sprint 09; settle its contract before implementation. Automatic routing allocation is a measured optimization target,
not an established speed gate. Do not mark the whole sprint complete from this selected slice.

Sprint 09 access-event coverage and the general On dispatch/authorization correction are complete in the
[delivery plan](../past/planning/P10262601.Sprint09-access-event-acceptance.plan.md). Preserve explicit access reports,
RL-to-read selection, generic and specific intent conjunction/order, successful-checkpoint history, and explicit
cleanup.
Required-description builder coverage now passes 33 new cases, with effect policy enforcement and optional quantifier
text corrected. Preserve prospective flag timing and standalone explicit-null constructor contracts. Root/consumer/
reactor verification is recorded in Sprint 09. Real-workload evidence from the access-event record onward uses ms and
decimal MB; earlier reports retain their units, and no measurements were rerun for the presentation change.
Structured diagnostics are delivered in the selected Sprint 09 slice. Preserve the
[design](../design/EventsAndConstraints.design.md#structured-violation-diagnostics),
[delivery plan](../past/planning/P10262602.Sprint09-structured-diagnostics.plan.md), and
[performance evidence](../performance/topics/Diagnostics.md): lazy default multiline messages, immutable detached
fields, occurrence-based declaration provenance, actual intent rejection details, callback causes, and shared boundary
notes. Root/consumer/reactor evidence belongs in Sprint 09; no caller rule-ID/metadata declaration API was added.
Next work remains the broader public-exception scope, supplied-rule binding, and unresolved cache/global contracts.
Do not close Sprint 09 from this selected delivery alone.

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
