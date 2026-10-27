# Existential Library Usage

## Overview
See [Readme.md](../Readme.md) for library overview.

## Getting started
This page is the quick-start. The formal overview is in [Readme.md](../Readme.md).

### Adding the library to your project
If you build and install the library locally, use the Maven coordinates below.

Maven:

    <dependency>
        <groupId>com.taitl</groupId>
        <artifactId>existential</artifactId>
        <version>0.0.1</version>
    </dependency>

Gradle:

    dependencies {
        implementation "com.taitl:existential:0.0.1"
    }

Night City is an independent consumer example. From a checkout, install the library and run its acceptance build and
interactive-free workflow:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify
    mvn -f examples/night_city/pom.xml exec:java

The example POM depends on the installed library artifact; it does not compile against the root project output. See
[Night City](../examples/night_city/Readme.md) for the scenarios and combined reactor command.

### Minimal workflow
Minimal workflow: configure rules, initiate transaction, send events. 

Configure rules:

    Ex.configure()
      .context("/app/orders/submit")
          .invariant(Order.class)
              .create(o -> o.total() > 0, "Total must be positive");// <-- (1)

    (1) The rule will apply to any Order created as part of a transaction    

Create transaction, send events:

    Tr tr = Ex.begin("/app/orders/submit"); // <-- (1)
    try
    {
        // ...create Order...
        Ex.create(order, tr.id()); // <-- (2)
        // ...
        Ex.commit(tr); // <-- (3)
    }
    catch (Exception e)
    {
        Ex.rollback(tr); // <-- (4)
        throw e;
    }

    (1) Start a transaction to collect application events and apply the configured rules   
    (2) Notify the library about entity creation by sending 'Create' event  
    (3) Evaluate the rules that match received events, throw on violations
    (4) No rule evaluation takes place in case of a rollback

Notes:
- Configuration code may live in a separate place from transaction code, e.g. at application startup.
- After `commit()` or `rollback()`, transaction object becomes unusable.

### Creating a constraint on an entity
Constraints are created in the context of a business operation, 
such as "creating an order" or "updating an account".

    Ex.configure()
      .context("/api/accounts/update")
          .invariant(Account.class)
              .create(a -> validEmail(a.emailAddress()), "Ensure valid email address")
              .update(a -> !a.locked(), "Can't update a locked account")
              .delete(a -> !balancePresent(a), "Can't delete an account with a balance");

Constraints can apply to entity creation, deletion, modification, entity access (such as loading the entity from
storage).
They can also involve multiple entities, such as all entities in a collection (All<> quantifier).

### Sending an event to the library
In order for the library to be able to evaluate constraints, it needs to be aware of the entity changes. 
This is done by sending an event to the library.

    Tr tr = Ex.begin("/api/accounts/update");
    try
    {
        // ...update Account...
        Ex.update(account, tr.id()); // <-- (1)
        Ex.commit(tr);
    }
    catch (Exception e)
    {
        Ex.rollback(tr);
        throw e;
    }

    (1) Send an Update<Account> event to the library    

With AspectJ weaving, `@Op` owns a fresh handle for each annotated invocation, commits on normal return, and attempts
explicit rollback on method/commit failure. Event reporting still requires an explicit active handle or string ID;
use the manual pattern above when passing that ID through nested application helpers.

### Constraint validation
Constraint validation is triggered automatically upon committing a transaction:

    Ex.commit(tr); // Detect and report constraint violations.

Constraint violations will be reported as part of thrown exception (ExistentialException).
`ExistentialException` is unchecked, so methods that call Existential do not need to declare it in a `throws` clause.
Catch it at a boundary where the application can recover or translate the failure.


### Checkpoints, authorization, and effects

Call `tr.checkpoint()` to validate without completing the transaction. Success keeps begin/immediate processing active;
begin rules still run only once per event/type key. Current entity values are checked again at later boundaries.
Failure makes the handle rollback-only, as does failed commit: further reports, checkpoints, commits, and managed-index
queries are rejected. The active string-ID entry is removed. Use explicit rollback for its callbacks, or close for
resource release, then start a fresh transaction. Neither cleanup path undoes caller-owned objects or prior effects.

