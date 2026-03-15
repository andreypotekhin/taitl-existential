## Development

### Project overview
The Existential library aims to:
- Express truths (constraints, invariants) about application classes and their relationships.
- Uphold these truths by automatically verifying them at specific points of execution.

To apply the rules, the library:
- Allows the user to send events about an entity, for example Update<MyEntity> when the entity changes.
- Automatically validates rules that are applicable based on the encountered events.
- An event may concern the entity lifecycle (Create, Update, Delete), access to the entity (Read, Write), or the
  transaction lifecycle (Begin, Commit, Rollback). This allows rules to be attached to specific points or
  circumstances.

For performance, the library:
- Avoids immediate rule evaluation and instead evaluates rules at the end of a business transaction, such as
  before committing the changed data to persistent storage.
- Multiple events of the same type are folded into a single event, reducing the number of validations performed.

To do so, the library allows end users to:
1. Attach rules (constraints, invariants) to arbitrary classes (own or third-party).
2. Configure the rules to apply only within a specific business operation (such as an API endpoint or verb),
   allowing different sets of rules to apply to different circumstances.

### User stories and use cases
See Specification.md for a detailed description of library behavior.

### Terminology
See Terminology.md for terminology.

### Architecture

#### Limitations

Existential is an in-memory library, so it does not run checks on data that was not loaded into memory.

#### Performance
The library does not emphasize performance in the following areas, assuming they take place outside of the
application's performance-critical paths, such as once per application run:
- Configuring the library itself
- Configuring the rules for each business operation ('per context')

The library emphasizes performance for the parts that run as part of a business transaction:
- Configuring the rules for a transaction
- Sending events to the library
- Evaluating the rules (typically at transaction end)

#### Benchmarking
Use the benchmark harness to anchor transaction-path performance:

    mvn -q -Pbenchmarks test
    mvn -q -Pbenchmarks -Dbenchmark.warmup.iterations=5000 -Dbenchmark.measure.iterations=50000 test

Benchmark classes live in `/src/test/java/com/taitl/existential/benchmarks`.

### Source code
Main doc: /docs/dev/Source.md

#### Code structure
Package structure:
- com.taitl.existential: public code (classes, interfaces) for use by end-user
- com.taitl.ex and subpackages: private code/implementation
  -  com.taitl.ex.common: common/ubiquitous classes (Creator, Args, State)
  -  com.taitl.ex.cross: cross-cut concepts (caching, logging)
  -  com.taitl.ex.concrete: concrete implementations (e.g. ConcreteExists) for the classes the end-user creates
     with 'new'
  -  com.taitl.ex.core: core classes, such as ExistentialConfigs, immediately used by public code
  -  com.taitl.ex.logic: business logic implementation
  -  com.taitl.ex.configuration: configuration logic (e.g. BuildContexts)
  -  com.taitl.ex.events: event processing logic (e.g. ReceiveEvent)
  -  com.taitl.ex.library: dealing with library as a whole
  -  com.taitl.ex.transactions: transaction logic (e.g. BeginTransaction, RollbackTransaction)
  -  com.taitl.ex.validation: validation logic (e.g. ValidateTransaction)

### Extending the library
Regular use of the library does not require custom classes; the stock classes should work for most cases.

For the rare cases where you want to significantly affect library behavior, we provide a few options.
See the section "Extending the library with custom classes" below.

### Setup
See /docs/dev/Setup.md for setup and prerequisites.

### Building
Use regular Maven commands to build the project:

    mvn clean install # build from scratch
    mvn -T 2C install -am --offline # speed build (offline)

### Troubleshooting
Refer to /docs/dev/Troubleshooting.md.
If using custom classes, remove them and verify whether the library works without them.
