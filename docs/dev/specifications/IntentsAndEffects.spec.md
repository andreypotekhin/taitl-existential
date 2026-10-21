# Intents and Effects Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/IntentsAndEffects.design.md) and [specification index](../Specification.md).

## Configuring effects

### Class event effects

User can specify side effect (event handler) for a class event.

### Transaction lifecycle effects

User can specify side effect for transaction lifecycle event (begin, commit, rollback).

### Generic type effects

+ User can specify side effect for generic types using a TypeKey.

### Custom transaction type effects

+ User can specify transaction lifecycle side effect for custom transaction type using a TypeKey.

## Configuring intents

### Intent event and entity configuration

+ User can configure intents for event types and entity classes.

### Intent builders and instances

+ User can configure intents using builders and rule instances in Context and Transaction.

### Intent access and entity handlers

+ Intent supports handlers for access and entity events, such as read, write, change and update.

## Effects

### Entity event effects

User can create a side effect for an entity by configuring an event handler for entity event.

### Effect execution stages

Effects can be assigned to different stages of transaction lifecycle: Early, Middle and Late .

### Early and middle effect timing

For early and middle stage effects, event handlers gets executed upon receiving a trigger event.
  - Handlers are executed immediately.
  - Handlers get invoked per each received trigger event.

### Late effect timing

Late stage handlers are executed at transaction commit or checkpoint, once per trigger event type.
  - For several trigger events of the same type, late stage event handler is executed only once.

### Effect invocation order

The order of effect invocation follows the order of their declaration.

### Combined event effects

+ User can define effects for combined events (CU, UD, CUD) emitted on create/update/delete transitions.

## Intents

### Intent definitions

+ User can define intents on event type and entity class, similarly to invariants, such as 'read', 'write'
intents.

### Intent access enforcement

+ As soon as an intent defined for some event type, the system prevents the user from sending the events for
any
entity class that is not covered by a similar intent for same event type.
  Example: As soon as an 'read' intent declared for entity class A, the system will:
  - require explicit 'read' intent declared for each other class that receives a 'read' event
  - if such sending occurs, throws an exception (IntentViolation)

### Immediate intent validation

+ By default, the system validates intents immediately upon receiving an event.

### Intent execution stages

+ User can assign intents to execution stages (Precondition, Immediate, Validation), with Immediate as default
stage.
