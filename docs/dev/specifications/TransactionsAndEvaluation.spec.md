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

### Transaction lifecycle handles

+ Commit, rollback, and checkpoint use the Tr returned by begin, directly or through the facade's Tr overloads. The public
Ex and Existential facades do not accept transaction IDs for these lifecycle operations. A completed transaction cannot
be reused for another attempt; operation keys passed to begin remain strings.

+ If commit fails, the active string-ID entry is removed while the caller-held Tr remains available for explicit rollback.
Rollback callbacks run only when rollback is explicitly called; abandoning the handle does not run those callbacks.
Rollback releases the handle's transaction-local data even if a rollback callback fails. A failed commit does not retain
the handle in a second registry.

Acceptance: [lifecycle and failure recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java)
and [public lifecycle API](../../../src/test/java/com/taitl/specs/library_usage/UserCanInitiateTransactions.java), plus
[failed begin recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterBeginFailure.java).

### Checkpoint failure recovery

+ A checkpoint failure leaves the transaction handle available for further application work or explicit rollback. The
library does not automatically mark it rollback-only. Callers are responsible for effects already performed and for
callbacks that may run again on a later attempt.

Acceptance: [checkpoint recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterCheckpointFailure.java).

### Rollback callback failure recovery

+ Explicit rollback attempts every rollback lifecycle callback and rollback-stage handler in declaration order, even if
an earlier callback fails. It reports the first failure and attaches later failures as suppressed exceptions. The
transaction's library-owned state is released in all cases. Rollback callbacks run only after an explicit rollback call.

Acceptance: [rollback callback recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanConfigureEntities.java).

### Library shutdown

+ Library shutdown stops admission of new transactions and waits for registered active handles and in-flight transaction
calls to finish before releasing library resources. Existing transactions may complete or explicitly roll back while
shutdown is waiting. A finite timeout or thread interruption reports failure and leaves the library draining so the
caller can finish transactions and retry close. A zero timeout waits without a time limit. Shutdown does not force
rollback or undo caller-owned effects. Calling close from a transaction callback fails immediately to avoid waiting
for that same callback to return, including when another thread has already started library shutdown.

Acceptance: [shutdown policy and concurrency](../../../src/test/java/com/taitl/specs/library_usage/UserCanShutdownLibrary.java).

### Closing a transaction from its callback

+ A callback cannot close the transaction handle whose callback is executing. Direct close fails promptly so the
transaction lifecycle can finish before its resources are disposed.

Acceptance: [transaction callback shutdown cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanShutdownLibrary.java).

### Explicit event transaction IDs

Entity/access events and memo registration can still identify an active transaction by its string ID. Callers may pass
that ID through application code without passing Tr.

### Annotated operation ownership

+ With AspectJ weaving enabled, each execution of an Op-annotated method owns a fresh transaction for its operation key.
Normal return commits that transaction and preserves the method result. Method or commit failure invokes rollback on
that same transaction and rethrows the original throwable; a rollback failure is attached as a suppressed exception.
Overlapping invocations do not overwrite one another's transaction handles.

Acceptance: [woven annotated operations](../../../src/test/java/com/taitl/specs/library_usage/UserCanUseAnnotatedOperations.java).

### Night City creation and relocation

+ The representative cat-creation operation accepts only black new cats and requires a caller-supplied dwelling at each
created or relocated cat's destination. Relocation carries explicit before and after values and preserves the cat's
color. A missing destination dwelling or changed color rejects commit.

Acceptance: [creating and relocating cats](../../../src/test/java/com/taitl/specs/library_usage/UserCanCreateAndRelocateCats.java).

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
