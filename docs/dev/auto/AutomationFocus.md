# Automation Focus

Follow [Process](Process.md), [V1](../project/V1.md), the [backlog](../project/Backlog.md), and
[traceability](../project/TraceabilityMatrix.md). The [rebaseline](../project/ReleaseRebaseline.md) replaces the initial
subsystem-first split with V1 representative core, V2 broader standard usage, and V3 custom-event extensibility.

## Current priority

V1 is complete as a locally installed `0.0.1` artifact. Sprints 01–05 have current acceptance/build evidence; the
release remains unpublished remotely. Sprint 06 is the next planned capability and will be discussed separately before
implementation. Use the [V1 charter](../project/V1.md) and current traceability for release evidence and deferrals.

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Restore useful verification feedback early and link evidence for each concern. Do not treat inherited marks or
neighboring tests as proof of the representative workflow.

## Subsequent work

The V1 epics now have direct evidence for the selected relationships/evolution, recovery/resource, and independent
consumer slices. [V1 Hardening and Closeout] completed the local release and closeout checks. Instance
isolation/configuration breadth belongs to V2; custom events to V3. Sprint 06 design discussion remains separate.

## Intake

Review [TODOs](../todo/) and [approved suggestions](../suggestions/approved/) through the backlog's current owners.
Reassignment preserves approval state and does not admit optional strategy, hot-reload, dashboard, or plugin features.
Robustness and performance matter in every release. Archive evidenced completed work by the existing conventions.

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Relationships and Evolution]: ../project/Backlog.md#relationships-and-evolution
[Failure Recovery and Resource Hygiene]: ../project/Backlog.md#failure-recovery-and-resource-hygiene
[Night City Artifact Consumption and Performance]: ../project/Backlog.md#night-city-artifact-consumption-and-performance
[V1 Hardening and Closeout]: ../project/Backlog.md#v1-hardening-and-closeout
