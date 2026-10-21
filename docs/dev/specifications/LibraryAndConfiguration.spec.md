# Library and Configuration Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/LibraryAndConfiguration.design.md) and [specification index](../Specification.md).

## Library

### Static facade access

+ User can access the library using the static facade.

### Singleton access

+ User can access the library using a singleton.

### Independent library instances

User can independently configure and use multiple instances of the library.

### Multiple business operations

Within an Existential instance, the user can configure multiple business operations.

### Multiple operation contexts

Within a business operation configuration, the user can configure multiple operation contexts.

### Multiple context rules

Within an operation context, the user can configure multiple rules such as invariants, effects, and intents.

## Library configuration

### Programmatic library options

+ User can change library configuration options programmatically.

### Behavioral flags

+ User can affect library behavior by setting behavioral flags.

### Configuration before use

+ User must configure the library before use.

### Configuration files

+ User can configure library options using a config file.

### Classpath configuration

+ User can configure library options using a classpath resource.

### Configuration environment variable

+ User can specify the config file with an environment variable.

### Initial configuration file

+ An initial config file is auto-created or otherwise available.

## Configuring contexts

### Custom context rules

User can configure the rules (constraints) on a class with a custom Context.

### Operation keys

Contexts are keyed by operation key.

### Parent context rules

The rules from parent contexts apply to child contexts.

### Parent context ordering

The rules from parent contexts execute before the rules of child contexts.

### Default context factory

User can specify a custom factory for creation of all Contexts.

### Operation context factory

User can specify a custom factory for creation of a Context for a specific op.

### Nonempty contexts

User can't configure a context without defining any rules.

## Wildcard contexts

### Wildcard operation keys

User can define a Context for an operation key with a wildcard in it.

### Wildcard operation matching

The wildcard contexts whose paths match a concrete op participate in its validation.

### Wildcard context matching

The wildcard contexts matching the currently evaluated context participate in its validation.

### Matching context rules

The rules from matching contexts apply to the context.

### Matching context ordering

The rules from matching contexts execute before the rules of the context.

## Configuring constraints

### Class event invariants

User can specify an invariant (predicate) for a class event.

### Transaction lifecycle invariants

User can specify an invariant (predicate) for transaction lifecycle event (begin, commit, rollback).

### Generic type invariants

+ User can specify an invariant for generic types using a TypeKey.

## Configs

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Contexts

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Public classes

(Technical user stories)

### Library facade classes

User can interact with the library as a whole using Ex, Existential classes.

### Operation configuration builders

User can configure rules per op using builders (ConfigBuilder, ContextBuilder, etc.).

### Context configuration classes

User can specify configuration rules per context using Context, ContextBuilder.

### Invocation configuration classes

User can specify configuration rules per business method invocation using Transaction, TransactionBuilder.

### Transaction interaction classes

User can interact with the business transaction using Ex, Existential and Tr classes.

### Event reporting classes

User can send messages to library using Ex, Existential and Tr classes.

### Transaction memo registration

+ User can register memo state for a transaction entity so bi-event rules receive before-state snapshots.
