# Events and Constraints Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Legacy `+` marks are preserved. Add `+` to a requirement only when its current acceptance evidence passes; see the
traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/EventsAndConstraints.design.md) and [specification index](../Specification.md).

## Configuring Custom Events

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Events

### Reporting class events

User can emit (record) a class event, such as accessing or modifying entity.

### Equal event deduplication

Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same
effect) as a single such event.

On the representative entity path, event equality uses the same event/type key and the same entity instance.
Repeated reports on that instance fold; distinct equal snapshots remain distinct reported entities. Source membership
uses its collection equality/comparator contract instead of runtime entity identity.

### Events require library configuration

User can't send any events before the library is configured.

### Events require a started transaction

User can't send any events before the transaction has started.

### Events after transaction completion

User can't send any events after transaction has been committed/rolled back.

### Configuration after the first event

User can't modify configurations (e.g. create invariants) after sending first event.
- Except for specifying custom Transaction object for transaction run

### Port event type inference

+ When emitting a port event without an explicit type key, library infers the type from the non-null entity
value (preferring t1 when present, otherwise t0). Both t0 and t1 can't be null.

### Transit event endpoints

+ When emitting a transit event without an explicit type key, both t0 and t1 must be non-null.

## Constraints

(Below, the terms 'constraints' and 'invariants' are used interchangeably)

## Constraint on a single entity

### Internal field constraints

User can create a constraint on a field within an entity's class code.

### External field constraints

User can create a constraint on a field outside of entity's class code.

### Third-party field constraints

User can create a constraint on a field of a third party class (e.g. a class which code is unavailable).

### Two-field constraints

User can create a constraint on two fields of a class.

### Multiple-field constraints

User can create a constraint on multiple fields of a class.

### Collection size constraints

User can create a constraint on a collection field size.

### Collection contents constraints

User can create a constraint on a collection field contents.

## Constraints on multiple entities

### Same-class entity relationships

Create a constraint on two entities of same class within entity class code.

### Cross-class entity relationships

Create a constraint on two entities of different classes, outside entity class code.

### Same-class existence constraints

Require another entity to exist when entity exists, (same class, within entity class code).

### Cross-class existence constraints

Require another entity to exist when entity exists, (different classes, outside entity class code).

## Constraints on entity evolution

### Entity transition invariants

+ User can declare an invariant on entity transition.

### Entity porting invariants

User can declare an invariant on entity porting.

### Explicit evolution and endpoint validation

+ The representative cat evolution rule preserves color and requires a dwelling at the selected destination.
Transit reports explicit non-null before/after values. Port permits one null endpoint for creation or deletion;
both endpoints null are rejected immediately. An invalid event does not enter validation and leaves the transaction
available for a corrected event. Failed commit identifies the operation and violated rule descriptions, detaches
active transaction lookup, and permits explicit rollback. Source collections and caller-owned snapshots are not undone.

Acceptance: [relationships and endpoint validation](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java)
and [cat relocation](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).

## Constraints on evolution of multiple entities

### Additional event values

User can pass additional values when emitting an event.

## Constraints on subclasses

### Inherited class constraints

