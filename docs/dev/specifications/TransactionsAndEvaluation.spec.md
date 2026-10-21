# Transactions and Evaluation Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/TransactionsAndEvaluation.design.md) and [specification index](../Specification.md).

## Library usage

### Class rule configuration

+ User can configure class rules.

### Access rule configuration

+ User can configure access rules for a class.

### Transaction lifecycle rule configuration

User can configure transaction lifecycle rules for a class.

### Starting a transaction

+ User can start a transaction.

### Committing a transaction

+ User can commit a transaction.

### Rolling back a transaction

+ User can roll back a transaction.

### Transaction checkpoints

+ User can initiate transaction checkpoint.

### Reporting entity modifications

+ User can send events to record entity modification.

### Reporting entity access

+ User can send events to record entity access.

### Reporting through transaction objects

+ User can send entity and access events directly through a transaction object.

### Events require a transaction

+ User can't send events outside a transaction.

### Events require configured rules

+ User can't send events if no rules have been configured.

## Library lifecycle phases

### Library usage phases

Library usage falls into configuration, execution, and validation phases.

### Configuration phase

In the configuration phase, the user defines rules - constraints and effects - using contexts and
transactions.

### Execution phase

In the execution phase, the user begins and ends transactions and sends events to the library.

### Execution-time effects

In the execution phase, the library executes execution-time side effects.

### Validation phase triggers

The validation phase triggers upon transaction commit, or at a checkpoint.

### Constraint validation and reporting

In the validation phase, the library evaluates all applicable constraints and reports violations.

### Validation-time effects

In the validation phase, the library executes validation-time side effects.

## Configuring transactions

### Rules on transaction objects

+ User can configure the rules (constraints) such as invariants, effects and intents on a Transaction object.

### Custom transaction factories

+ User can specify a custom factory for creation of Transaction objects in a Context.

### Transaction factory inheritance

+ Transaction factory from parent Context is used for child Contexts, unless overridden in the child Context.

### Caller-supplied transactions

+ User can specify an instance of Transaction class to use in a library transaction.
  Rationale: so the users could configure rules based on dynamic information, such as web request parameters passed
  into an API endpoint handler.
  Code: This is done by specifying Transaction instance as parameter to Ex.begin() method.

## Execution stages

### Assigning execution stages

+ User can assign rules to different stages of transaction execution.

### Built-in execution stages

+ The execution stages include: Precondition (Early), Immediate (Middle) and Validation (Late).

### Default validation stage

+ By default, the rules are assigned to Validation stage.
- Precondition stage rules execute once per trigger event.
  They may be used to initialize itermediate data structures, such as indexes, and the like.
  Subsequent trigger events of same type are ignored for precondition rules.
- Immediate stage rules execute on each trigger event - at any point during transaction.
- Validation stage rules execute on transaction commit or checkpoint.

## Evaluation workflow

Evaluation is the process of executing the rules (evaluating expressions, calling event handlers) configured for an
execution stage as part of a business operation.

Each existential transaction is associated with a business operation key, and through that with the closest matching
context, its parent contexts,
matching wildcard contexts, any configured Transaction factory and any passed-in Transaction instance.

### Participating evaluation rules

The rules configured in these contexts and transactions participate in evaluations.

### Parent rule precedence

The rules defined in parent contexts/transactions are considered to be defined 'earlier' than the rules in
children contexts/transactions.

### Rule execution order

The order of execution of rules follows the order of their definition.

### Event handler order

For each event, the order of invocations of its event handlers, if multiple handlers are defined, follows the
order of their definition

### Separate stage evaluations

+ There is a separate evaluation per execution stage: Precondition, Immediate and Validation.

### Preconditions evaluation naming

+ Evaluation of Precondition stage rules is called Preconditions evaluation.

### Immediate evaluation naming

+ Evaluation of Immediate stage rules is called Immediate evaluation.

### Validation evaluation naming

+ Evaluation of Validation stage rules is called Validation evaluation.

### Independent transaction evaluations

Evaluations of separate transactions are independent, even if these transactions are nested.

### Transaction event scope

Only the events reported during a transaction participate in its evaluations.

## Preconditions evaluation

### Assigning precondition rules

+ User can assign any rule to Precondition stage.

### Preconditions evaluation lifetime

+ Preconditions evaluation becomes active (ready to accept trigger events) on transaction start, and lasts
until commit/checkpoint/rollback.

### Precondition event frequency

+ Precondition stage rules are evaluated once per trigger event, as soon as event has been received, at any
time during transaction.

## Immediate evaluation

### Immediate evaluation lifetime

+ Immediate evaluation becomes active (ready to accept trigger events) on transaction start, and lasts until
commit/checkpoint/rollback.

### Immediate event frequency

+ Immediate stage rules are evaluated each time per trigger event, as soon as event has been received, at any
time during transaction.
Since this may affect performance, using Immediate stage is considered an advanced technique. Normally, if you don't use
side effects, you should be fine with precondition and validation (default) stages.

## Validation evaluation

### Commit and checkpoint validation

+ Validation evaluation is triggered on a commit or checkpoint of an existential transaction, ignored on
rollback.

### Validation effect dispatch

+ The Effects are evaluated by applying event handlers for each event reported during a transaction to the
corresponding entity.

### Validation event deduplication

+ Validation stage rules are evaluated once per trigger event, from an in-memory index of such events (a
runtime index),
that folds duplicate events of same type into single event.
