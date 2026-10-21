# Automation Focus

Follow [Process](Process.md), [V1](../project/V1.md), the [backlog](../project/Backlog.md), and
[traceability](../project/TraceabilityMatrix.md). The [rebaseline](../project/ReleaseRebaseline.md) replaces the initial
subsystem-first split with V1 representative core, V2 broader standard usage, and V3 custom-event extensibility.

## Current priority

Begin [Night City Core Workflow] in Sprint 01: develop the documented Night City workflow covering success, useful
rejection, recovery, a basic All/Exists relationship, and entity evolution. Resolve the selected design/specification
decisions and acceptance before coding. Deliver the core success/violation slice first and retain existing passing
behavior.

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Restore useful verification feedback early and link evidence for each concern. Do not treat inherited marks or
neighboring tests as proof of the representative workflow.

## Subsequent work

The [Relationships and Evolution] and [Failure Recovery and Resource Hygiene] epics complete the selected
relationships/evolution and failure/resource cases. [Night City Artifact Consumption and Performance] makes Night City
an independent Maven project consuming the installed library and collects representative performance evidence.
[V1 Hardening and Closeout] hardens the complete admitted workflow. Instance isolation/configuration breadth belongs
to V2; custom events to V3. Independent investigation/design may proceed ahead, but implementation follows actual
dependencies.

## Intake

Review [TODOs](../todo/) and [approved suggestions](../suggestions/approved/) through the backlog's current owners.
Reassignment preserves approval state and does not admit optional strategy, hot-reload, dashboard, or plugin features.
Robustness and performance matter in every release. Archive evidenced completed work by the existing conventions.

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Relationships and Evolution]: ../project/Backlog.md#relationships-and-evolution
[Failure Recovery and Resource Hygiene]: ../project/Backlog.md#failure-recovery-and-resource-hygiene
[Night City Artifact Consumption and Performance]: ../project/Backlog.md#night-city-artifact-consumption-and-performance
[V1 Hardening and Closeout]: ../project/Backlog.md#v1-hardening-and-closeout
