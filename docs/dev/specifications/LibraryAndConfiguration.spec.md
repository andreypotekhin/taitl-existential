# Library and Configuration Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Legacy `+` marks are preserved. Add `+` to a requirement only when its current acceptance evidence passes; see the
traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/LibraryAndConfiguration.design.md) and [specification index](../Specification.md).

## Library

### Static facade access

+ User can access the library using the static facade.

### Singleton access

+ User can access the library using a singleton.

### Independent library instances

+ User can independently configure and use multiple instances of the library.

Each instance owns its flags, configured rules, event and transaction state, and shutdown policy. JVM-wide facilities
such as the static `Ex` facade and `Creator` injection are explicitly shared and are not instance configuration.
Configure an instance on one thread, safely publish it, and use it on another thread. Concurrent configuration calls
on the same instance are not supported.

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

Flag changes are prospective: they affect subsequent work that consults the flag and never redo work already
completed. Required rule descriptions are checked when each behavior is added. All flags and rule declarations become
fixed when the first transaction successfully finalizes configuration. Attempts to change a flag or declare more
rules afterwards fail with an explanation that the instance has already been configured.

Concurrent first transaction attempts on the same instance wait for one configuration finalization to finish. A
successful finalization is safely visible to threads that use the instance after normal publication or after waiting
for that first transaction. Independent instances finalize independently.

### Configuration before use

+ User must configure the library before use.

### Configuration build failure recovery

+ If configuration finalization fails while constructing contexts or rules, the library remains unconfigured and the
  caller can retry by starting another transaction after correcting or recovering the failing factory.

### Configuration files

+ User can configure library options using a config file.

Configuration sources are applied in order: the bundled classpath properties, the file selected by
`EXISTENTIAL_CONFIG_FILE`, then programmatic flag changes. Each source is fully validated before any of its values are
applied. Invalid input leaves the instance's settings unchanged for that source and reports the source and invalid
key/value.

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
