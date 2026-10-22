# Quantifiers and Indexes Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
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
empty source rejects the entity. Sources remain caller-owned; validation neither maintains nor undoes their contents.

Acceptance: [entity quantifier validation](../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java)
and [cat creation and relocation](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).

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

Acceptance: [caller-supplied entity relationships](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).

### Exists on collections

+ User can define the 'Exists' quantifier on a collection.

### Exists on maps

+ User can define the 'Exists' quantifier on a map (index).

## Indexes

### Indexed existence lookup

User can use an index to speed up evaluation of Exists expression.
  We use SetIndex and JoinIndex classes for this

### Indexes between rules

User can create an index to pass information between the rules.
  We use Index and JoinIndex classes for this

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
