## Claims / User Stories

This is the list of claims made by Existential library - things that are stated (claimed) in library documents.
As user stories, the claim statements may be prefixed with 'The user can...' (or 'The user can't...').
The claims are backed by test cases in src/test/claims. The (+) signs indicate the ones already covered by tests. 

### Terminology

Op: a business operation. Op name: business operation name, such as "/api/user/update" 
Path: OS path-like notation for Op names 
  - Abstract path can contain wildcards, such as "/api/*/update"
  - Concrete path doesn't contain wildcards
Evaluable: anything that can be evaluated
Expression: an Evaluable that evaluates to a value
Predicate: an Expression that evaluates to a boolean
Invariant: a list of Predicate expressions
Statement: an Evaluable that does not necessarily return a value
Effect: a list of Statements
Intent: a list of Statements for controlling access to entities, e.g. loading an entity from persistent store  
Rule: a general term for invariants, effects and intents
Event: an application or library event, such as: 
  - modifying an entity (e.g. Update<User>) 
  - accessing an entity (e.g. Read<User>)
  - starting or committing a transaction (e.g. Begin<Transaction>) and the like
  - Events are sent to the library by calling event() method
  - Access events are sent to the library by calling read() and write()
  - Transaction events are automatically sent to the library when initiating or completing a transaction (begin(), commit(), rollback(), checkpoint() methods)
Context: a set of rules (constraints, effects, intents) associated with a business operation 
  - A Context uses Op name to associate with an Op 
  - A Context can be defined with a wildcard Op name
  - Parent context is any Context whose name matches, without being equal to, Context's name
  - Matching context is any wildard context whose name matches, without being equal to, Context's name
  - The rules from parent apply to child contexts
  - The rules from matching contexts apply to matched contexts
Transaction: a set of rules (constraints, effects, intents) associated with a Context
  Transactions are more dynamic than the Contexts, allowing to use local scope (method parameters,
  local variables) and members of the defining class (for anonimous nested classes) in event handlers' code
Library Transaction: a unit of execution in the library, associated with an Op name
  - Association with an Op name allows to select relevant Contexts and Transactions for evaluation
  - Transaction lifecycle is triggered by calling begin(), ending with commit(), rollback(), optional checkpoint()
  - For each relevant Context and Transactions, their configured rules are evaluated
  - The rules are evaluated at the end of transaction (commit or checkpoint), unless assigned to an earlier stage
  - In case of a constraint violation, an exception is raised and constraint violations are reported
Quantifier: a logical expression, such as All or Exists
Mutation: an event that records both before- and after- states of an entity 
Transition: a Mutation that can have a null in before- or after- state (but not in both) 
  - The null in 'before' state indicates creation of an entity
  - The null in 'after' state indicates deletion of an entity
  - Both 'before' and 'after' states being non-null indicates a change (mutation) of an entity

### Library

+User can access the library using static facade
+User can access the library using a singleton
User can independently configure and use multiple instances of the library
Within an instance of Existential library, the user can configure multiple business operations
Within a business operation configuration, the user can configure multiple operation contexts
Within an operation context, the user can configure multiple rules such as invariants, effects and intents

#### Library configuration

+User can change library configuration options programmatically
+User can affect library behavior by setting behavioral flags
+User must configure the library before use
User can configure library options using a config file
User can configure library options using a classpath resource
User can specify the config file with an environment variable
Initial version of the config file is auto-created or otherwise available

#### Library usage

+User can configure class rules
User can configure access rules for a class
User can configure transaction lifecycle rules for a class
+User can start a transaction
+User can commit a transaction
+User can roll back a transaction
+User can initiate transaction checkpont
+User can send events to record entity modification
+User can send events to record entity access 
User can't send events outside a transaction 
+User can't send events if no rules has been configured

#### Library lifecycle phases
The library usage falls into configuration, execution and validation phases
In configuration phase, the user defines rules - constraints, effects - using contexts and transactions
In execution phase, user begins and ends transactions and sends events to the library
In execution phase, the library executes execution time side effects 
The validation phase triggers upon transaction commit, or at a checkpoint
In validation phase, the library evaluates all applicable constraints and reports violations
In validation phase, the library executes validation time side effects 

### Workflows

#### Configuration workflow

##### Configuring contexts

User can configure the rules (constraints) on a class with a custom Context
Contexts are keyed by op name 
The rules from parent contexts apply to child context
The rules from parent contexts execute before the rules of child contexts
User can specify a custom factory for creation of all Contexts
User can specify a custom factory for creation a Context for specific op
User can't configure a context without defining any rules

##### Wildcard contexts

User can define a Context for an op name with a wildcard in it
The wildcard contexts whose path match a concrete op participate in its validation
The wildcard contexts matching the currently evaluated context participate in its validation
The rules from matching contexts apply to context
The rules from matching contexts execute before the rules of context

##### Configuring transactions

User can configure invariants, effects and intents on a Transaction object
User can configure the rules (constraints) with custom transaction object per transaction run
User can specify a custom factory for creation of transactions in a Context
Transaction factory from parent Context is used for child Contexts, unless overridden in the child Context

##### Custom transaction

User can specify an instance of Transaction class to use in a library transaction
  Rationale: be able to configure the rules based on dynamic information, such as web request parameters  
  Code: This is done by specifying Transaction instance as parameter to Ex.begin() method

##### Configuring Constraints

User can specify an invariant (predicate) for a class event
User can specify an invariant (predicate) for transaction lifecycle event (begin, commit, rollback)

##### Configuring Effects

User can specify side effect (event handler) for a class event
User can specify side effect for transaction lifecycle event (begin, commit, rollback)

##### Configuring Intents

##### Configuring Custom Events

#### Execution workflow

Execution phase starts upon beginning and ends upon end of a library transaction
The execution transaction is associated with a context, its parent contexts, and any configured Transaction factories and instances
The pre-conditions are checked at the beginning of transaction
Execution-time Effects are executed immediately upon trigger event
Execution-time Constraints are checked immediately upon trigger event
Execution-time Intents are checked immediately upon trigger event

#### Validation workflow

Validation phase starts upon transaction commit or checkpoint
Separate transactions are validated independently (even if these transactions are nested)
Only the events reported during transaction are considered for validation for that transaction
The order of execution of Effects, Constraints and Intents follows the order of their definition
Validation-time Effects are executed by applying event handlers for each reported event to its corresponding entity
Validation-time Constraints are evaluated and violations added to validation report
Validation-time Intents are checked similarly to the constraints

### Contexts

### Events

User can emit (record) a class event, such as accessing or modifying entity
Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same effect) as a single such event
User can't send any events before the library is configured
User can't send any events before the transaction has started
User can't send any events after transaction has been committed/rolled back
User can't modify configurations (e.g. create invariants) after sending first event
- Except for specifying custom Transaction object for transaction run

