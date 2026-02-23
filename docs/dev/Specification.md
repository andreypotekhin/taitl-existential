## Library Specification 

Below is the list of claims made by the Existential library - things that are stated (claimed) in library documents.
None of the claims are in any way legal. None of them constitute any contract.
They only describe library features and behavior for implementation.

As user stories, claim statements may be prefixed with 'The user can...' (or 'The user can't...').
Claims are backed by test cases in src/test/java/com/taitl/existential/specs. 
The (+) signs indicate items already completed and covered by tests.

### Terminology

Op: a business operation. Op name: the business operation identifier, such as "/api/user/update"   
Path: OS path-like notation for op names   
  - Abstract path can contain wildcards, such as "/api/*/update"
  - Concrete path doesn't contain wildcards
Evaluable: anything that can be evaluated  
Statement: an Evaluable that does not necessarily return a value  
Expression: an Evaluable that returns a value
Predicate: an Expression that returns a boolean value
Invariant: a list of Predicate expressions evaluated as constraints
Effect: a list of Statements executed for side effects
Intent: a list of Statements used to authorize or gate access to entities (e.g., loading from persistence)  
Rule: an umbrella term for invariants, effects and intents 
Event: an application or library event, such as: 
  - modifying an entity (e.g. Update<User>) 
  - accessing an entity (e.g. Read<User>)
  - starting or committing a transaction (e.g. Begin<Transaction>) and the like
  - Events are sent to the library by calling the event() method
  - Access events are sent to the library by calling read() and write()
  - Transaction events are automatically sent to the library when initiating or completing a transaction (begin(), commit(), rollback(), checkpoint() methods)
Runtime event: an event that is sent to library during execution of a transaction. Includes event type, entity type and reference to the affected entity.   
Context: a set of rules associated with a business operation 
  - A Context uses Op name to associate with an Op 
  - A Context can be defined with a wildcard Op name
  - Parent context is any Context whose name matches, without being equal to, the Context's name
  - Matching context is any wildcard context whose name matches, without being equal to, the Context's name
  - The rules from parent apply to child contexts
  - The rules from matching contexts apply to matched contexts
Transaction: a set of rules associated with a Context
  - Transactions are defined within a Context
  - Transactions are more dynamic than the Contexts, allowing use of local scope (method parameters,
  local variables) and members of the defining class (for anonymous nested classes) in event handlers' code
Library Transaction (Tr): a unit of execution in the library, associated with an Op name
  - Association with an Op name allows to select relevant Contexts and Transactions for evaluation
  - Transaction lifecycle is triggered by calling begin(), ending with commit(), rollback(), optional checkpoint()
  - For each relevant Context and Transaction, their configured rules are evaluated
  - The rules are evaluated at the end of transaction (commit or checkpoint), unless assigned to an earlier stage
  - In case of a constraint violation, an exception is raised and violations are reported
Quantifier: a logical expression such as All or Exists
Mutation: an event that records both before and after states of an entity 
Transition: a Mutation that can have a null in the before or after state (but not in both) 
  - The null in 'before' state indicates creation of an entity
  - The null in 'after' state indicates deletion of an entity
  - Both 'before' and 'after' states being non-null indicate a change (mutation) of an entity

For additional documentation, see /Readme.md, /docs and /docs/dev.

### Library

+User can access the library using static facade.
+User can access the library using a singleton.
User can independently configure and use multiple instances of the library.
Within an instance of Existential library, the user can configure multiple business operations.
Within a business operation configuration, the user can configure multiple operation contexts.
Within an operation context, the user can configure multiple rules such as invariants, effects and intents.

#### Library configuration.

+User can change library configuration options programmatically.
+User can affect library behavior by setting behavioral flags.
+User must configure the library before use.
+User can configure library options using a config file.
+User can configure library options using a classpath resource.
+User can specify the config file with an environment variable.
+Initial version of the config file is auto-created or otherwise available.

#### Library usage

+User can configure class rules.
User can configure access rules for a class.
User can configure transaction lifecycle rules for a class.
+User can start a transaction.
+User can commit a transaction.
+User can roll back a transaction.
+User can initiate transaction checkpoint.
+User can send events to record entity modification.
+User can send events to record entity access .
User can't send events outside a transaction .
+User can't send events if no rules have been configured.

#### Library lifecycle phases

The library usage falls into configuration, execution and validation phases.
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
Contexts are keyed by op name.
The rules from parent contexts apply to child contexts.
The rules from parent contexts execute before the rules of child contexts.
User can specify a custom factory for creation of all Contexts.
User can specify a custom factory for creation of a Context for a specific op.
User can't configure a context without defining any rules.

##### Wildcard contexts

User can define a Context for an op name with a wildcard in it.
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

##### Configuring Effects

User can specify side effect (event handler) for a class event.
User can specify side effect for transaction lifecycle event (begin, commit, rollback).

##### Configuring Intents

##### Configuration stages

User can assign rules to different stages of transaction lifecycle.
Configuration stages include: Precondition (Early), Runtime (Middle) and Validation (Late).
By default, the rules are assigned to Validation stage.
Precondition stage rules execute on transaction start.
Runtime stage rules execute within transaction upon encountering a trigger event.
Validation stage rules execute on transaction commit or checkpoint.

##### Configuring Custom Events

#### Evaluation workflow

Evaluation is the process of executing the rules (evaluating expressions, calling event handlers) configured for a business operation.
Evaluations start when an existential transaction begins and end when it ends.
There is a separate evaluation per stage: Precondition, Runtime and Validation.

Each existential transaction is associated with a business op name, and through that with the closest matching context, its parent contexts,  
matching wildcard contexts, any configured Transaction factory and any passed-in Transaction instance. The rules 
configured in these contexts and transactions participate in evaluations. 
Evaluation of Early stage rules is called Preconditions evaluation. It is invoked upon transaction start.
Evaluation of Middle stage rules is called Runtime evaluation. It is invoked for each trigger event.
Evaluation of Late stage rules is called Validation evaluation. It is invoked upon transaction commit or at checkpoint.

Evaluations of separate existential transactions are independent, even if these transactions are nested.
Only the events reported during a transaction participate in its evaluations.

Rules defined in parent contexts/transactions are considered to be defined 'earlier' than the rules in children contexts/transactions.
The order of execution of rules follows the order of their definition.
For each event, the order of invocations of its event handlers, if multiple handlers are defined, follows the order of their definition
Immediately evaluated rules are out-of-order. 

##### Preconditions evaluation

User can assign any rule to early stage.
Early stage rules are evaluated at the beginning of existential transaction.
Precondition expressions are evaluated at transaction start.
Precondition event handlers get invoked upon receiving a trigger event (any time during transaction).

##### Runtime evaluation

The rules assigned to middle stage are evaluated immediately upon receiving the corresponding trigger event.

##### Validation evaluation

Validation evaluation is triggered on a commit or checkpoint of an existential transaction.
Effects are evaluated by applying event handlers for each event reported during a transaction to the corresponding entity.
Intents are evaluated as lists of predicates. Violations are added to validation report.


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

### Constraints.
(Below, the terms 'contstraints' and 'invariants' are used interchangeably)

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

User can declare an invariant on entity mutation.
User can declare an invariant on entity transition.

##### Constraints on evolution of multiple entities

User can pass additional values when emitting an event.

##### Constraints on subclasses

Constraints on parent class apply to any subclass. 

##### Constraint violation reporting

User should receive an exception in case of constraint violation, along with the details.
User can pass a human-readable description when creating a constraint, to help with reporting.
User can specify an error number when creating a constraint, to help with reporting.
Constraint violation exception contains details about the violation, such as human description of violated rule.
Optionally, user can confgure the library to require a description for each constraint.

### Quantifiers

#### Universal Quantifier

User can create an invariant for an entity class using the 'All' quantifier .
User can restrict the 'All' quantifier to only apply to certain entities, by specifying a condition.
User can define the 'All' quantifier on an entity class.
User can define the 'All' quantifier on an entity mutation class (Mutation<T>).

#### Existence Quantifier

User can create an invariant on an entity class using the 'Exists' quantifier.
User can require entity existence when certain condition is met (same class, within entity class code).
User can require entity existence when certain condition is met (different classes, outside entity class code).
User can specify 'Exists' quantifier as a parameter to the 'All' quantifier, thus creating an All-Exists invariant.
User can define the 'Exists' quantifier on a collection.
User can define the 'Exists' quantifier on a stream.
User can specify a transaction object for the 'Exists' quantifier.

#### Indexes

User can use an index to speed up evaluation of Exists expression.
User can use an index to speed up evaluation of other expressions.
User can create an in-transaction index to pass information between the rules.
User can create an out-of-transaction index to pass information between the rules.

### Effects

User can create a side effect for an entity by configuring an event handler for entity event.
Effects can be assigned to different stages of transaction lifecycle: Early, Middle and Late .
For early and middle stage effects, event handlers gets executed upon receiving a trigger event.
  - Handlers are executed immediately.
  - Handlers get invoked per each received trigger event.
Late stage handlers are executed at transaction commit or checkpoint, once per trigger event type.
  - For several trigger events of the same type, late stage event handler is executed only once.
The order of effect invocation follows the order of their declaration.

### Intents

User can define 'allow' intent on event type and entity class.
User can define 'deny' intent on event type and entity class.
User can declare a combination of event type and entity class as 'protected', thus requiring explicit intents.
  - Marking as 'protected' results in requiring explicit intents.
  - Any action on the entity not covered by explicit intents is denied.
System validates the intents at validation phase along with other constraints.

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

##### Public classes.
User can interact with the library as a whole using Ex, Existential classes.
User can configure rules per op using builders (ConfigBuilder, ContextBuilder, etc.).
User can specify configuration rules per context using Context, ContextBuilder.
User can specify configuration rules per business method invocation using Transaction, TransactionBuilder.
User can interact with the business transaction using Ex, Existential and Tr classes.
User can send messages to library using Ex, Existential and Tr classes.

##### Internal indexing.
System automatically finalizes configuration for op upon start of a transaction.
System indexes configuration rules per op when configuration is finalized.
Configuration indexes are owned by Config object for business op.
System indexes the received events at run time.
Runtime indexes are owned by Tr object.
Evaluation runs (e.g. at Validation state) use config and runtime indexes for performance.


### Appendix

### Out of scope / Not used
