# Events and Constraints Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
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

Constraints on parent class apply to any subclass.

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