### Constraints

##### Constraint single entity

User can create a constraint on a field within an entity's class code
User can create a constraint on a field outside of entity's class code
User can create a constraint on a field of a third party class (e.g. a class which code is unavailable)
User can create a constraint on two fields of a class
User can create a constraint on multiple fields of a class
User can create a constraint on a collection field size
User can create a constraint on a collection field contents

##### Constraint multiple entities

Create a constraint on two entities of same class within entity class code
Create a constraint on two entities of different classes, outside entity class code
Require another entity to exist when entity exists, (same class, within entity class code)
Require another entity to exist when entity exists, (different classes, outside entity class code)

##### Constraint entity evolution

User can declare an invariant on entity mutation
User can declare an invariant on entity transition

##### Constraint evolution of multiple entities

User can pass additional values when emitting an event

##### Constraint subclasses

Constraints on parent class apply to any subclass. 

##### Constraint violation reporting

User should receive an exception in the case of constraint violation, along with the details
User can pass a human-readable description when creating a constraint, to help with reporting
User can specify an error number when creating a constraint, to help with reporting
Constraint violation exception contains details such as custom description
Optionally, user can confgure the library to require a description for each constraint

### Qualifiers

#### Universal Qualifier

User can create an invariant for an entity class using the 'All' quantifier 
User can restrict the 'All' quantifier to only apply to certain entities, by specifying a condition
User can define the 'All' quantifier on an entity class
User can define the 'All' quantifier on an entity mutation class (Mutation<T>)

#### Existence Qualifier

User can create an invariant on an entity class using the 'Exist' quantifier
User can require entity existence when certain condition is met (same class, within entity class code)
User can require entity existence when certain condition is met (different classes, outside entity class code)
User can specify 'Exists' quantifier as a parameter to the 'All' quantifier, thus creating All-Exists invariant
User can define the 'Exist' quantifier on a collection
User can define the 'Exist' quantifier on a stream
User can specify a transaction object for the 'Exist' quantifier

### Effects

### Intents

User can define 'allow' intent on event type and entity class
User can define 'deny' intent on event type and entity class
User can declare a combination of event type and entity class as 'protected', thus requiring intents to be declared 
System validates the intents at validation phase along with other constraints.

#### Custom Event Types

User can define a custom event type
User can define event handler for custom event type
User can create a constraint based on custom event type (Context)
User can create a constraint based on custom event type (Transaction)
User can customize event splitter to emit events of custom event type
User can send/record custom event from application code
The system invokes custom event handler upon encountering the custom event

### Housekeeping

#### Caching

Intermediate data structures are cached between transactions
Intermediate data structures are cached between contexts

#### Transaction Cleanup

Resources taken during transaction are released upon its completion (commit or rollback)
The above applies to transaction resources as well as reported events

#### Global Cleanup

System ensures that all transactions and their resources are recycled
System ensures that rules, such as constraints, invariants, intents are recycled
System ensures that any memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down)

### Maven 

User can add the Library as a dependency to their project using Maven-based dependency resolution. 
Requirements: Maven Central account

#### Indexing

User can create in-transaction index to pass info between the rules
User can create out-of-transaction index to pass information between the rules
User can use an index to speed up evaluation of Existence expression

#### Side Effects

User can automate side effects, such as setting a field to a default value on all objects of certain type
that participate in a transaction.

### Appendix

### Out of scope / Not used

