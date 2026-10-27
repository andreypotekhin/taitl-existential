# Quantifiers and Indexes Design

The paired specification defines observable behavior. This design covers caller-supplied quantifier sources and
transaction-local managed indexes. Delivery readiness and verification belong in project and execution-plan records;
the managed-index implementation and evidence are recorded there.

## User outcome

Express universal and existence constraints, using collections or indexes to relate in-memory entities.

The [specification](../specifications/QuantifiersAndIndexes.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

All delegates to a predicate-based expression. Exists selects one collection, set, or map source and one predicate form.
Public SingleIndex/MultiIndex and SingleJoin/MultiJoin serve entity relationships; configuration/runtime indexes instead
select handlers and retain encountered events. Some index tests live under a package named unused. The existing
`Transaction.index(name, getKey)` creates a named MultiIndex but supplies neither an entity TypeKey nor an automatic
event binding; it is not evidence of automatic maintenance.

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
transaction recovery. Ownership and mutation synchronization for caller-supplied sources remain with the application.
Dispatch does not maintain or undo those sources. The route adds type metadata lookups and cached rule references,
not retained runtime entities or a new registry. Key construction is proportional to the distinct expanded TypeKeys;
lookup merging occurs on cache misses, and subsequent lookups reuse the cached list.

This correction preserves existing collection/key matching and endpoint extraction. Quantification over transition
objects is not implied by this route. Managed maintenance requires an explicit declaration under the design below;
passing an ordinary source to Exists does not enroll it automatically.

## Evidence and acceptance work

- [quantifier unit tests](../../../src/test/java/com/taitl/existential/quantifiers)
- [entity dispatch
  acceptance](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java)
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
not undone. Successful checkpoints reevaluate current source state and leave reporting active. Failed checkpoints
detach the handle and make it rollback-only; correction and retry require a fresh transaction.

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

## Managed index ownership

A managed index is a declared relationship lookup whose transaction-local contents are maintained by the library.
The application declares the subject Class or TypeKey, key extractor, index shape, and applicable operation contexts.
SingleIndex and MultiIndex provide one value or multiple values per key; SingleJoin and MultiJoin relate two subject
types through a shared key. Retain these names rather than introduce the historical SetIndex/JoinIndex names.

Definitions contain configuration metadata. Each active Tr owns separate contents, enrollment records, pending changes,
and join views. This fits a business operation that loads and locks its own working dataset, reports events, and
releases
that dataset at completion. The application owns loading, locking, persistence, and synchronization of its objects.
Registering a managed definition does not adopt an existing caller-owned source collection or silently change the
semantics of standalone indexes.

Shared mutable relationship contents across transactions are outside this design. Such contents could save repeated
construction, but would require publication, invalidation, and isolation rules, and could expose objects an operation
has not loaded or locked. Shared compiled definitions are useful without sharing those entity references.

## Enrollment and explicit maintenance

Automatic maintenance is enabled by default. Reported creation, update, deletion, and explicit before/after transitions
maintain matching definitions. Read enrollment is independently enabled by default and can be disabled while mutation
maintenance remains enabled. Explicit seed collections admit already loaded entities without requiring synthetic read
events or invoking access rules. Seeding is an index population operation, not a declaration that data has been
persisted.

An automatic-maintenance opt-out leaves the transaction-local index under explicit application control. Seeding and
manual add/remove/reindex remain available. That index incurs no automatic maintenance routing, key-extraction, or
pending-change bookkeeping. Independent event recording required by transaction validation still occurs. This is useful
when the application already has an efficient maintenance path.

Use an immutable IndexingPolicy value to describe the independent automatic-maintenance and read-enrollment choices.
Each option supports inheritance rather than treating an omitted value as false. Global configuration sources and
app/instance options supply defaults; matching contexts, configured or supplied transaction definitions, and the
individual index can supply overrides. Resolve each property independently: index override, transaction override,
matching contexts from last to first in the existing resolved order, then app/instance defaults. Ordinary configuration
file and programmatic precedence is unchanged. Do not introduce a separate process-wide mutable settings registry.
Bind the resulting policy to the active handle before its managed index processes data; later work must not
retroactively
change previously performed maintenance. Policy capture and configuration freeze obey the library configuration
contract.

## Identity and replacement

Track enrolled entities by object identity, consistently with runtime event folding. Keep extracted keys separately
from the object's mutable fields; changing equals/hashCode must not prevent removing or moving an enrolled object.
Lookup keys retain their equality/hash contract. The application must report relevant mutations and keep extracted key
objects stable while indexed. The library does not infer business identity from arbitrary fields.

A single-valued index or join side uses replacement semantics. Later applicable enrollment or maintenance at a key
supersedes the previous value. Removing or moving the replacement does not restore the displaced value. Multi-valued
indexes and joins retain distinct object identities at a shared key, including equal-but-distinct objects.

For example, two Order objects with ID 42 can represent draft and approved snapshots. A before/after transition retires
the draft and enrolls the approved snapshot; deleting the approved snapshot leaves no mapping. Independent reports of
those snapshots still obey replacement rather than retaining a hidden fallback to the draft. A location grouping with
several cats belongs in MultiIndex, not in a single-valued index with an implicit uniqueness requirement.

Replacement has an ordering cost: deduplicating A, B, A into first-encounter order A, B can change the winner. A summary
of final entity fields also cannot reveal that a value was previously displaced. Retain the sequence and supersession
information necessary for the selected policy. This does not justify replaying every report or rebuilding every join.
The folded managed-maintenance state supplements existing event records; it does not redefine rule invocation folding.

## Construction, access, and folded maintenance

Creating empty index objects when a transaction starts is allowed and provides a uniform lifecycle. Construction,
population, and maintenance are distinct costs. Defer population and relationship maintenance until needed.

When the compiled consumer information proves an index is not needed before a boundary, use folded event state and
prepare its complete working set before dependent boundary rules execute. Repeated reports must not become repeated
set insertions, removals, or full join reconstructions. Validation-only rules are one case; boundary-only effects and
checkpoint-only consumers are others. Unrelated immediate authorization does not itself require an index to be eager.

When an index is accessed earlier, make all relevant preceding changes available before returning the result. Between
accesses, fold pending changes and apply only the necessary changes at the next access. Query timing must not change
replacement, deletion, or final membership. An explicit view is bound to a Tr; access must not depend on a thread-local
current transaction. Declare consumer dependencies where the library can observe them; do not inspect lambda bodies or
assume an unknown consumer cannot query an index early.

The existing first-encounter event set is not, by itself, sufficient to implement last-replacement semantics. Preserve
compact ordering, old-key, and supersession metadata where necessary. Correctness metadata may be captured at reporting
time even when physical maintenance is deferred; do not promise that every key extractor can always be deferred.
Avoid a raw log proportional to repeated reports. Separate and measure this bookkeeping from actual maintenance.

## Managed maintenance components

Keep automatic maintenance separate from user Effect declarations. Otherwise generated handlers could accidentally
change the effect-aware validation failure policy or make relationship availability depend on declaration order.
Compile type-to-index bindings alongside ExecutionPlan metadata and reuse TypeKey matching and EventField ownership.
Expand a reported event for maintenance once; elementary/compound event families must not duplicate enrollment.

IndexMaintenanceLogic coordinates focused actions such as CompileIndexMaintenancePlan, BindManagedIndexes,
FoldIndexChanges, PrepareManagedIndexes, and DisposeManagedIndexes. IndexMaintenancePlan contains immutable bindings
and consumer information; ManagedMembership and ManagedSide contain only one handle's mutable contents.
IndexingPolicyLogic resolves
options, and RequiresIndexMaintenance expresses the decision to perform work. These component names are distinct from
the existing event-recording IndexingLogic and configuration ConfigurationLogic. Keep actions,
business rules, and data units small; group and annotate Logic delegates and use Creator construction as elsewhere.

Update joins by affected keys/groups instead of rebuilding all relationships after each change. Where many values
share a key, do not store every pair of a Cartesian product merely to answer a grouped lookup. Expose protected,
transaction-bound query views; structural mutations go through the index's supported maintenance methods so enrollment
and opposite join directions stay consistent.

## Failure, disposal, and evidence obligations

Report malformed definitions, unsupported types, invalid keys, and extractor failures with the index name, operation,
subject/side, cause, and remedy, without dumping entity payloads. A boundary maintenance failure follows the existing
failed-checkpoint/commit contract. Earlier access failures must not expose a partly applied batch. Prior application
effects are not undone.

A successful checkpoint retains index contents and maintenance history for subsequent work. A failed boundary leaves
only explicit rollback/close available. Disposal releases contents, seeds, pending changes, identity/key metadata, and
transaction-bound view references, including through a retained closed handle. No secondary failed-handle registry is
introduced. Independent handles never share these mutable contents; one handle remains unsafe for concurrent mutation.

Correctness evidence must compare early-access and boundary-only results, identity-based moves, replacement/deletion,
policy scope, and repeated checkpoints. Resource evidence must inspect deterministic reference release, not force GC.
Performance must separate construction, report bookkeeping, population, incremental updates, lookup, and whole
workflows,
including repeated events and large collision groups. Record measurements in microseconds, three fresh JVM samples,
and source/environment identity. Measurements establish baselines before any numerical gate is considered.

## Concrete declarations and binding

Immutable IndexDefinition tokens declare one of the four existing facade shapes. Factories single, multi, singleJoin,
and multiJoin accept a name, explicit Class/TypeKey subjects, and extractors. Register the returned token through
Context/Transaction.index or their builders; Tr.index(token) resolves the active handle's view. withPolicy returns a
new token: register and use that returned token. Duplicate effective names from distinct tokens fail binding; repeated
registration of the same token is idempotent. Ordinary transactions allocate no managed maintenance coordinator.

IndexingPolicy.inherit supplies optional overrides; defaults enables both properties. Fluent automatic and enrollReads
return immutable values. Sources use indexing.automatic and indexing.enrollReads. Policies are bound once and index
configuration freezes at binding. Existing standalone Transaction.index(name, extractor) remains a manual API.

Seed with facade addAll/addAllLeft/addAllRight. Explicit mutations remain available with either automatic policy;
map/set query views are guarded and structurally read-only. All/Exists accept explicit definition tokens and resolve
views through the current invocation's Tr, passed through Expression.evaluate(entity, tr). Shared expressions hold no
active handle and use no thread-local state.

## Admission and compact storage

Read, RL, and Access are read enrollment; Create, Update, Write, Delete, and explicit bi-event endpoints maintain
mutation membership. Combined marker/custom events have no inferred maintenance meaning. Both matching join sides
participate once, independent of event-family expansion. Report-time rules first see previously accepted reports and
seeds. A denied candidate never enrolls; the candidate becomes visible after its report succeeds.

Single-valued membership captures keys at acceptance to preserve destructive displacement history. Multi-valued
membership folds pending identity enrollment and defers extraction until access/boundary; it validates all keys before
applying the pending batch. Repeated reports therefore need one extraction per final pending identity, rather than
per event. Explicit mutation flushes prior pending work to preserve ordering. Extractors must not query/mutate their
own index. Mutable source objects are live; neither deferred extraction nor event folding promises field snapshots.

ManagedMembership retains desired identity/key state, single-valued winners, and materialized identity groups, with a
bounded pending identity set. Changes touch affected members rather than rebuilding a large bucket. Join views derive
related groups without retaining a Cartesian product. Type-to-side routing uses raw subject buckets and a bounded
256-signature per-handle cache, retaining descriptors/classes rather than event payloads. Existing type matching and
per-instance unresolved-generic warning policy apply.

Retiring a before-snapshot removes its index membership, not its previously reported validation events. An all-events
Exists may therefore reject an earlier Read of that snapshot after relocation. Seed a loaded before-snapshot when
only the replacement should be an event subject, or configure appropriate event-scoped checks. This preserves the
existing expression-dispatch contract.

Any extraction/maintenance failure, including an early query, detaches active lookup and makes the handle rollback-only.
Escaped views share that guard, so partly applied internals cannot be queried. Close clears backing collections in
place and replaces large buffers, drops extractor closures and access callbacks, and invalidates escaped views.
