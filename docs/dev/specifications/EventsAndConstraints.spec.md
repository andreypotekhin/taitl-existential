# Events and Constraints Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/EventsAndConstraints.design.md) and [specification index](../Specification.md).

## Configuring Custom Events

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Events

<a id="ec-001"></a>

**EC-001** User can emit (record) a class event, such as accessing or modifying entity.

<a id="ec-002"></a>

**EC-002** Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same
effect) as a single such event.

<a id="ec-003"></a>

**EC-003** User can't send any events before the library is configured.

<a id="ec-004"></a>

**EC-004** User can't send any events before the transaction has started.

<a id="ec-005"></a>

**EC-005** User can't send any events after transaction has been committed/rolled back.

<a id="ec-006"></a>

**EC-006** User can't modify configurations (e.g. create invariants) after sending first event.
- Except for specifying custom Transaction object for transaction run

<a id="ec-007"></a>

**EC-007** When emitting a port event without an explicit type key, library infers the type from the non-null entity
value (preferring t1 when present, otherwise t0). Both t0 and t1 can't be null.

<a id="ec-008"></a>

**EC-008** When emitting a transit event without an explicit type key, both t0 and t1 must be non-null.

## Constraints

(Below, the terms 'constraints' and 'invariants' are used interchangeably)

## Constraint on a single entity

<a id="ec-009"></a>

**EC-009** User can create a constraint on a field within an entity's class code.

<a id="ec-010"></a>

**EC-010** User can create a constraint on a field outside of entity's class code.

<a id="ec-011"></a>

**EC-011** User can create a constraint on a field of a third party class (e.g. a class which code is unavailable).

<a id="ec-012"></a>

**EC-012** User can create a constraint on two fields of a class.

<a id="ec-013"></a>

**EC-013** User can create a constraint on multiple fields of a class.

<a id="ec-014"></a>

**EC-014** User can create a constraint on a collection field size.

<a id="ec-015"></a>

**EC-015** User can create a constraint on a collection field contents.

## Constraints on multiple entities

<a id="ec-016"></a>

**EC-016** Create a constraint on two entities of same class within entity class code.

<a id="ec-017"></a>

**EC-017** Create a constraint on two entities of different classes, outside entity class code.

<a id="ec-018"></a>

**EC-018** Require another entity to exist when entity exists, (same class, within entity class code).

<a id="ec-019"></a>

**EC-019** Require another entity to exist when entity exists, (different classes, outside entity class code).

## Constraints on entity evolution

<a id="ec-020"></a>

**EC-020** User can declare an invariant on entity transition.

<a id="ec-021"></a>

**EC-021** User can declare an invariant on entity porting.

## Constraints on evolution of multiple entities

<a id="ec-022"></a>

**EC-022** User can pass additional values when emitting an event.

## Constraints on subclasses

<a id="ec-023"></a>

**EC-023** Constraints on parent class apply to any subclass.

## Constraint violation reporting

<a id="ec-024"></a>

**EC-024** User should receive an exception in case of constraint violation, along with the details.

<a id="ec-025"></a>

**EC-025** User can pass a human-readable description when creating a constraint, to help with reporting.

<a id="ec-026"></a>

**EC-026** User can specify an error number when creating a constraint, to help with reporting.

<a id="ec-027"></a>

**EC-027** Constraint violation exception contains details about the violation, such as human description of violated
rule.

<a id="ec-028"></a>

**EC-028** Optionally, user can configure the library to require a description for each constraint.

## Custom Event Types

<a id="ec-029"></a>

**EC-029** User can define a custom event type.

<a id="ec-030"></a>

**EC-030** User can define event handler for custom event type.

<a id="ec-031"></a>

**EC-031** User can create a constraint based on custom event type (Context).

<a id="ec-032"></a>

**EC-032** User can create a constraint based on custom event type (Transaction).

<a id="ec-033"></a>

**EC-033** User can customize event splitter to emit events of custom event type.

<a id="ec-034"></a>

**EC-034** User can send/record custom event from application code.
The system invokes custom event handler upon encountering the custom event.
