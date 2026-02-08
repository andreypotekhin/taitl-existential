## Claims / User Stories

This is the list of claims made by Existential library - 
things that are stated (claimed) in library documents.

As user stories, claim statements may be prefixed with 'The user can...' ('The user can't...' for negative claims).
Claims are baked by test cases in src/test/claims. The (+) signs below indicate the ones covered by tests. 

### Library

+User can access the library as an object from client code
User can independently configure and use multiple instances of Existential library
Within an instance of Existential library, the user can configure multiple operations
Within an operation configuration, the user can configure multiple operation contexts
Within an operation context, the user can configure multiple transactions

#### Library configuration

+User can change library configuration and options programmatically
User can affect library behavior by setting behavioral flags
User can configure library options using a config file
User can configure library options using a classpath resource
User can specify the config file with an environment variable
The initial version of the config file is auto-created or otherwise available

#### Library usage

User can configure constraints on classes, such as application entities
+User can send events to the library for the purpose of recording entity access and modifications 
+User can record access to an entity, such the fact that entity was loaded (read) from storage, modified, or saved
+User can record entity modifications, such the fact that entity was created or changed 
+User can begin a transaction
+User can commit a transaction
+User can roll back a transaction
+User can't send events to library which hasn't been configured

#### Lifecycle phases
The library usage falls into configuration, execution and validation phases
During configuration phase, user defines rules (constraints, intents) for contexts and transactions
During execution phase, user begins and ends transactions, and sends events to the library
During validation phase, the library checks the constraints
The validation phase happens at transaction end (commit) or at checkpoints

#### Configuration workflow

##### Configuring contexts

User can configure the rules (constraints) with custom context
Rules from parent context apply to child contexts
Rules from parent context execute before the rules from child context
User can specify a custom factory for creation of all Contexts
User can specify a custom factory for creation a specific Context
User can't configure a context without defining any rules

##### Wildcard contexts

User can define an op with a wildcard in its op path
User can create a context with a wildcard in its op path
All wildcard contexts whose path matches current op participate in its validation
All wildcard ops matching the current context participate in its validation

##### Custom transactions

User can not create a transaction with a wildcard in its op path - only concrete op paths are allowed
User can configure the rules (constraints) with custom transaction factory per context
User can configure the rules (constraints) with custom transaction object per transaction run
User can't configure a transaction without defining any rules
User can specify a custom factory for creation of transactions in a context
User can specify a Transaction instance to use in a transaction

##### Configuring rules and constraints

User can specify a predicate for an event.
User can specify a handler (side effect) for an event.

#### Execution workflow
(We call an 'execution' what happens between beginning and end of a transaction)

Execution transaction is associated with a context, its parent contexts, and any configured transaction factories/instances.
Pre-conditions are checked at the beginning of transaction.
Intents are checked immediately (in the middle of transaction).
Constraints are checked at the end of transaction.
Constraints are also checked at checkpoints.

##### Events

User can emit/record an event, such as entity access or modification
Multiple equal events (e.g. same entity + event type) (successive or not) are considered to be (have same effect) as a single such event
User can't send any events before the library is configured
User can't send any events before the transaction has started
User can't send any events after transaction has been committed/rolled back
User can't modify configurations (e.g. create invariants) after sending first event
  - Except for specifying custom Transaction object for transaction run.

#### Validation workflow
(Validation is the process of checking the constraints at transaction commit or checkpoint)

Validation automatically starts upon transaction commit or checkpoint
Separate transactions are validated independently (even if these transactions are nested)
Only the events reported during transaction are considered for validation 
The validation is carried out by applying event handlers for each reported event to its corresponding entity
The order of execution of event handlers follows the order of their definition during configuration phase

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

### Intents

User can define 'allow' intent on event type and entity class
User can define 'deny' intent on event type and entity class
User can declare a combination of event type and entity class as 'protected', thus requiring intents to be declared 
System validates the intents at validation phase along with other constraints.

### Events

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

### Appendix

### Out of scope / Not used

#### Indexing

User can create in-transaction index to pass info between the rules
User can create out-of-transaction index to pass information between the rules
User can pass information between the rules using an in-transaction index
User can pass information between the rules using an out-of-transaction index
User can use an index to speed up evaluation of Existence expression

#### Side Effects

User can automate side effects, such as setting a field to a default value on all objects of certain type
that participate in a transaction.
