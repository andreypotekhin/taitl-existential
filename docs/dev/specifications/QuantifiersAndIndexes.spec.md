# Quantifiers and Indexes Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Legacy `+` marks are preserved. Add `+` to a requirement only when its current acceptance evidence passes; see the
traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/QuantifiersAndIndexes.design.md) and [specification index](../Specification.md).

## Universal Quantifier

### All entity invariants

+ User can create an invariant for an entity class using the 'All' quantifier.

### Conditional All invariants

+ User can restrict the 'All' quantifier to only apply to certain entities, by specifying a condition.

### All on entity classes

+ User can define the 'All' quantifier on an entity class.

### All on entity transitions

User can define the 'All' quantifier on an entity transition class (Transition<T>).

## Existence Quantifier

### Entity quantifier validation scope

+ Fluent All and Exists rules run during checkpoint and commit validation for each matching reported entity/access
event. They use the supplied entity for ordinary events, the after-value for transitions, and the before-value for
deletion. Unreported entities are outside this scope. A rule executes once per encountered event and validation
attempt, regardless of how many elementary/compound event keys select handlers. Existing event deduplication applies.

Expressions and event handlers preserve declaration order and context/type matching, including full/simple names and
generic type expansion. Event-bound predicates such as create remain limited to their configured event.
False All/Exists expressions reject validation with PredicateFailure and their configured description. A conditional
All skips its predicate when its condition is false. Exists requires a matching source entity/key; a missing key or
empty source rejects the entity. Caller-supplied sources remain caller-owned; validation neither maintains nor undoes
their contents. Explicitly declared managed sources follow the managed-index requirements below.

Acceptance: [entity quantifier
validation](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java)
and [cat creation and
relocation](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).

### Exists entity invariants

+ User can create an invariant on an entity class using the 'Exists' quantifier.

### Conditional same-class existence

User can require entity existence when certain condition is met (same class, within entity class code).

### Conditional cross-class existence

User can require entity existence when certain condition is met (different classes, outside entity class
code).

### All-Exists composition

+ User can specify 'Exists' quantifier as a parameter to the 'All' quantifier, thus creating an All-Exists
invariant.

All(new Exists(source)) validates the reported subject's membership. Conditional All evaluates the relationship only
for selected subjects. A caller-supplied cat-to-dwelling map can express a relationship with a key/value predicate:
the matching source key and its mapped value must satisfy that predicate. A missing subject, empty source, null mapped
dwelling, or mismatched dwelling location rejects the representative relationship. Source contents remain caller-owned.
Predicates inspecting a matching source object receive that stored object, including for an equal reported snapshot.
Membership-only Exists uses the collection/set/map membership operation without retrieving stored objects by scanning.

Acceptance: [caller-supplied entity
relationships](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).

### Exists on collections

+ User can define the 'Exists' quantifier on a collection.

### Exists on maps

+ User can define the 'Exists' quantifier on a map (index).

## Indexes

### Indexed existence lookup

User can use an index to speed up evaluation of Exists expression.
The public shapes are SingleIndex, MultiIndex, SingleJoin, and MultiJoin.

### Indexes between rules

User can create an index to pass information between the rules.
SingleIndex and MultiIndex provide key lookups; SingleJoin and MultiJoin expose relationships between two subject types.

## Managed transaction-local indexes

The following requirements have direct acceptance in the linked user stories. Release scope and remaining work are
tracked separately in Sprint 09 and release traceability.

### Managed definitions and transaction ownership

+ User can declare transaction-local single/multi indexes and joins, identifying subject types through Class or TypeKey
and supplying key extractors. Every active Tr receives separate mutable contents for its applicable definitions.
Applications retain responsibility for loading, locking, persistence, and synchronization of their working objects.
Shared mutable entity indexes across transactions are outside this contract. Existing standalone index behavior and
caller-owned quantifier sources do not become automatically managed through incidental use.

### Automatic enrollment and maintenance

+ Automatic maintenance is enabled by default for managed definitions. Applicable creation, update, deletion, and
  explicit
before/after transition reports maintain the indexed working set. A report contributes once to managed maintenance,
not once for every elementary/compound event expansion; physical updates can fold across reports. Read reports enroll
objects by default. An independent read
enrollment opt-out leaves mutation maintenance enabled.

User can supply seed collections for already loaded objects without emitting synthetic events or invoking access rules.
Seeds are transaction-local inputs; the library does not clear or mutate the caller's seed collection. Source objects
remain caller-owned and relevant later mutations must be reported.

### Automatic maintenance opt-out

+ User can disable automatic maintenance while keeping explicit population and add/remove/reindex operations. Such an
index performs no automatic event routing, key extraction, or pending-change maintenance for itself. Normal transaction
event recording and other indexes continue according to their own policies.

### Managed indexing policy scopes

+ Automatic maintenance and read enrollment are independently configurable through global configuration sources, app and
instance options, context options, configured/supplied transaction options, and individual-index options. Defaults
enable
both. An omitted property inherits instead of disabling its broader value.

Resolve each property independently, from the most specific explicit setting: index, transaction, matching contexts
in reverse resolved order, then app/instance default. Global file and programmatic sources retain their existing
precedence. Bind an effective policy to the active handle before maintenance begins; settings do not retroactively redo
already performed work. Existing configuration finalization/freeze rules remain applicable.

