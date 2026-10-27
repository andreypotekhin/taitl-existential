# Transactions and Evaluation Specification

These requirements describe transaction and evaluation behavior, including the selected Sprint 08 contract.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Unchanged legacy `+` marks are preserved. Sprint 08 claims marked `+` have direct passing acceptance coverage;
remaining open semantics and implementation status are recorded in the traceability matrix.
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

### Transaction state diagnostics

+ Direct closed-handle and rollback-only rejections from the transaction operation guard expose structured diagnostics
through the existing exact ExistentialException type. Unknown, completed, and detached string-ID lookup expose
TRANSACTION_NOT_FOUND through the existing exact NotFoundException type. Codes display as TRANSACTION-CLOSED,
TRANSACTION-ROLLBACK-ONLY and TRANSACTION-NOT-FOUND. Each has a stable diagnostic-specific troubleshooting heading.
No declaration API or public Diagnostic field is added.

+ Guard diagnostics retain the known operation and detached string notes for requested action and transaction ID.
Retained disposed managed views report TRANSACTION-CLOSED with their already retained operation and requested
index access, omitting the transaction ID because their owner guard has been released.
They have no rule/context, event/type, stage, or children. Commit/checkpoint/rollback requests name that boundary;
entity/access reporting and memo registration use report; query actions leave boundary unknown. ID lookup misses
leave operation and boundary unknown and record only the requested ID. They never reconstruct an operation by
retaining detached handles. evaluationStopped is false because no rule evaluation started at that guard.

+ Closed state takes precedence after disposal, even when the handle was previously rollback-only. A failed checkpoint
or commit still permits only explicit rollback or close; a guard rejection does not run callbacks or change state.
Messages give the specific remedy for the state. Existing constructor-only exceptions remain undecorated, and original
fallback messages remain available if diagnostic construction/rendering fails. Snapshots use the existing immutable,
serializable, escaped, lazy-rendering and ownership contract; causes of prior lifecycle failure are not inferred.

Covered entry points are those already routed through the operation guard, including memo registration and live
managed-view access, plus the separate disposed-view rejection. Independent memo-query IllegalStateException guards,
shutdown/configuration failures, invalid input, and memo validation/admission remain outside this selected contract.
Within rule callbacks, existing callback-failure
classification and cause preservation still apply to any exception the callback propagates.

Acceptance: [transaction state
diagnostics](../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseTransactionState.java)
and [construction/fallback](../../../src/test/java/com/taitl/ex/logic/diagnostics/TransactionDiagnosticLogicTest.java).
Performance: [transaction diagnostics](../performance/topics/TransactionDiagnostics.md).

### Checkpoint failure recovery

+ A failed checkpoint throws and makes the handle rollback-only. Further entity/access event reporting, memo
registration, checkpoint, and commit are rejected. The active string-ID entry is removed as for a failed commit;
string-ID event lookup cannot continue. The caller may explicitly roll back or close the handle, or abandon it without
an additional library registry retaining it. Rollback callbacks run only on explicit rollback. Neither failure nor
cleanup undoes application effects already performed. Correction and retry require a fresh transaction.

Acceptance: [checkpoint failure recovery](../../../src/test/java/com/taitl/specs/library_usage/UserCanRecoverAfterCheckpointFailure.java).

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

