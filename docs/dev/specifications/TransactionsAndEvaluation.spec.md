# Transactions and Evaluation Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/TransactionsAndEvaluation.design.md) and [specification index](../Specification.md).

## Library usage

<a id="te-001"></a>

+**TE-001** User can configure class rules.

<a id="te-002"></a>

+**TE-002** User can configure access rules for a class.

<a id="te-003"></a>

**TE-003** User can configure transaction lifecycle rules for a class.

<a id="te-004"></a>

+**TE-004** User can start a transaction.

<a id="te-005"></a>

+**TE-005** User can commit a transaction.

<a id="te-006"></a>

+**TE-006** User can roll back a transaction.

<a id="te-007"></a>

+**TE-007** User can initiate transaction checkpoint.

<a id="te-008"></a>

+**TE-008** User can send events to record entity modification.

<a id="te-009"></a>

+**TE-009** User can send events to record entity access.

<a id="te-010"></a>

+**TE-010** User can send entity and access events directly through a transaction object.

<a id="te-011"></a>

+**TE-011** User can't send events outside a transaction.

<a id="te-012"></a>

+**TE-012** User can't send events if no rules have been configured.

## Library lifecycle phases

<a id="te-013"></a>

**TE-013** Library usage falls into configuration, execution, and validation phases.

<a id="te-014"></a>

**TE-014** In the configuration phase, the user defines rules - constraints and effects - using contexts and
transactions.

<a id="te-015"></a>

**TE-015** In the execution phase, the user begins and ends transactions and sends events to the library.

<a id="te-016"></a>

**TE-016** In the execution phase, the library executes execution-time side effects.

<a id="te-017"></a>

**TE-017** The validation phase triggers upon transaction commit, or at a checkpoint.

<a id="te-018"></a>

**TE-018** In the validation phase, the library evaluates all applicable constraints and reports violations.

<a id="te-019"></a>

**TE-019** In the validation phase, the library executes validation-time side effects.

## Configuring transactions

<a id="te-020"></a>

+**TE-020** User can configure the rules (constraints) such as invariants, effects and intents on a Transaction object.

<a id="te-021"></a>

+**TE-021** User can specify a custom factory for creation of Transaction objects in a Context.

<a id="te-022"></a>

+**TE-022** Transaction factory from parent Context is used for child Contexts, unless overridden in the child Context.

<a id="te-023"></a>

+**TE-023** User can specify an instance of Transaction class to use in a library transaction.
  Rationale: so the users could configure rules based on dynamic information, such as web request parameters passed
  into an API endpoint handler.
  Code: This is done by specifying Transaction instance as parameter to Ex.begin() method.

## Execution stages

<a id="te-024"></a>

+**TE-024** User can assign rules to different stages of transaction execution.

<a id="te-025"></a>

+**TE-025** The execution stages include: Precondition (Early), Immediate (Middle) and Validation (Late).

<a id="te-026"></a>

+**TE-026** By default, the rules are assigned to Validation stage.
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

<a id="te-027"></a>

**TE-027** The rules configured in these contexts and transactions participate in evaluations.

<a id="te-028"></a>

**TE-028** The rules defined in parent contexts/transactions are considered to be defined 'earlier' than the rules in
children contexts/transactions.

<a id="te-029"></a>

**TE-029** The order of execution of rules follows the order of their definition.

<a id="te-030"></a>

**TE-030** For each event, the order of invocations of its event handlers, if multiple handlers are defined, follows the
order of their definition

<a id="te-031"></a>

+**TE-031** There is a separate evaluation per execution stage: Precondition, Immediate and Validation.

<a id="te-032"></a>

+**TE-032** Evaluation of Precondition stage rules is called Preconditions evaluation.

<a id="te-033"></a>

+**TE-033** Evaluation of Immediate stage rules is called Immediate evaluation.

<a id="te-034"></a>

+**TE-034** Evaluation of Validation stage rules is called Validation evaluation.

<a id="te-035"></a>

**TE-035** Evaluations of separate transactions are independent, even if these transactions are nested.

<a id="te-036"></a>

**TE-036** Only the events reported during a transaction participate in its evaluations.

## Preconditions evaluation

<a id="te-037"></a>

+**TE-037** User can assign any rule to Precondition stage.

<a id="te-038"></a>

+**TE-038** Preconditions evaluation becomes active (ready to accept trigger events) on transaction start, and lasts
until commit/checkpoint/rollback.

<a id="te-039"></a>

+**TE-039** Precondition stage rules are evaluated once per trigger event, as soon as event has been received, at any
time during transaction.

## Immediate evaluation

<a id="te-040"></a>

+**TE-040** Immediate evaluation becomes active (ready to accept trigger events) on transaction start, and lasts until
commit/checkpoint/rollback.

<a id="te-041"></a>

+**TE-041** Immediate stage rules are evaluated each time per trigger event, as soon as event has been received, at any
time during transaction.
Since this may affect performance, using Immediate stage is considered an advanced technique. Normally, if you don't use
side effects, you should be fine with precondition and validation (default) stages.

## Validation evaluation

<a id="te-042"></a>

+**TE-042** Validation evaluation is triggered on a commit or checkpoint of an existential transaction, ignored on
rollback.

<a id="te-043"></a>

+**TE-043** The Effects are evaluated by applying event handlers for each event reported during a transaction to the
corresponding entity.

<a id="te-044"></a>

+**TE-044** Validation stage rules are evaluated once per trigger event, from an in-memory index of such events (a
runtime index),
that folds duplicate events of same type into single event.
