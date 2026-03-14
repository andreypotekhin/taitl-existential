## Library Specification 

Below is a list of claims made by the Existential library - statements declared in library documents.
None of these claims are legal statements or contracts.
They only describe library features and behavior for implementation.

As user stories, claims may be prefixed with 'The user can...' (or 'The user can't...').
Claims are backed by test cases in src/test/java/com/taitl/existential/specs.
A leading (+) marks items already completed and covered by tests.

### Terminology
See Terminology.md for terms and concepts.

### Library

+User can access the library using the static facade.
+User can access the library using a singleton.
User can independently configure and use multiple instances of the library.
Within an Existential instance, the user can configure multiple business operations.
Within a business operation configuration, the user can configure multiple operation contexts.
Within an operation context, the user can configure multiple rules such as invariants, effects, and intents.

#### Library configuration.

+User can change library configuration options programmatically.
+User can affect library behavior by setting behavioral flags.
+User must configure the library before use.
+User can configure library options using a config file.
+User can configure library options using a classpath resource.
+User can specify the config file with an environment variable.
+An initial config file is auto-created or otherwise available.

#### Library usage

+User can configure class rules.
+User can configure access rules for a class.
User can configure transaction lifecycle rules for a class.
+User can start a transaction.
+User can commit a transaction.
+User can roll back a transaction.
+User can initiate transaction checkpoint.
+User can send events to record entity modification.
+User can send events to record entity access.
+User can send entity and access events directly through a transaction object.
+User can't send events outside a transaction.
+User can't send events if no rules have been configured.

#### Library lifecycle phases

Library usage falls into configuration, execution, and validation phases.
In the configuration phase, the user defines rules - constraints and effects - using contexts and transactions.
In the execution phase, the user begins and ends transactions and sends events to the library.
In the execution phase, the library executes execution-time side effects.
The validation phase triggers upon transaction commit, or at a checkpoint.
In the validation phase, the library evaluates all applicable constraints and reports violations.
In the validation phase, the library executes validation-time side effects.

### Workflows

#### Configuration workflow

##### Configuring contexts

User can configure the rules (constraints) on a class with a custom Context.
Contexts are keyed by operation key.
The rules from parent contexts apply to child contexts.
The rules from parent contexts execute before the rules of child contexts.
User can specify a custom factory for creation of all Contexts.
User can specify a custom factory for creation of a Context for a specific op.
User can't configure a context without defining any rules.

##### Wildcard contexts

User can define a Context for an operation key with a wildcard in it.
The wildcard contexts whose paths match a concrete op participate in its validation.
The wildcard contexts matching the currently evaluated context participate in its validation.
The rules from matching contexts apply to the context.
The rules from matching contexts execute before the rules of the context.

##### Configuring transactions

User can configure invariants, effects and intents on a Transaction object.
User can configure the rules (constraints) with custom transaction object per transaction run.
User can specify a custom factory for creation of transactions in a Context.
Transaction factory from parent Context is used for child Contexts, unless overridden in the child Context.

##### Custom transaction instance

User can specify an instance of Transaction class to use in a library transaction.
  Rationale: so they can configure the rules based on dynamic information, such as web request parameters.
  Code: This is done by specifying Transaction instance as parameter to Ex.begin() method.

##### Configuring Constraints

User can specify an invariant (predicate) for a class event.
User can specify an invariant (predicate) for transaction lifecycle event (begin, commit, rollback).
+User can specify an invariant for generic types using a TypeKey.

##### Configuring Effects

User can specify side effect (event handler) for a class event.
User can specify side effect for transaction lifecycle event (begin, commit, rollback).
+User can specify side effect for generic types using a TypeKey.
+User can specify transaction lifecycle side effect for custom transaction type using a TypeKey.

##### Configuring Intents

+User can configure intents for event types and entity classes.
+User can configure intents using builders and rule instances in Context and Transaction.
+Intent supports handlers for access and entity events, such as read, write, change and update.

##### Execution stages

+User can assign rules to different stages of transaction execution.
+The execution stages include: Precondition (Early), Immediate (Middle) and Validation (Late).
+By default, the rules are assigned to Validation stage.
- Precondition stage rules execute once per trigger event.
  They may be used to initialize itermediate data structures, such as indexes, and the like.
  Subsequent trigger events of same type are ignored for precondition rules.
- Immediate stage rules execute on each trigger event - at any point during transaction.
- Validation stage rules execute on transaction commit or checkpoint.

##### Configuring Custom Events

#### Evaluation workflow

Evaluation is the process of executing the rules (evaluating expressions, calling event handlers) configured for an execution stage as part of a business operation.

Each existential transaction is associated with a business operation key, and through that with the closest matching context, its parent contexts,  
matching wildcard contexts, any configured Transaction factory and any passed-in Transaction instance. 
The rules configured in these contexts and transactions participate in evaluations.
The rules defined in parent contexts/transactions are considered to be defined 'earlier' than the rules in children contexts/transactions.
The order of execution of rules follows the order of their definition.
For each event, the order of invocations of its event handlers, if multiple handlers are defined, follows the order of their definition

+There is a separate evaluation per execution stage: Precondition, Immediate and Validation.
+Evaluation of Precondition stage rules is called Preconditions evaluation. 
+Evaluation of Immediate stage rules is called Immediate evaluation.
+Evaluation of Validation stage rules is called Validation evaluation.

Evaluations of separate transactions are independent, even if these transactions are nested.
Only the events reported during a transaction participate in its evaluations.

##### Preconditions evaluation

+User can assign any rule to Precondition stage.
+Preconditions evaluation becomes active (ready to accept trigger events) on transaction start, and lasts until commit/checkpoint/rollback.
+Precondition stage rules are evaluated once per trigger event, as soon as event has been received, at any time during transaction.

##### Immediate evaluation

+Immediate evaluation becomes active (ready to accept trigger events) on transaction start, and lasts until commit/checkpoint/rollback.
+Immediate stage rules are evaluated each time per trigger event, as soon as event has been received, at any time during transaction.
Since this may affect performance, using Immediate stage is considered an advanced technique. Normally, if you don't use
side effects, you should be fine with precondition and validation (default) stages.

##### Validation evaluation

+Validation evaluation is triggered on a commit or checkpoint of an existential transaction, ignored on rollback.
+The Effects are evaluated by applying event handlers for each event reported during a transaction to the corresponding entity.
+Validation stage rules are evaluated once per trigger event, from an in-memory index of such events (a runtime index),
that folds duplicate events of same type into single event.


## Concepts

### Configs

### Contexts

### Events

User can emit (record) a class event, such as accessing or modifying entity.
Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same effect) as a single such event.
User can't send any events before the library is configured.
User can't send any events before the transaction has started.
User can't send any events after transaction has been committed/rolled back.
User can't modify configurations (e.g. create invariants) after sending first event.
- Except for specifying custom Transaction object for transaction run
When emitting a port event without an explicit type key, library infers the type from the non-null entity
value (preferring t1 when present, otherwise t0). Both t0 and t1 can't be null.
When emitting a transit event without an explicit type key, both t0 and t1 must be non-null.