The validation evaluation collects ordinary constraint violations into its report under the
[evaluation failure policy](IntentsAndEffects.spec.md#evaluation-failure-policy). Checkpoint and commit retain their
exception-based failure signal when violations are reported. A report does not imply that every rule ran: an
evaluation with effects stops when its first violation is encountered. No public report-returning API is added.

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

+ User can specify an instance of Transaction class to use in a library transaction through `Ex.begin()` or the
instance API.

+ Custom transactions allow rules to be configured at runtime using dynamically available information, rather than
requiring all rules upfront at application startup. For example, a method parameter can provide a validation threshold;
API endpoint processing is one use case, not a restriction on the feature.

### Runtime transaction rule lifetime

+ Custom rules and captured values are retained by the active handle's transaction-local binding for evaluation and
released on disposal. Independent invocation bindings must not leak their dynamic values into one another or retain
their closures in shared configuration/selection caches. Caller-owned objects are not cleared by the library.

Acceptance: [runtime custom transaction isolation and disposal](../../../src/test/java/com/taitl/specs/configuration_workflow/custom_transactions/UserCanConfigureCustomTransactions.java).

## Execution stages

### Assigning execution stages

+ User can assign rules to different stages of transaction execution.

### Built-in execution stages

The stage names are begin, immediate, commit, checkpoint, rollback, and validation. In prose these names are
lowercase; uppercase is used for constants such as `StageName.BEGIN`. Entity evaluation in the begin stage is distinct
from a transaction begin lifecycle callback.

### Default validation stage

Invariants and effects default to the validation stage unless explicitly assigned to another stage. Intents default
to [immediate evaluation](IntentsAndEffects.spec.md#immediate-intent-validation). Explicit stage selection takes
precedence over the rule category's default.

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

### Compiled execution plans

The [runtime execution-plan contract](ExecutionPlans.spec.md) defines automatic validation-only reporting, complete
transaction rule inventories, and reusable rule selections. Compiled plans follow
[context resolution order](LibraryAndConfiguration.spec.md#deterministic-context-resolution),
[entity type matching](EventsAndConstraints.spec.md#entity-type-matching), and the stage/failure contracts in this
document and [IntentsAndEffects.spec.md](IntentsAndEffects.spec.md#evaluation-failure-policy). Compilation does not
execute user predicates, cache entity validity, or alter configured lifecycle callback order.

### Separate stage evaluations

+ There is a separate evaluation per execution stage. The begin, immediate, and validation entity evaluations have
distinct triggers and lifetimes.

### Naming the begin evaluation

+ Evaluation of begin-stage entity rules is called begin evaluation.

### Naming the immediate evaluation

+ Evaluation of immediate-stage rules is called immediate evaluation.

### Naming the validation evaluation

+ Evaluation of validation-stage rules is called validation evaluation.

### Independent transaction evaluations

Evaluations of separate transactions are independent, even if these transactions are nested.

### Transaction event scope

Only the events reported during a transaction participate in its evaluations.

## Evaluation in the begin stage

### Assigning rules to begin

+ User can assign rules to the begin stage.

### Lifetime of begin evaluation

+ The begin evaluation becomes active when a transaction starts and remains available while its handle permits
reporting. A successful checkpoint preserves begin evaluation and its once-per-event/type history. Commit, rollback,
close, or a checkpoint failure ends further report-time evaluation on that handle.

### Frequency of begin events

The begin-stage entity rules execute upon the first reported event for each event/type key in the transaction,
independently of entity identity. Subsequent reports with the same key do not repeat begin evaluation. After a
successful checkpoint an already encountered key stays encountered; a newly reported key may still trigger begin
rules. Lifecycle begin callbacks execute during transaction creation, rather than once per reported key.

## Evaluation in the immediate stage

### Lifetime of immediate evaluation

+ The immediate evaluation becomes active when a transaction starts and remains active after a successful checkpoint.
Commit, rollback, close, or a checkpoint failure ends further report-time evaluation on that handle.

### Frequency of immediate events

+ The immediate evaluation runs on every reported trigger event while reporting is permitted, including repeated
reports that later fold for boundary validation.

### Successful checkpoint continuation

+ A successful checkpoint leaves the same handle available for new events, further checkpoints, or commit. The immediate
handlers and intents continue to run on subsequent reports, subject to applicable rules. The begin evaluation retains
its existing key history. This continuation does not reset the transaction's event-folding identity or certify future
entity values.

Acceptance: [successful checkpoint continuation](../../../src/test/java/com/taitl/specs/library_usage/UserCanContinueAfterSuccessfulCheckpoint.java).

## Evaluation in the validation stage

### Commit and checkpoint validation

+ The validation evaluation is triggered on commit or checkpoint and is ignored on rollback.

### Validation effect dispatch

+ Effects execute for the folded event/entity evaluations to which they apply, in declaration order with checks and
subject to the [evaluation failure policy](IntentsAndEffects.spec.md#evaluation-failure-policy). Boundary evaluation
preserves entity encounter order; it does not regroup entities by type or move effects into a separate final pass.

Acceptance: [ordered effect stopping and collection](../../../src/test/java/com/taitl/specs/library_usage/UserCanStopOrderedEffectsAfterViolation.java).

### Validation event deduplication

+ The validation stage evaluates folded event/type/entity records from the runtime event index. Duplicate reports
fold under the [event identity contract](ExecutionPlans.spec.md#event-identity-and-folding); distinct entities retain
separate evaluations.
