## Development

### Project overview
The Existential library aims for:
- Ability to express certain truths (constraints, invariants) about application classes and combinations of such. 
- Uphold these truths (rules) by automatically verifying them at certain points of execution.

To apply the rules, Existential library:
- Allows the user to 'send' events about an entity, e.g. 'Change\<MyEntity\>' upon changing the entity.
- Automatically validates rules that are applicable based on the encountered events.
- An event may concern entity's lifecycle (Create, Update, Delete), accessing the entity (Read, Write), 
transaction lifecycle (Begin, Commit, Rollback), allowing to 'attach' rules to specific points/circumstances.

To allow for performance, Existential library:
- Avoids immediate evaluation of the rules, evaluating them instead at the end of a 'business transaction', 
e.g. before committing the changed data to persistent storage.
- Multiple events of same type 'fold' into a single event, saving on the number of performed validations. 

To do so, Existential library allows end-user to:
1. Attach rules (constraints, invariants) to arbitrary classes (own or third-party).
2. Configure the rules to only be applicable within a certain business operation (e.g. an API endpoint or 
verb), allowing for different sets of rules to apply to different circumstances.

### Terminology, use cases, user stories
See Specification.md for terminology and detailed description of library behavior.

### Architecture

#### Limitations

Existential is an in-memory library, so will not run checks on what was not loaded into memory.

#### Performance
The library does not emphasize performance at the following areas, assuming they take
place outside of application's performance-critical paths, e.g. once per application run:
- Configuring the library itself
- Configuring the rules for each business operation ('per context')

The library emphasizes performance for the parts that run as part of a business transaction:
- Configuring the rules for a Transaction
- Sending events to the library
- Evaluating the rules (typically at transaction end)

### Source code

#### Code structure
Package structure:
- com.taitl.existential: public code (classes, interfaces) for use by end-user
- com.taitl.ex and subpackages: private code/implementation
    -  com.taitl.ex.common: common/ubiquitous classes (Creator, Args, State)
    -  com.taitl.ex.cross: cross-cut concepts (caching, logging)
    -  com.taitl.ex.concrete: concrete implementations (e.g. ConcreteExists) for the classes that end-user creates with 'new'
    -  com.taitl.ex.code: core classes, such as ExistentialConfigs, immediately used by public code
    -  com.taitl.ex.logic: business logic implementation
    -  com.taitl.ex.configuration: configuration logic (e.g. BuildContexts)
    -  com.taitl.ex.events: event processing logic (e.g. ReceiveEvent)
    -  com.taitl.ex.library: dealing with library as a whole
    -  com.taitl.ex.transactions: transaction logic (e.g. BeginTransaction, RollbackTransaction)
    -  com.taitl.ex.validation: validation logic (e.g. ValidateTransaction)

### Setup
See Setup.md for setup and prerequisites

### Building
Use regular Maven commands to build the project:

    mvn clean install # build from scratch
    mvn -T 2C install -am --offline # speed build (offline) 

### Troubleshooting
Refer to Troubleshooting.md doc.
If using custom classes, remove them and see if the library works without them.
