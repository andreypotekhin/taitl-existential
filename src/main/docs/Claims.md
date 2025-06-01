## Claims / User Stories

Ths is the list of claims made by Existential library - 
the things that stated (claimed) in somewhere in the library documents.

As user stories, claim statements may be prefixed with 'The user can...' ('The user can't...' for negative claims).
Claims are baked by test cases in src/test/claims. The (+) signs below indicate the ones for which test cases exist. 

### Library

+User can access the library as an object from client code
User can independently configure and use multiple instances of Existential library
Within an instance of Existential library, the user can configure multiple operation contexts
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

#### Configuration workflow

User can configure the rules (constraints) with custom contexts
User can configure the rules (constraints) with custom transactions
Rules from parent context affect child contexts
Rules defined in parent context execute before the ones defined in child context
A transaction is associated with a context
User can't configure a context without defining any rules (constraints) in it
User can't configure a transaction without defining any rules  (constraints) in it
?User can specify a custom factory for creation of Contexts
?User can specify a one-time custom factory to produce a single instance of Context
?User can specify a custom factory for creation of Transactions
?User can specify a one-time custom factory to produce a single instance of Transaction
When configuring, user can call .context() and .transaction() in any order
User can specify a predicate, not only a handler, to process an event.
User can create a context with a wildcard in its op path
User can not create a transaction with a wildcard in its op path - only concrete op paths are allowed
All wildcard contexts whose path matches current op participate in its validation
User can define an op with a wildcard in its op path
All wildcard ops matching the current context participate in its validation

#### Execution workflow
(We call an 'exectuion' what happens between beginning and ending of a transaction)

##### Events

User can emit an event into the library, indicating entity access or modification
Successive events (for same entity and event type combination) are considered a single event
User can't send any events before the library is configured
User can't send any events before the transaction has begun
User can't send any events after transaction has been committed/rolled back

#### Validation workflow
(Validation is the process of checking the constraints)

Validation automatically starts upon transaction commit
Separate transactions are validated independently (even if transactions are nested)
Only the events reported during transaction are considered during its validation 
Validation is carried out by applying event handlers for each reported event to its entity
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
User can declare a combination of event type and entity class as protected, thus requiring intents to be declared 
System validates the intents at validation phase along with other constraints.

### Events

#### Custom Event

User can define custom event type
User can define an event handler for custom event type
User can declare a constraint based on custom event type (Context)
User can declare a constraint based on custom event type (Transaction)
User can customize event splitter to emit the custom event
System should invoke the custom event handler when validating custom event

### Housekeeping

#### Caching

Intermediate data structures are cached between transactions
Intermediate data structures are cached between contexts

#### Transaction Cleanup

Resources taken during transaction are released upon its completion
This applies to transaction resources, as well as reported events

#### Global Cleanup

System ensures that transactions are recycled
System ensures that rules, such as constraints, invariants, intents are recycled
System ensures that memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down)

### Maven 

User can add the library as a dependency to their project using Maven-based dependency resolution. 
Requirements: Maven Central account

### Appendix

### Out of scope / Not used

#### Indexing

User can create an in-transaction index to pass info between the rules
User can create an out-of-transaction index to pass information between the rules
User pass information between the rules using an in-transaction index
User pass information between the rules using an out-of-transaction index
User can use an index to speed up evaluation of Existence expression

#### Side Effects

User can automate side effects, such as setting a field to a default value on all objects (of same entity type)
that are loaded into transaction.
