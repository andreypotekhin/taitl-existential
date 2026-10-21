# Intents and Effects Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/IntentsAndEffects.design.md) and [specification index](../Specification.md).

## Configuring effects

<a id="ie-001"></a>

**IE-001** User can specify side effect (event handler) for a class event.

<a id="ie-002"></a>

**IE-002** User can specify side effect for transaction lifecycle event (begin, commit, rollback).

<a id="ie-003"></a>

+**IE-003** User can specify side effect for generic types using a TypeKey.

<a id="ie-004"></a>

+**IE-004** User can specify transaction lifecycle side effect for custom transaction type using a TypeKey.

## Configuring intents

<a id="ie-005"></a>

+**IE-005** User can configure intents for event types and entity classes.

<a id="ie-006"></a>

+**IE-006** User can configure intents using builders and rule instances in Context and Transaction.

<a id="ie-007"></a>

+**IE-007** Intent supports handlers for access and entity events, such as read, write, change and update.

## Effects

<a id="ie-008"></a>

**IE-008** User can create a side effect for an entity by configuring an event handler for entity event.

<a id="ie-009"></a>

**IE-009** Effects can be assigned to different stages of transaction lifecycle: Early, Middle and Late .

<a id="ie-010"></a>

**IE-010** For early and middle stage effects, event handlers gets executed upon receiving a trigger event.
  - Handlers are executed immediately.
  - Handlers get invoked per each received trigger event.

<a id="ie-011"></a>

**IE-011** Late stage handlers are executed at transaction commit or checkpoint, once per trigger event type.
  - For several trigger events of the same type, late stage event handler is executed only once.

<a id="ie-012"></a>

**IE-012** The order of effect invocation follows the order of their declaration.

<a id="ie-013"></a>

+**IE-013** User can define effects for combined events (CU, UD, CUD) emitted on create/update/delete transitions.

## Intents

<a id="ie-014"></a>

+**IE-014** User can define intents on event type and entity class, similarly to invariants, such as 'read', 'write'
intents.

<a id="ie-015"></a>

+**IE-015** As soon as an intent defined for some event type, the system prevents the user from sending the events for
any
entity class that is not covered by a similar intent for same event type.
  Example: As soon as an 'read' intent declared for entity class A, the system will:
  - require explicit 'read' intent declared for each other class that receives a 'read' event
  - if such sending occurs, throws an exception (IntentViolation)

<a id="ie-016"></a>

+**IE-016** By default, the system validates intents immediately upon receiving an event.

<a id="ie-017"></a>

+**IE-017** User can assign intents to execution stages (Precondition, Immediate, Validation), with Immediate as default
stage.
