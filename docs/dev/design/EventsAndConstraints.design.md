# Events and Constraints Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Report entity creation, modification, access, and evolution, then receive a useful failure when an invariant is false.

The [specification](../specifications/EventsAndConstraints.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Entity and access events are explicitly reported. Port permits one missing endpoint; Transit requires both states. The
splitter derives elementary/combined events and memo state supplies before-values for bi-event handlers. Constraint
evaluation and handler invocation use the configured entity/type keys rather than scanning all rules.

- [public dispatch delegation](../../../src/main/java/com/taitl/ex/core/existential/ExistentialEvents.java)
- [derived events]
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

[Night City Core Workflow], [Relationships and Evolution], and [Failure Recovery and Resource Hygiene] own
representative events/evolution/errors; V2 broadens them; [Custom Event Contracts and Registration] and Custom Event
Evaluation and Extensions own custom events. Add user-facing positive and negative acceptance cases for each unresolved
behavior; passing adjacent unit tests do not establish the full claim. Preserve inherited completion marks until a
requirement-level review changes them.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: specify entity/type matching, null endpoints, before/after semantics, and derived-event behavior.
- Actionable errors: identify invalid input or violated rule and operation/type context, avoiding ambiguous failures.
- Failure recovery: define state and caller actions after rejected events, violations, and splitter/handler failure.
- Resource hygiene: define entity/snapshot/payload ownership and release, including custom registration when admitted.
- Performance: state unique/repeated event and split-expansion costs under representative entity/rule counts.

## Performance

Measure repeated versus unique events and memo/split overhead with explicit entity counts and rule counts. Use the
[performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[derived events]:
  ../../../src/main/java/com/taitl/ex/logic/evaluation/events/split_events/event_splitter/EventSplitter.java

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Relationships and Evolution]: ../project/Backlog.md#relationships-and-evolution
[Failure Recovery and Resource Hygiene]: ../project/Backlog.md#failure-recovery-and-resource-hygiene
[Custom Event Contracts and Registration]: ../project/Backlog.md#custom-event-contracts-and-registration
