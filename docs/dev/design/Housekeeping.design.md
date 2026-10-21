# Housekeeping Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Complete or abandon operations without retaining transaction entities, stale rules, or unbounded cache state.

The [specification](../specifications/Housekeeping.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

DisposeTran closes a transaction and removes its registry entry after normal completion. TransactionLogic.close
clears the registry. Type-key and event-field caching exist, but CacheHandlers operations deliberately throw
UnsupportedOperationException. Those stubs are not evidence of implemented cross-transaction handler caching.

- [transaction disposal](../../../src/main/java/com/taitl/ex/logic/transactions/actions/DisposeTran.java)
- [active transactions](../../../src/main/java/com/taitl/ex/logic/transactions/data/TrRegistry.java)
- [type-key caching](../../../src/main/java/com/taitl/ex/cross/caching/TypeKeyCache.java)
- [explicit unimplemented cache operations](../../../src/main/java/com/taitl/ex/cross/caching/CacheHandlers.java)

## Approach and tradeoffs

Tie resources to their owners and fail explicitly for unavailable operations. Prefer deterministic release of
transaction state over garbage-collection timing; separate useful shared caches from entity-retaining state.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Specify cleanup after commit, rollback, failed validation, failed handlers, and library close.
- Define cache scope, invalidation, boundedness, and concurrency before implementing additional caching.
- Define what leak detection can observe and when reporting occurs; do not promise deterministic GC.
- Verify that memo state and prepared transaction overlays release references on each supported exit path.

## Evidence and acceptance work

- [registry tests](../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java)
- [type cache tests](../../../src/test/java/com/taitl/ex/logic/events/TypeKeyCacheTest.java)

V1-E03 owns admitted success/failure cleanup; V2-E04 broadens global/cache behavior; V3 covers extension resources.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: define retained/released state for every admitted exit and the exact observable leak-reporting claim.
- Actionable errors: identify invalid/stale lifecycle use or retained ownership and the caller cleanup remedy.
- Failure recovery: define cleanup retry/idempotence, partial close behavior, and preservation of original failures.
- Resource hygiene: specify registry, memo, overlay, stream, callback, and cache ownership/bounds/invalidation.
- Performance: inspect retained-state growth alongside repeated success/failure workloads; timing does not establish
  resource safety.

## Performance

Use bounded repeated-transaction workloads and retained-state counts; timing alone cannot establish no memory leak.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.