Constraints on parent classes apply to subclasses under [entity hierarchy matching](#entity-hierarchy-matching).

## Entity type matching

### Entity hierarchy matching

+ Event dispatch traverses generic superclass signatures with type-variable substitution and directly or indirectly
  implemented interfaces. A declaration reached through an interface diamond executes once. `UserCanApplyInheritedRules`
  covers a multi-edge generic superclass and a diamond interface.

Rules declared for the reported entity class, its superclass chain, and its directly or indirectly implemented
interfaces participate when compatible. Diamond inheritance does not multiply one declaration's execution. Applicable
rules retain declaration order inside their resolved context, rather than executing by class/interface specificity.
An explicit generic key supplements available type evidence; it cannot erase known runtime subclass/interface rules.

### Generic argument matching

An explicitly reported concrete generic type selects compatible exact, raw, and unbounded-wildcard rules. Using an
illustrative `Box<T>`, `Box<Cat>` can select `Box<Cat>`, raw `Box`, and `Box<?>`, but does not select `Box<Animal>`
merely because Cat extends Animal. The same restriction applies to non-collection generic classes: a callback typed
as `Box<Animal>` could otherwise store a different Animal subtype in a Box reserved for cats.

Concrete generic arguments are invariant. Nested wildcard positions follow the same compatibility discipline; a
wildcard inside a concrete invariant argument does not make its enclosing argument covariant. Preserve symbolic
exact matching for string-only TypeKeys. Inheritance/bound matching requires reliable metadata, not guessing which
class an ambiguous simple name denotes.

+ Type-key expansion treats only the entity type's direct generic arguments as wildcard positions. For example,
  reporting `Outer<Box<Cat>>` does not select `Outer<Box<?>>`; only `Outer<?>` and raw `Outer` are compatible.
  `UserCanApplyInheritedRules` verifies that the nested callback is not invoked.

### Generic signature recovery

Use explicit TypeKey metadata and retained superclass/interface signatures to recover actual arguments where possible,
including substitution across multiple inheritance edges. For example, `CatBox extends Box<Cat>` provides a known
Box argument without an explicit key at each report. The library does not infer erased arguments from object contents
or substitute a declared upper bound as though it were the concrete argument.

Keep unresolved information distinct from a known type. Apply rules whose compatibility is provable, including general
raw/unbounded rules; unresolved concrete compatibility follows the
[warning policy](LibraryAndConfiguration.spec.md#unresolved-generic-warnings). Metadata-aware matching preserves
actual class identity in reflected hierarchy and descriptor-cache keys and does not change existing TypeKey
printed-key/equality contracts. The bounded dispatch descriptor cache evicts the oldest inserted key after its
4,096-entry limit; eviction changes recomputation cost only.

### Upper-bounded generic matching

A rule for `Box<? extends Animal>` can select an entity reported as `Box<Cat>` when the Cat-to-Animal relationship is
known. Apply superclass/interface projection and generic substitution before testing the bound. Multiple/nested generic
arguments obey the same compatible-bound and concrete-invariance rules; matching does not change callbacks' Java type
contracts. Lower-bounded rule patterns using `? super` are outside this selected contract and fail with a corrective
unsupported-pattern diagnostic during configuration rather than silently leaving an intended rule unapplied.

+ A rule using a direct upper bound selects a reported generic subclass when its reflected argument signature proves
  the subtype relationship. `UserCanApplyInheritedRules` verifies `Box<? extends Being>` receives `CatBox` through
  the resolved `Box<Cat>` signature and a raw `Box` event carrying an explicit anonymous TypeKey.

+ `IndexEvs` rejects a configured lower-bound pattern before publishing its indexes and links to the
  [lower-bound troubleshooting guidance](../../../Troubleshooting.md#unsupported-lower-bounded-type-rules).
  `UserCanApplyInheritedRules` verifies the failure occurs during configuration finalization.

### Shared type matching

Matching results may be shared across distinct entities with the same relevant type/event signature, but actual rule
checks still apply to their respective entities. Resource, ordering, concurrent-publication, and validation-only
requirements are defined in [ExecutionPlans.spec.md](ExecutionPlans.spec.md).

## Constraint violation reporting

### Constraint violation exceptions

User should receive an exception in case of constraint violation, along with the details.

### Constraint descriptions

User can pass a human-readable description when creating a constraint, to help with reporting.

### Constraint error numbers

User can specify an error number when creating a constraint, to help with reporting.

### Violation details

Constraint violation exception contains details about the violation, such as human description of violated
rule.

### Required constraint descriptions

Optionally, user can configure the library to require a description for each constraint.

## Custom Event Types

### Defining custom event types

User can define a custom event type.

### Custom event handlers

User can define event handler for custom event type.

### Custom event context constraints

User can create a constraint based on custom event type (Context).

### Custom event transaction constraints

User can create a constraint based on custom event type (Transaction).

### Custom event splitting

User can customize event splitter to emit events of custom event type.

### Reporting custom events

User can send/record custom event from application code.
The system invokes custom event handler upon encountering the custom event.