### Constraints.
(Below, the terms 'constraints' and 'invariants' are used interchangeably)

##### Constraint on a single entity

User can create a constraint on a field within an entity's class code.
User can create a constraint on a field outside of entity's class code.
User can create a constraint on a field of a third party class (e.g. a class which code is unavailable).
User can create a constraint on two fields of a class.
User can create a constraint on multiple fields of a class.
User can create a constraint on a collection field size.
User can create a constraint on a collection field contents.

##### Constraints on multiple entities

Create a constraint on two entities of same class within entity class code.
Create a constraint on two entities of different classes, outside entity class code.
Require another entity to exist when entity exists, (same class, within entity class code).
Require another entity to exist when entity exists, (different classes, outside entity class code).

##### Constraints on entity evolution

User can declare an invariant on entity transition.
User can declare an invariant on entity porting.

##### Constraints on evolution of multiple entities

User can pass additional values when emitting an event.

##### Constraints on subclasses

Constraints on parent class apply to any subclass. 

##### Constraint violation reporting

User should receive an exception in case of constraint violation, along with the details.
User can pass a human-readable description when creating a constraint, to help with reporting.
User can specify an error number when creating a constraint, to help with reporting.
Constraint violation exception contains details about the violation, such as human description of violated rule.
Optionally, user can configure the library to require a description for each constraint.

