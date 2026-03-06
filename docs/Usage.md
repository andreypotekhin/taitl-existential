# Existential Library Usage

## Overview
See /Readme.md for library overview.

## Getting started
This page is the quick-start. The formal overview is in /Readme.md.

### Adding the library to your project
If you build and install the library locally, use the Maven coordinates below.

Maven:

    <dependency>
        <groupId>com.taitl</groupId>
        <artifactId>existential</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

Gradle:

    dependencies {
        implementation "com.taitl:existential:0.0.1-SNAPSHOT"
    }

### Minimal workflow
Minimal workflow: configure rules, initiate transaction, send events. 

Configure rules:

    Ex.configure()
      .context("/app/orders/submit")
          .invariant(Order.class)
              .create(o -> o.total() > 0, "Total must be positive");

Create transaction, send events:

    Tr tr = Ex.begin("/app/orders/submit"); // <-- (1)
    try
    {
        // ...create Order...
        Ex.create(order, tr.id()); // <-- (2)
        // ...
        Ex.commit(tr); // <-- (3)
    }
    catch (ExistentialException e)
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

Constraints can apply to entity creation, deletion, modification, entity access (such as loading the entity from storage).
They can also involve multiple entities, such as all entities in a collection (All<> quantifier).

### Sending an event to the library
In order for the library to be able to evaluate constraints, it needs to be aware of the entity changes. 
This is done by sending an event to the library.

    Tr tr = Ex.begin("/api/accounts/update");
    try
    {
        // ...update Account...
        Ex.update(account, tr.id());
        Ex.commit(tr);
    }
    catch (ExistentialException e)
    {
        Ex.rollback(tr);
        throw e;
    }

### Constraint validation
Constraint validation is triggered automatically upon committing a transaction.

    Ex.commit(tr); // Detect and report constraint violations.

Constraint violations will be reported as part of thrown exception (ExistentialException).


## Configuration

### Library configuration
Library startup loads options in this order:
- Classpath resource `existential.properties` (default values).
- If env var `EXISTENTIAL_CONFIG_FILE` is set, load that file next (override defaults).

Workflow:
1. Create a properties file:
  - File: existential.properties
  - Example property: `behavior.rules.requireDescriptions=true`
2. Set EXISTENTIAL_CONFIG_FILE env variable to point to properties file:
  - `export EXISTENTIAL_CONFIG_FILE=/path/to/existential.properties`


## Troubleshooting
Troubleshooting:
- See `/Troubleshooting.md`


## Advanced Topics

### Terminology
See Concepts.md for concepts and terminology.
See /docs/dev/Terminology.md for developer terminology.

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
/docs/dev/Development.md is starting point.

