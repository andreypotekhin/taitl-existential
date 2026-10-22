# Housekeeping Design

Status: selected transaction-index release design; broader cleanup/cache capabilities retain their recorded limits.

## User outcome

Complete or abandon operations without retaining transaction entities, stale rules, or unbounded cache state.

The [specification](../specifications/Housekeeping.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

DisposeTran closes a transaction and removes its registry entry after normal completion. TransactionLogic.close clears
the registry. Type-key and event-field caching exist, but CacheHandlers operations deliberately throw
UnsupportedOperationException. Those stubs are not evidence of implemented cross-transaction handler caching.

- [transaction disposal](../../../src/main/java/com/taitl/ex/logic/transactions/actions/DisposeTran.java)
- [active transactions](../../../src/main/java/com/taitl/ex/logic/transactions/data/TrRegistry.java)
- [type-key caching](../../../src/main/java/com/taitl/ex/cross/caching/TypeKeyCache.java)
- [explicit unimplemented cache operations](../../../src/main/java/com/taitl/ex/cross/caching/CacheHandlers.java)

## Approach and tradeoffs

Tie resources to their owners and fail explicitly for unavailable operations. Prefer deterministic release of
transaction state over garbage-collection timing; separate useful shared caches from entity-retaining state.

## Runtime event indexes

ConcreteTr owns IndexData. Closing the transaction invokes IndexData.close, clearing its encountered event keys, type
keys, and unique runtime events in place. Clearing in place releases references even if a caller retains the closed
handle or an alias to its runtime indexes. Release is deterministic and repeatable; it does not depend on garbage
collection. Caller-owned relationship sources remain referenced by their configuration owner as intended.

Failed commit detaches active registry lookup; explicit rollback then closes transaction-local state. Abandoning a
failed handle permits normal reachability-based reclamation, but does not run rollback callbacks. This is distinct
from clearing every live transaction on library close or proving arbitrary callbacks/global caches safe.

## Evidence and acceptance work

- [registry tests](../../../src/test/java/com/taitl/ex/logic/transactions/data/TrRegistryTest.java)
- [type cache tests](../../../src/test/java/com/taitl/ex/logic/events/TypeKeyCacheTest.java)

The [relationship lifecycle cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java)
retain closed handles and inspect runtime state after commit/rollback. This evidence is limited to the selected
transaction paths; it does not establish global cache bounds, leak reporting, or all library-close behavior.

## Ownership boundaries

Runtime event/type metadata is transaction-owned; source maps and rule references belong to their configuration or
application owners. Clearing the former does not mutate the latter. State guards reject use of closed handles and
link to lifecycle remedies. The selected clear operation empties in-memory collections and is idempotent. It adds no
global state or cross-transaction synchronization. Configuration caches retain their existing ownership and contracts;
this targeted release does not establish their boundedness or a general leak-reporting mechanism.

Transaction close also clears memo values, per-stage event types and handlers, prepared configuration indexes, and
transaction-owned event-field caches. A prepared overlay owns its merged-result cache; closing it must not clear the
base event field shared with configured rules.

Library shutdown drains registered transactions and in-flight calls before closing shared library components. The
immutable `ShutdownPolicy` configures the drain timeout, with zero meaning unlimited wait. Timeouts and interruption
leave the library draining so the caller can finish work and retry. The coordinator never runs rollback callbacks or
forces application effects to be undone. Detailed observable behavior is in the
[transactions specification](../specifications/TransactionsAndEvaluation.spec.md#library-shutdown).

## Performance

Runtime-index clearing is proportional to the recorded event/key/type collections. Retained-state assertions establish
the selected reference-release contract; throughput cannot prove absence of memory leaks. The
[relationship measurement record](../performance/runs/20261022T013657Z.RelationshipsAndEvolution.md) separates timing
from deterministic runtime-state checks and states the limits of both.
