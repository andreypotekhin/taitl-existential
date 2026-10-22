# Quantifiers and Indexes Design

Status: entity expression dispatch design; broader index capabilities retain their recorded implementation limits.

## User outcome

Express universal and existence constraints, using collections or indexes to relate in-memory entities.

The [specification](../specifications/QuantifiersAndIndexes.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

All delegates to a predicate-based expression. Exists selects one collection, set, or map source and one predicate form.
Public SingleIndex/MultiIndex and SingleJoin/MultiJoin serve entity relationships; configuration/runtime indexes instead
select handlers and retain encountered events. Some index tests live under a package named unused.

- [universal expression evaluation](../../../src/main/java/com/taitl/ex/concrete/ConcreteAll.java)
- [collection/set/map existence evaluation](../../../src/main/java/com/taitl/ex/concrete/ConcreteExists.java)
- [public index classes](../../../src/main/java/com/taitl/existential/indexes)
- [configuration handler indexes](../../../src/main/java/com/taitl/ex/logic/configuration/indexes)

## Approach and tradeoffs

Keep business relationship indexes distinct from internal dispatch indexes. Choose collection traversal or indexed
lookup explicitly; an index is useful only when its maintenance cost and transaction semantics are understood.

## Entity expression dispatch

Bare All/Exists expressions are entity rules rather than business event handlers. Configuration indexes them under
the shared Expression class and their declared TypeKey. Runtime lookup includes this route once per distinct matched
entity TypeKey alongside the ordinary event keys. Existing ordering merges the selected expressions and handlers in
declaration order; existing EventField caching stores the merged list. Expression classes do not become business events.

The scope is every reported entity/access event. Ordinary events use their supplied entity; transitions use the
after-value, falling back to the before-value for deletion. Expressions are evaluated once per matching declaration
per encountered event and validation attempt, regardless of the number of elementary/compound event splits.
Unreported entities are not scanned. Event-bound predicates retain their configured event scope.

False quantifiers produce PredicateFailure using the rule description and follow existing validation aggregation and
transaction recovery. Collection/index ownership and mutation synchronization remain with the application. Dispatch
does not maintain or undo those sources. The route adds type metadata lookups and cached rule references, not retained
runtime entities or a new registry. Key construction is proportional to the distinct expanded TypeKeys; lookup merging
occurs on cache misses, and subsequent lookups reuse the cached list.

This correction preserves existing collection/key matching and endpoint extraction. Quantification over transition
objects and broader index maintenance semantics require their own design; they are not implied by this route.

## Evidence and acceptance work

- [quantifier unit tests](../../../src/test/java/com/taitl/existential/quantifiers)
- [entity dispatch acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java)
- [existing relationship-index tests](../../../src/test/java/com/taitl/exlogic/unused/indexes)
- [runtime index tests](../../../src/test/java/com/taitl/ex/core/transactions/TransactionIndexesTest.java)

Direct collection/index tests and end-to-end expression dispatch tests establish different claims. Passing adjacent
unit tests does not establish complete transaction validation or maintained-index semantics.

## Caller-supplied relationships and composition

All can wrap an Exists predicate directly, including a conditional All selecting subjects that require a relationship.
List/set sources express same-class membership; a map from subject to a selected related entity expresses a cross-class
relationship using the map bipredicate. The predicate receives the matched source key and mapped value. It can verify
that a non-null dwelling has the cat's location without stream traversal or an explicit predicate test call.

Default Exists performs membership through the source collection/set/map API. This avoids scanning indexed sources
when the stored key/value is not needed. Predicate-bearing Exists retains matched-source object semantics; recovering
that original object may require iteration through a set or map. Collection predicates retain source duplicates.
Maps/sets must obey their equality/hash or comparator contracts. Source contents are caller-owned and read at
evaluation time; mutation during evaluation requires application synchronization. Hash keys must remain stable while
stored. No automatic index maintenance, snapshotting, or source rollback is implied.

## Diagnostics, recovery, and concurrency

Exists requires a matching key/entity in its source; empty sources and missing keys reject the evaluated entity.
Conditional All invokes its predicate only when its condition holds. Reflected generic type names use comma separators
without spaces, matching runtime type expansion. These rules retain their configured descriptions in PredicateFailure
and link to [quantifier troubleshooting](../../../Troubleshooting.md#entity-quantifier-validation).

Rejected validation uses the existing transaction lifecycle: failed commit detaches the handle, and explicit rollback
runs its callbacks and releases its transaction-local state. Source collections, indexes, and application objects are
not undone. Checkpoint uses the existing retry path and reevaluates the caller's current source state.

Expression dispatch introduces no mutable invocation state on shared rule objects. Reports remain local to each
validation attempt; configuration retains its existing metadata caches. Application predicates and source collections
must support the application's access/concurrency model. The dispatcher neither synchronizes their mutations nor
introduces an automatic relationship-index maintenance policy.

## Performance

For E runtime event/type combinations and T distinct expanded types, shared expression dispatch adds O(T) metadata
keys after O(E) key collection. Cold lookup merges matching configured references; warm lookup reuses the cached list.
No runtime entity is added to a cache key. Source traversal/index lookup costs belong to the selected quantifier and
remain distinct from dispatch. The [measurement record](../performance/runs/20261021T223641Z.QuantifierDispatch.md)
compares the dispatch change using an unchanged transaction workload and states its allocation/cardinality limits.
