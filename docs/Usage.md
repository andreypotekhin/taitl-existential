## Existential Library Usage

### Overview
See /Readme.md for library overview.

### Using the library
See /Readme.md for general usage.

### Library configuration
Library startup loads library options in this order:
- Load classpath resource `existential.properties` first (default values).
- If env var `EXISTENTIAL_CONFIG_FILE` is set and non-empty, load that file next (override defaults).

Quickstart:
1. Create a properties file:
   - `behavior.rules.requireDescriptions=true`
2. Set env var before starting your application:
   - `export EXISTENTIAL_CONFIG_FILE=/path/to/existential.properties`
3. Start your application. The library applies options on startup.

Reference:
- Format: Java `.properties`
- Supported keys:
  - `behavior.rules.requireDescriptions` (`true`/`false`)
- Source precedence:
  - Classpath defaults load first, then env-selected file overrides.
- Troubleshooting:
  - See `/Troubleshooting.md#library-configuration-load-failure`

#### More details on usage
See /docs/dev/Specification.md for terminology and complete description of library behavior.

### Extending the library
Regular use of library does not imply extending any library classes.

However, for rare circumstances where you want to significantly affect library behavior, 
we provide some ways for doing so. See the section 'Extending the classes' below.

### Code structure
Package structure:
- com.taitl.existential: public code (classes, interfaces) for use by end-user
- com.taitl.ex and subpackages: private code, implementation details
  -  com.taitl.ex.common: common/ubiquitous classes (Creator, Args, State)
  -  com.taitl.ex.concrete: concrete implementations (e.g. ConcreteExists) for the classes that end-user creates with 'new'
  -  com.taitl.ex.core: core classes, such as ExistentialConfigs, immediately used by the public code 
  -  com.taitl.ex.cross: cross-cut concepts (caching, logging)
  -  com.taitl.ex.logic: business logic implementation
    -  com.taitl.ex.configuration: configuration logic (e.g. BuildContexts)
    -  com.taitl.ex.events: event processing logic (e.g. ReceiveEvent)
    -  com.taitl.ex.library: dealing with library as a whole
    -  com.taitl.ex.transactions: transaction logic (e.g. BeginTransaction, RollbackTransaction)
    -  com.taitl.ex.validation: validation logic (e.g. ValidateTransaction)

### Extending the library with custom classes
For the rare cases where you aim to significantly affect library's behavior
(and cannot do it by other means), you can extend the library with own 
versions of the classes, but do so at your own risk.

This is worth repeating:

    ! 1. Do this at your own risk!
    ! 2. Do not ask for help/support or file issues if your case involves custom classes 

For troubleshooting, remove custom classes and see if the library works without them.

Here are some ways to put custom classes in use:
- By using a *Factory() method on an existing class (e.g. Context.transactionFactory())
- By calling Creator.inject() method. This essentially creates a global default factory for class.
  - If using Creator.inject(), it is best called very early in app life, to avoid the situation when 
  some instances already created before the call is made.
- By modifying FACTORY field on an existing library class (e.g. Transaction.FACTORY)
- For the classes that end-user creates with 'new' (e.g. Exists, Invariant) 
subclass concrete implementations (e.g. ConcreteExists, ConcreteInvariant).

To facilitate the ability to extend, we allowed a few freedoms: 
- No emphasys on final or sealed classes
- FACTORY fields are not declared final
- Creator.inject() method is declared public

But remember, with this freedom comes responsibility:
- If using custom classes, you are on your own. 
- Future versions of the library can completely rewrite its implementation, including any and all non-public classes.

In short, you should treat extending the library with own classes as 'hacking', 
which relies on undocumented features/features that not guaranteed to survive multiple versions.
