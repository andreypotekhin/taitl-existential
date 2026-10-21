# Intents and Effects Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Authorize entity operations with intents and execute declared effects at a predictable transaction stage.

The [specification](../specifications/IntentsAndEffects.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Intent declarations restrict corresponding event types for entities; immediate intent evaluation is the default. Effects
use event handlers and can be configured in contexts or transaction rules. Existing specifications test combined
CU/UD/CUD effects and stage-assigned intents, including validation-stage rejection.

- [intent enforcement](../../../src/main/java/com/taitl/ex/logic/evaluation/intents/EvaluateIntents.java)
- [handler execution](../../../src/main/java/com/taitl/ex/logic/evaluation/events/actions/ExecuteHandlers.java)
- [effect configuration](../../../src/main/java/com/taitl/existential/constraints/Effect.java)
- [intent configuration](../../../src/main/java/com/taitl/existential/constraints/Intent.java)

## Approach and tradeoffs

Keep permission checks explicit and effect ordering observable. Do not promise effect rollback or transactional
atomicity for caller side effects. Reuse indexed rule selection across invariant, intent, and effect evaluation.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Specify how wildcard/context precedence composes with intents and transaction overlays.
- Reconcile the migrated Early/Middle/Late wording with implemented stage names and lifecycle hooks.
- Define handler failure propagation, partial effects, and ordering across split events.
- Specify custom event interactions together with EventsAndConstraints before enabling new dispatch behavior.

## Evidence and acceptance work

- [intent specifications](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureIntents.java)
- [combined effects](../../../src/test/java/com/taitl/specs/library_usage/UserCanHandleCombinedEventEffects.java)
- [execution unit tests](../../../src/test/java/com/taitl/ex/logic/evaluation/actions/ExecuteHandlersTest.java)

[Stages, Intents, and Transaction Customization] owns standard intents/effects/stages; Custom Event Evaluation and
Extensions owns their integration with custom events. Add user-facing positive and negative acceptance cases for each
unresolved behavior; passing adjacent unit tests do not establish the full claim. Preserve inherited completion marks
until a requirement-level review changes them.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: specify authorization scope, stage timing, callback order/count, and context/transaction composition.
- Actionable errors: distinguish authorization denial from callback failure and identify operation/stage/rule/cause.
- Failure recovery: define partial effects and permitted follow-up actions; caller side effects are not auto-reverted.
- Resource hygiene: define handler/captured-state and transaction lifetimes, registration scope, and concurrency limits.
- Performance: isolate dispatch/authorization overhead from callback work and measure stage/event cardinality.

## Performance

Separate predicate/handler cost from dispatch overhead and include denied intents and repeated events. Use the
[performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[Stages, Intents, and Transaction Customization]: ../project/Backlog.md#stages-intents-and-transaction-customization