Every applicable intent must pass across matching contexts, types, and runtime transaction rules. A general grant
cannot bypass a narrower restriction. Write alternatives explicitly, for example `entity -> owns(entity) || isAdmin()`.

Checks and effects retain declaration order. Checks-only validation collects ordinary failures into its report. If
an effect participates, the first ordinary violation stops later checks/effects and later entities. Conditional effects
count; effects in another stage do not. Unexpected failures and denials always stop immediately. Inspect the existing
exception cause and `multiple()` results; a stopped evaluation does not mean every rule ran.

### Required rule descriptions

Enable `Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS` on the instance, or set
`behavior.rules.requireDescriptions=true` in the usual configuration files, to require non-empty rule descriptions.
The fluent context/transaction builders apply it to checks, intents, effects, All, and Exists, including managed
sources.
A rejected declaration adds no rule: supply its description and retry on the same builder.

The flag applies when each rule is added, including through an already obtained builder. Earlier optional declarations
remain valid. Disable it before adding a subsequent undescribed rule if appropriate; changes freeze with configuration.
Standalone rule sets expose `requireDescriptions(boolean)` for their subsequent additions. There is no implicit
instance binding or retroactive scan of supplied runtime definitions.

### Reporting access

Use `tr.read(cat)` or `tr.write(cat)` to report loaded or saved objects. For explicit read-and-lock or general access,
use the existing typed event API:

    TypeKey<Cat> cats = new TypeKey<>(Cat.class);
    tr.event(new RL<>(cat), cat, cats);
    tr.event(new Access<>(cat), cat, cats);

Use the TypeKey naming appropriate to your configured mode. Equivalent event methods on `ex` accept the transaction ID
for nested application calls. Reporting RL also selects read handlers; Write does not invent create/update events,
and Access does not expand into storage reads, locks, or writes. The application performs the actual storage/locking.
General On rules apply alongside the selected event-specific rules, in resolved declaration order. All matching intents
must pass, including general, read, lock, and transaction-local restrictions. Repeated reports still run immediate
checks, while boundary work folds matching event/type/object identities and successful checkpoints preserve history.

### Managed indexes