### Managed entity identity and key changes

+ Membership uses object identity. Distinct objects remain distinct even if equals returns true; mutable equals/hashCode
does not strand a tracked entity. Lookup keys use their equality/hash contract and must remain stable while indexed.
An update moves an enrolled object from its previously recorded key to its current key. An explicit before/after event
retires the before object and enrolls the after object. No application business-ID field is inferred automatically.

### Single-valued replacement

+ A later applicable enrollment or maintenance action at an occupied key replaces the previous value in a single-valued
index or join side. Removal or movement of the replacement does not resurrect the displaced value. With two snapshots
of Order 42, reporting draft then approved selects approved; deleting approved leaves key 42 absent. An explicit
draft-to-approved transition likewise retires draft. Multi-valued indexes and joins retain all distinct enrolled object
identities at a key. Query timing does not change the replacement result.

### Construction and boundary folding

+ Index objects may be constructed uniformly at transaction start. When an index is definitely not needed before a
boundary, population and relationship maintenance leverage folded event state before dependent boundary rules run.
Repeated reports do not require replaying every physical index update or rebuilding joins for every report. Necessary
ordering, prior-key, and supersession metadata must preserve replacement and endpoint semantics; existing validation
event identity, folding, and rule invocation counts are unchanged.

### Early access and pending changes

+ An explicit transaction-bound index access before a boundary observes relevant preceding reports and seeds. Between
accesses, pending changes are folded and only the required maintenance is applied before the next query. Early-access
and boundary-only paths produce equivalent contents for the same accepted reports and seeds. No thread-local current
transaction is required. Unknown callback behavior cannot be treated as proof that an index is boundary-only.

### Managed quantifier and join visibility

+ All/Exists rules can consume a transaction's managed source without streams or explicit predicate test calls.
  Dependent
rules see the complete prepared working set for their evaluation, independent of maintenance-handler declaration order.
Library maintenance is not a declared user Effect and does not by itself activate stop-on-first-violation semantics.
User checks/effects retain their existing declaration order and failure policy. Join query views protect their
structure;
modification uses supported maintenance methods rather than bypassing enrollment through exposed map/set views.

### Managed index lifecycle and failures

+ Successful checkpoints retain contents and folding history for later reports and queries. Failed checkpoints/commits
follow the rollback-only and active-ID-detachment contract. Disposal clears managed contents, seeds, pending changes,
and identity/key metadata even when a caller retains a closed handle or view. Access through a disposed view is
rejected.
Explicit rollback callbacks remain explicit-only; index cleanup does not undo caller objects or external effects.

Malformed definitions, invalid keys, or extractor failures identify the operation and managed index, preserve the cause,
and explain the remedy. Failed maintenance must not expose a partly applied batch. Independent handles share no mutable
managed contents, and an individual handle is not safe for concurrent mutation.

Acceptance: [explicit
maintenance](../../../src/test/java/com/taitl/specs/library_usage/UserCanMaintainManagedIndexesExplicitly.java),
[managed ownership and rules](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseManagedIndexes.java),
[policy scopes](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureManagedIndexPolicies.java),
[joins](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseManagedJoins.java),
[early queries and failures](../../../src/test/java/com/taitl/specs/library_usage/UserCanQueryManagedIndexesDuringTransaction.java),
[release and isolation](../../../src/test/java/com/taitl/specs/library_usage/UserCanReleaseManagedIndexes.java), and
[shared consumer story](../../../src/test/java/com/taitl/specs/library_usage/UserCanMaintainWorkingRelationships.java).

### Concrete declaration and access API

+ IndexDefinition.single/multi/singleJoin/multiJoin create typed immutable tokens. Register the token with a context
or transaction's index method and obtain the per-handle facade with Tr.index(token). withPolicy returns a new token.
Unknown tokens and conflicting names are rejected. The same token can be registered repeatedly. Seeds use facade bulk
add methods without reporting an event. Query maps/sets reject structural mutation; facade maintenance methods remain
available in automatic and manual modes.

+ Read, RL, and Access reports follow read enrollment; Create, Update, Write, Delete, and explicit bi-event endpoints
maintain mutation membership. Combined marker/custom events have no inferred maintenance action. Report-time rules
see previously accepted state; the current candidate enters indexes after those rules succeed. Denial does not enroll.
Single-valued keys are captured at acceptance; multi-valued keys are extracted once per pending identity at access or
boundary. Mutating unreported objects or key objects remains outside the maintenance contract.

+ Early index failures also make the handle rollback-only and detach ID lookup. Escaped views reject further queries;
explicit rollback/close remains available. Retiring index membership does not remove earlier validation event subjects.

## Internal indexing

### Configuration finalization

System automatically finalizes configuration for op upon start of a transaction.

### Configuration rule indexing

System indexes configuration rules per op when configuration is finalized.
Configuration indexes are owned by Config object for business op.

### Runtime event indexing

System indexes the received events at runtime.
The runtime indexes are owned by Tr object.
Evaluation runs (e.g. at Validation state) use configuration and runtime indexes for performance.
