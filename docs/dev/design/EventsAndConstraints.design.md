# Events and Constraints Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Report entity creation, modification, access, and evolution, then receive a useful failure when an invariant is false.

The [specification](../specifications/EventsAndConstraints.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Entity and access events are explicitly reported. Port permits one missing endpoint; Transit requires both states.
The splitter derives elementary/combined events and memo state supplies before-values for bi-event handlers.
Constraint evaluation and handler invocation use the configured entity/type keys rather than scanning all rules.

- [public dispatch delegation](../../../src/main/java/com/taitl/ex/core/existential/ExistentialEvents.java)
- [derived events][link-01]
- [before-state memo storage](../../../src/main/java/com/taitl/ex/logic/transactions/data/MemoData.java)
- [public exception types](../../../src/main/java/com/taitl/existential/exceptions)

## Approach and tradeoffs

Keep event reporting explicit and limited to in-memory entities. Preserve fail-early input validation and keep
before-state ownership visible to callers; do not imply automatic observation of arbitrary object mutation.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Specify event identity when equal/mutable entities and repeated events interact with runtime indexing.
- Specify superclass/interface/generic matching and the exact guarantees for subclass constraints.
- Define custom event registration, collision handling, splitting, dispatch, and intent interactions before coding.
- Clarify additional event values and stable violation metadata without assuming an unimplemented public API.

## Evidence and acceptance work

- [entity events](../../../src/test/java/com/taitl/specs/library_usage/UserCanSendEntityEvents.java)
- [memo behavior](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseMemoForBiEvents.java)
- [event input/equality tests](../../../src/test/java/com/taitl/existential/events)

V2-E01, V2-E03, and V2-E04. Existing event functionality is a V1 evaluation prerequisite, not a scope reassignment.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Performance

Measure repeated versus unique events and memo/split overhead with explicit entity counts and rule counts.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[link-01]: ../../../src/main/java/com/taitl/ex/logic/evaluation/events/split_events/event_splitter/EventSplitter.java
