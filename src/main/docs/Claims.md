## Claims

The following are claims made by Existential library.
Claims are things that one way or another stated (claimed) in library documents.

As user stories, claim statements may be prefixed with 'The user can...' ('The user can't...' for negative claims).
In the test code, the clams take the form of test cases.

### Claims / User Stories

#### Usage

+User can access the library from client code
+User can change library options programmatically
User can configure library options using a config file
User can configure library options using a classpath resource
User can specify the config file with an environment variable
+User can send events to library
+User can record access to an entity, e.g. the fact that entity was loaded (read)
+User can begin a transaction
+User can roll back a transaction
+User can commit a transaction
+User can't send events to library which hasn't been configured

#### Configuration

User must configure the library before use
User can configure the library with execution contexts
User can configure the library with custom transactions
User can affect library behavior by setting behavioral flags
User can't configure a context without defining any rules
User can't configure a transaction without defining any rules
User can specify a custom factory to produce Contexts
User can specify a one-time custom factory to produce a single instance of Context
User can specify a custom factory to produce Transactions
User can specify a one-time custom factory to produce a single instance of Transaction
When configuring, user can call .context() and .transaction() in any order
Order of execution of handlers follows the order of their definition during configuration
User can specify a predicate, not only a handler, to process an event.

#### Events

User can send events into the library
User can't send any events before the library is configured
User can't send any events outside the transaction
User can't send any events if transaction is committed or rolled back

#### Constraints

##### Entity Constraints

Create a constraint on a field within entity class code
Create a constraint on a field outside of entity class code
Create a constraint on a field of entity class I do not own (outside its code)
Create a constraint on two fields of a class
Create a constraint on multiple fields of a class
Create a constraint on a collection field of a class (e.g. collection size)
Create a constraint on a collection size (min / max)
Create a constraint on a collection contents

##### Constraint multiple entities

Create a constraint on two entities belonging to same class, within entity class code
Create a constraint on two entities belonging to different classes (outside entity class code)
Require another entity to exist when certain entity exists, same class, within entity class code
Require another entity to exist when certain entity exists, different classes, outside entity class code

##### Constraint entity evolution
Declare an invariant on entity mutation
Declare an invariant on entity transition

##### Constraint evolution of multiple entities

Pass additional values when creating/reporting an event
Pass human-readable description (string) when creating a rule, to help report violations
Pass error number when creating a rule, to help report a violations

#### Universal Qualifier

#### Existence

Require another entity to exist when certain entity exists, same class, within entity class code
Require another entity to exist when certain entity exists, different classes, outside entity class code

#### Indexing

User can create an in-transaction index to pass info between the rules
User can create an out-of-transaction index to pass information between the rules
User pass information between the rules using an in-transaction index
User pass information between the rules using an out-of-transaction index
User can use an index to speed up evaluation of Existence expression

#### Side Effects

User can automate side effects, such as setting a field to a default value on all objects (of same entity type)
that are loaded into transaction.

#### Customization
##### Custom Context class

##### Custom Transaction class

##### Custom Events
User can extend the set of events that comes with the library with more event types
User can define custom event type
User can register custom event type
User can define event handler type class for custom event type

User can customize event splitter to emit custom event

User can define event handler to contexts and transactions to process custom event

Custom Event Splitter

User can customize event splitter to emit custom event

Security

Intents

User can declare any event type 'protected', that is, requiring an 'allow' intent to be present in the configuration and checked at validation time.

Also for 'protected' event type, any existing 'deny' intent should be checked at validation time.

ACLs

Cleanup

Transactions are recycled

Rules are recycled

Contexts are cleaned upon transaction end

Leaks are reported

- log memory leaks, e.g. when the calling app shuts down

Cross-cut concepts

The order of execution of handlers is always same as the order of their definition.

Multiple instances of Existential library

User can independently configure and use multiple instances of Existential library

- Maven
    - User can add the library as a dependency to their project using Maven-based dependency resolution. Requirements: Maven Central account