### Quantifiers

#### Universal Quantifier

User can create an invariant for an entity class using the 'All' quantifier.
User can restrict the 'All' quantifier to only apply to certain entities, by specifying a condition.
User can define the 'All' quantifier on an entity class.
User can define the 'All' quantifier on an entity transition class (Transition<T>).

#### Existence Quantifier

User can create an invariant on an entity class using the 'Exists' quantifier.
User can require entity existence when certain condition is met (same class, within entity class code).
User can require entity existence when certain condition is met (different classes, outside entity class code).
User can specify 'Exists' quantifier as a parameter to the 'All' quantifier, thus creating an All-Exists invariant.
User can define the 'Exists' quantifier on a collection.
User can define the 'Exists' quantifier on a map (index).

#### Indexes

User can use an index to speed up evaluation of Exists expression.
  We use SetIndex and JoinIndex classes for this
User can create an index to pass information between the rules.
  We use Index and JoinIndex classes for this 

### Effects

User can create a side effect for an entity by configuring an event handler for entity event.
Effects can be assigned to different stages of transaction lifecycle: Early, Middle and Late .
For early and middle stage effects, event handlers gets executed upon receiving a trigger event.
  - Handlers are executed immediately.
  - Handlers get invoked per each received trigger event.
Late stage handlers are executed at transaction commit or checkpoint, once per trigger event type.
  - For several trigger events of the same type, late stage event handler is executed only once.
The order of effect invocation follows the order of their declaration.
+User can define effects for combined events (CU, UD, CUD) emitted on create/update/delete transitions.

### Intents

+User can define intents on event type and entity class, similarly to invariants, such as 'read', 'write' intents.
+As soon as an intent defined for some event type, the system prevents the user from sending the events for any 
entity class that is not covered by a similar intent for same event type.  
  Example: As soon as an 'read' intent declared for entity class A, the system will:
  - require explicit 'read' intent declared for each other class that receives a 'read' event
  - if such sending occurs, throws an exception (IntentViolation)
+By default, the system validates intents immediately upon receiving an event.
+User can assign intents to execution stages (Precondition, Immediate, Validation), with Immediate as default stage.

#### Custom Event Types

User can define a custom event type.
User can define event handler for custom event type.
User can create a constraint based on custom event type (Context).
User can create a constraint based on custom event type (Transaction).
User can customize event splitter to emit events of custom event type.
User can send/record custom event from application code.
The system invokes custom event handler upon encountering the custom event.

### Housekeeping

#### Caching

Intermediate data structures are cached between transactions.
Intermediate data structures are cached between contexts.

#### Transaction Cleanup

Resources taken during a transaction are released upon its completion (commit or rollback).
The above applies to the transaction resources as well as all reported events.

#### Global Cleanup

System ensures that all transactions and their resources are recycled.
System ensures that rules, such as constraints, invariants, intents are recycled.
System ensures that any memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down).

### Maven 

User can add the Library as a dependency to their project using Maven-based dependency resolution.
  - Requirements: Maven Central account


### Implementation
(Technical user stories)

#### Public interface

##### Public classes
User can interact with the library as a whole using Ex, Existential classes.
User can configure rules per op using builders (ConfigBuilder, ContextBuilder, etc.).
User can specify configuration rules per context using Context, ContextBuilder.
User can specify configuration rules per business method invocation using Transaction, TransactionBuilder.
User can interact with the business transaction using Ex, Existential and Tr classes.
User can send messages to library using Ex, Existential and Tr classes.
+User can register memo state for a transaction entity so bi-event rules receive before-state snapshots.

##### Internal indexing
System automatically finalizes configuration for op upon start of a transaction.
System indexes configuration rules per op when configuration is finalized.
Configuration indexes are owned by Config object for business op.
System indexes the received events at runtime.
The runtime indexes are owned by Tr object.
Evaluation runs (e.g. at Validation state) use configuration and runtime indexes for performance.


### Appendix

### Out of scope / Not used
