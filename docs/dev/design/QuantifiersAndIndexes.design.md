# Quantifiers and Indexes Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Express universal and existence constraints, using collections or indexes to relate in-memory entities.

The [specification](../specifications/QuantifiersAndIndexes.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

All delegates to a predicate-based expression. Exists selects one collection, set, or map source and one predicate
form. Public SingleIndex/MultiIndex and SingleJoin/MultiJoin serve entity relationships; configuration/runtime
indexes instead select handlers and retain encountered events. Some index tests live under a package named unused.

- [universal expression evaluation](../../../src/main/java/com/taitl/ex/concrete/ConcreteAll.java)
- [collection/set/map existence evaluation](../../../src/main/java/com/taitl/ex/concrete/ConcreteExists.java)
- [public index classes](../../../src/main/java/com/taitl/existential/indexes)
- [configuration handler indexes](../../../src/main/java/com/taitl/ex/logic/configuration/indexes)

## Approach and tradeoffs

Keep business relationship indexes distinct from internal dispatch indexes. Choose collection traversal or indexed
lookup explicitly; an index is useful only when its maintenance cost and transaction semantics are understood.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Reconcile the specification names SetIndex/JoinIndex/Index with the actual public index class names.
- Specify empty collections, missing keys, multiplicity, conditional All/Exists, and composed All-Exists behavior.
- Establish ownership, mutation, before/after updates, and failure behavior for application-maintained indexes.
- Map tests under exlogic/unused to supported behavior before treating their presence as release proof.

## Evidence and acceptance work

- [quantifier unit tests](../../../src/test/java/com/taitl/existential/quantifiers)
- [existing relationship-index tests](../../../src/test/java/com/taitl/exlogic/unused/indexes)
- [runtime index tests](../../../src/test/java/com/taitl/ex/core/transactions/TransactionIndexesTest.java)

V1-E03 owns existing index capabilities and internal indexing; V2-E02 owns complete quantifier/relationship cases.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Performance

Compare collection and index paths at stated cardinalities, including index maintenance rather than lookup alone.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.
