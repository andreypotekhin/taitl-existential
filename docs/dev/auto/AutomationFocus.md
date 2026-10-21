# Automation Focus

Follow [Process](Process.md), [V1](../project/V1.md), the [backlog](../project/Backlog.md), and
[traceability](../project/TraceabilityMatrix.md). The [rebaseline](../project/ReleaseRebaseline.md) replaces the initial
subsystem-first split with V1 representative core, V2 broader standard usage, and V3 custom-event extensibility.

## Current priority

Select V1-E01 / Sprint 01: develop the documented Night City workflow covering success, useful rejection, recovery,
a basic All/Exists relationship, and entity evolution. Resolve the selected design/specification decisions and
acceptance before coding. Deliver the core success/violation slice first and retain existing passing behavior.

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Restore useful verification feedback early and link evidence for each concern. Do not treat inherited marks or
neighboring tests as proof of the representative workflow.

## Subsequent work

V1-E02/E03 complete the selected relationships/evolution and failure/resource cases. V1-E04 makes Night City an
independent Maven project consuming the installed library and collects representative performance evidence. V1-E05
hardens the complete admitted workflow. Instance isolation/configuration breadth belongs to V2; custom events to V3.
Independent investigation/design may proceed ahead, but implementation follows actual dependencies.

## Intake

Review [TODOs](../todo/) and [approved suggestions](../suggestions/approved/) through the backlog's current owners.
Reassignment preserves approval state and does not admit optional strategy, hot-reload, dashboard, or plugin features.
Robustness and performance matter in every release. Archive evidenced completed work by the existing conventions.