A definition is reusable configuration metadata. Its bound contents belong to one active Tr. For example, this groups
reported strings by length and checks their enrolled membership:

    IndexDefinition<MultiIndex<Integer, String>> words =
            IndexDefinition.multi("words", String.class, value -> value.length());
    try (Existential ex = new Existential())
    {
        ex.configure().context("/words/save").index(words)
                .invariant(String.class)
                    .all(words, (value, index) -> index.contains(value), "Value belongs to the working set");
        Tr tr = ex.begin("/words/save");
        try
        {
            tr.index(words).addAll(List.of("seed"));
            tr.create("new");
            Set<String> lengthThree = tr.index(words).get(3);
            tr.checkpoint();
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }

Use imports from `com.taitl.existential`, `com.taitl.existential.indexes`,
`com.taitl.existential.transactions`, and `java.util`. Class overloads suit non-generic subjects; TypeKey overloads
retain generic arguments. Register the same definition token used for `tr.index`; names must be unique among distinct
effective definitions. Existing standalone indexes and `Transaction.index(name, extractor)` remain manual APIs.

Factories `single`, `multi`, `singleJoin`, and `multiJoin` return the corresponding existing facade shapes. A managed
Exists can project a join direction without streams:

    ex.configure().context(op).index(homes).invariant(Cat.class)
            .exists(homes, join -> join.left(), "Every reported cat has a dwelling");

The [Night City configuration][managed-config] supplies the complete generic declaration and shared runnable operations.
Reported reads, read-and-lock, and general access enroll loaded objects by default; Write and concrete mutation events
maintain them. Explicit combined-event markers do not guess whether a deletion occurred: report Create/Update/Delete
or explicit before/after endpoints. Both matching sides of a join are maintained once each. Seeding with addAll or
addAllLeft/addAllRight creates no synthetic events. Earlier report-time rules see accepted prior reports and seeds;
the candidate event is enrolled after those rules and authorization pass.

Membership uses object identity, so equal-but-distinct objects remain independent in multi-valued indexes. Report
relevant mutations; keep extracted key objects stable and extractors free of side effects/reentrant index access.
Single-valued indexes replace the previous value at a key. Removing or moving that replacement does not restore an old
snapshot. Explicit transitions retire the before object and enroll the after object. Index retirement does not erase
previous validation event records: a class-wide Exists rule still checks its reported subjects. Seed a loaded prior
snapshot when only its replacement should be reported, or configure a predicate with the intended source scope.

Empty holders may be constructed at begin. Multi-valued enrollment folds repeated reports before key extraction and
group maintenance; single-valued bookkeeping captures keys when necessary to preserve destructive replacement.
Queries flush pending changes, with only changed members updated in large groups. Join views avoid storing every
pair of matching objects. Returned map/set query views protect their structure; use explicit facade methods to mutate.
Do not capture an active handle or bound view in shared rule configuration. Managed All/Exists resolve it per
evaluation.

Both automatic maintenance and read enrollment default to true. Supply partial overrides with
`IndexingPolicy.inherit()`:

    ex.indexingPolicy(IndexingPolicy.inherit().enrollReads(false));
    ex.configure().context(op).indexingPolicy(IndexingPolicy.inherit().automatic(false));
    customTransaction.indexingPolicy(IndexingPolicy.inherit().automatic(true));
    IndexDefinition<MultiIndex<Integer, String>> manual = words.withPolicy(
            IndexingPolicy.inherit().automatic(false));

Register and access the returned `manual` token when using withPolicy. Resolve each property independently, from index
overrides to transaction overrides, then contexts in reverse resolved order, then app/instance defaults. `Ex` changes
its facade instance. Global classpath/environment-selected files accept `indexing.automatic` and `indexing.enrollReads`;
normal source precedence and atomic validation apply. Settings bind before processing and freeze with their scopes.
Manual mode bypasses automatic event work for that index; seeds and explicit add/remove/reindex remain available.

Dispose every handle. Success, rollback, and close clear transaction-owned contents and invalidate bound views. Failed
maintenance can make the handle rollback-only, with extractor errors appearing at event capture or deferred access,
according to the index shape. Caller-owned seed collections and entities are never cleared. See
[managed-index troubleshooting](../Troubleshooting.md#managed-index-maintenance).

[managed-config]: ../examples/night_city/src/main/java/com/taitl/ex/examples/night_city/configure/ConfigureManagedRelationships.java

## Configuration

### Library configuration
Library startup loads options in this order:
- Classpath resource `existential.properties` (default values).
- If env var `EXISTENTIAL_CONFIG_FILE` is set, load that file next (override defaults).
- Programmatic flag changes made during setup override file values.

Each `Existential` instance loads its own settings. Flag changes affect subsequent configuration or event work and do
not redo work already completed. Flags and rule declarations become fixed after the first successful `begin()`; finish
configuration first, then safely publish the instance to request threads. Use ordinary synchronization, such as a
latch, future, or synchronized initialization method, when another thread may encounter the instance during startup.

Workflow:
1. Create a properties file:
  - File: existential.properties
  - Example property: `behavior.rules.requireDescriptions=true`
2. Set EXISTENTIAL_CONFIG_FILE env variable to point to properties file:
  - `export EXISTENTIAL_CONFIG_FILE=/path/to/existential.properties`


## Troubleshooting
Troubleshooting:
- See [Troubleshooting.md](../Troubleshooting.md).


## Advanced Topics

### Terminology
See [Concepts.md](Concepts.md) for concepts and terminology.
See [Terminology.md](dev/Terminology.md) for developer terminology.

### Customizing the library
Regular use of the library does not require custom classes; the stock classes should work for most cases.

For the rare cases where you want to significantly affect library behavior, we provide a few options.
See the section "Extending the library with custom classes" below.

#### Code structure
Package structure:
- com.taitl.existential: public code (classes, interfaces) for use by end-user
- com.taitl.ex and subpackages: private code, implementation details
  -  com.taitl.ex.common: common/ubiquitous classes (Creator, Args, State)
  -  com.taitl.ex.concrete: concrete implementations (e.g. ConcreteExists) for the classes that end-user creates
     with 'new'
  -  com.taitl.ex.core: core classes, such as ExistentialConfigs, immediately used by the public code
  -  com.taitl.ex.cross: cross-cut concepts (caching, logging)
  -  com.taitl.ex.logic: business logic implementation
    -  com.taitl.ex.configuration: configuration logic (e.g. BuildContexts)
    -  com.taitl.ex.events: event processing logic (e.g. ReceiveEvent)
    -  com.taitl.ex.library: dealing with library as a whole
    -  com.taitl.ex.transactions: transaction logic (e.g. BeginTransaction, RollbackTransaction)
    -  com.taitl.ex.validation: validation logic (e.g. ValidateTransaction)

#### Extending the library with custom classes
For the rare cases where you aim to significantly affect the library's behavior (and cannot do it by other
means), you can extend the library with your own versions of the classes, but do so at your own risk.

This is worth repeating:
1. Do this at your own risk.
2. Do not ask for help or file issues if your case involves custom classes.

For troubleshooting, remove custom classes and see if the library works without them.

Here are some ways to put custom classes in use:
- By using a `*Factory()` method on an existing class (e.g. Context.transactionFactory()).
- By calling Creator.inject(). This creates a global default factory for the class.
- If using Creator.inject(), call it early in the application lifecycle to avoid creating instances before
  injection.
- By modifying the FACTORY field on an existing library class (e.g. Transaction.FACTORY).
- For the classes the end user creates with new (e.g. Exists, Invariant), subclass the concrete implementations
  (e.g. ConcreteExists, ConcreteInvariant).

To keep extension possible, we allow a few freedoms:
- No emphasis on final or sealed classes.
- FACTORY fields are not declared final.
- Creator.inject() is declared public.

But remember, with this freedom comes responsibility:
- If using custom classes, you are on your own.
- Future versions of the library can completely rewrite its implementation, including any and all non-public
  classes.

In short, treat extending the library with your own classes as hacking, which relies on undocumented
features or features that are not guaranteed to survive multiple versions.


### Building the library
1. Clone source code repository
   https://github.com/andreypotekhin/taitl-existential
2. Build with maven:
   `mvn clean install`


### Development
See /docs/dev/ for development documentation.  
[Development.md](dev/Development.md) is the starting point.

## Structured rule diagnostics

Existing descriptions identify rules in both readable exceptions and structured diagnostics:

```java
ex.configure().context("/cats").invariant(new Invariant<>(Cat.class)
        .on(cat -> "Black".equals(cat.color), "Only black cats may enter"));
Tr tr = ex.begin("/cats/save");
try {
    tr.read(cat);
    tr.commit();
} catch (ValidationStageExceptions failure) {
    System.err.println(failure.getMessage());
    Diagnostic first = failure.diagnostic().children().get(0);
    String description = first.rule();
    String declaration = first.context();
    DiagnosticCode category = first.code();
} finally {
    tr.close();
}
```

Import diagnostics from `com.taitl.existential.diagnostics`. `getMessage()` renders detailed plain text lazily;
`diagnostic().message()` is the concise category message. Optional fields are null and lists are immutable snapshots.
Descriptions remain unchanged in raw fields; rendering escapes control characters. Parent/wildcard declaration
contexts remain distinct from the invocation operation. Transaction-local rules have `transactionLocal() == true`
and null context. Reported event/type can differ from the selected rule event/type; stage and boundary are separate.

Aggregates keep the existing `multiple()`, cause, and suppressed failures. Children retain full fields even when
common transaction context is printed once. `evaluationStopped()` records known early termination; false does not
promise exhaustive validation. Snapshots remain usable after rollback/close and retain no entities or handles.

Unexpected callback RuntimeException is wrapped in EventHandlerException, or IntentViolation for an intent callback,
with code CALLBACK-FAILED and the original exception in its cause chain. Caller-supplied exception objects are not
mutated. Error propagates unchanged. Undecorated caller-created exceptions keep their original message/cause behavior.
See [diagnostic categories and remedies](../Troubleshooting.md#diagnostic-evaluation-failed).
