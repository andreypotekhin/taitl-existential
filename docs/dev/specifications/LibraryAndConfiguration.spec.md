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

### Managed indexing options

+ Managed-index settings have the independent scopes and precedence defined by
[managed indexing policy scopes](QuantifiersAndIndexes.spec.md#managed-indexing-policy-scopes), including context,
transaction, and individual-index overrides. Acceptance covers source validation and scoped overrides; these are typed
policy settings, not Flags constants.

### Configuration before use

+ User must configure the library before use.

### Unresolved generic warnings

+ The option `typeMatching.warnUnresolvedGenerics` defaults to true. If unresolved type arguments could prevent
configured rules from matching, the library warns when that resolution is needed and continues with proven matches.
The warning identifies the operation and reported type, explains supplying an explicit `TypeKey`, and links to
troubleshooting. It is emitted when a raw runtime type with unresolved signature variables reaches type resolution;
fully resolved types and reports with an explicit generic `TypeKey` do not produce it.

This option uses the same classpath defaults, environment-selected file, and programmatic override order as other
options. `Flags.TYPE_MATCHING_WARN_UNRESOLVED_GENERICS` supports the existing on/off APIs on `Ex` and individual
instances. `Ex` changes its facade instance; global files supply defaults loaded by each instance. There is no new
programmatic process-wide default registry. Option changes remain prospective and freeze with configuration.

Warning suppression is bounded to 64 operation/type signatures per runtime class within each library instance;
eviction may allow a later warning. The suppression map uses weak runtime-class keys so it does not retain application
class loaders after the corresponding classes become otherwise unreachable.
Invalid property values fail atomically under the normal configuration-source rules. The opt-out disables this
diagnostic, not rule validation or input checks. `UserCanConfigureLibrary` covers the default and programmatic/file
opt-outs; `UserCanApplyInheritedRules` covers the raw-signature warning and explicit generic TypeKey path.

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

The rules from parent contexts execute before the rules of child contexts, under
[deterministic context resolution](#deterministic-context-resolution).

### Deterministic context resolution

Walk the concrete operation's path levels from root to operation. At each level, select matching wildcard contexts in
their declaration order, then concrete contexts for that level in their declaration order. Each configured context
participates once, at its earliest matching level, and its rules are inherited by descendant levels. Preserve rule
declaration order within a selected context, independent of which entity class/interface/type pattern matched it.

For `/api/cats/save`, contexts `/api`, `/api/*`, `/api/cats`, and `/api/cats/save` execute in that order. Wildcards
matching the final level, such as `/api/*/save` and `/api/cats/*`, execute in their declaration order before the final
concrete context. Reversing the registration of a concrete parent and child does not reverse their inherited order.

+ Overlapping context registrations resolve in path-level order for both indexed rules and context-created transaction
  factories; a passing specification covers a parent context, a parent wildcard, two declaration-ordered final-level
  wildcards, and the final concrete context.

Use the same resolved context sequence for rule indexing and context-created transaction/factory participation.
Transaction-local contributions preserve the existing base-then-local overlay/lifecycle contract. More-specific type
matches do not acquire an independent priority over declaration order. The
[rule declaration identity](ExecutionPlans.spec.md#rule-declaration-identity) contract prevents duplicate discovery
without combining separate registrations.

### Default context factory

User can specify a custom factory for creation of all Contexts.

### Operation context factory

User can specify a custom factory for creation of a Context for a specific op.

### Nonempty contexts

User can't configure a context without defining any rules.

## Wildcard contexts

### Wildcard operation keys

User can define a Context for an operation key with a wildcard in it.

### Wildcard boundaries

An asterisk matches zero or more characters within one path segment and never consumes a slash. Both whole-segment
and partial-segment patterns are permitted. Concrete operation keys retain their existing nonempty-segment validation.
An entire wildcard segment requires a present segment: `/*` matches `/api`, not the root `/`.
Matching a parent context makes its rules applicable to descendants. Consecutive asterisks do not introduce a separate
recursive `**` operator. Runtime operations remain concrete and do not contain wildcards.

### Wildcard operation matching

The wildcard contexts whose paths match a concrete op participate in its validation.

### Wildcard context matching

The wildcard contexts matching the currently evaluated context participate in its validation.

### Matching context rules

The rules from matching contexts apply to the context.

### Matching context ordering

The rules from matching contexts execute before that concrete context under
[deterministic context resolution](#deterministic-context-resolution). Overlapping wildcards at the same level use
declaration order, without a specificity ranking.

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

## Operational error metadata

+ Library-generated semantic configuration, lifecycle, memo, index-maintenance and cleanup failures carry stable
  category
identifiers and diagnostic-specific troubleshooting remedies. Existing ExistentialException subtypes expose immutable
detached Diagnostic snapshots. Existing Java IllegalStateException/IllegalArgumentException guards preserve their exact
catch types and include category/remedy text in getMessage(); they do not acquire a new exception hierarchy.
Caller-created exceptions keep constructor behavior. Ordinary programmer preconditions and internal unsupported stubs
retain Java errors; this catalog is not a promise to classify every Java Throwable.

+ CONFIGURATION-INVALID covers invalid property sources/values, missing contexts and unsupported rule configuration;
CONFIGURATION-FROZEN covers attempts to change finalized configuration. LIBRARY-CLOSED identifies shutdown admission
rejection; SHUTDOWN-TIMEOUT/SHUTDOWN-INTERRUPTED retain draining state, bounded outstanding-resource details and
explicit
retry after cleanup. REENTRANT-LIFECYCLE rejects callback close before acquiring shutdown locks. Causes, interrupt
status
and existing explicit-only rollback remain unchanged. Java messages retain original explanatory text as escaped detail.

+ MEMO-INVALID identifies identical live/before objects or duplicate registration, leaving accepted memo state intact.
MEMO-MISSING identifies required absent before-state. INDEX-MAINTENANCE-FAILED preserves extractor cause and existing
rollback-only/detachment. CLEANUP-FAILED preserves original failure/suppressed order. Snapshots do not retain entities,
callbacks, indexes or Throwable objects; unknown stage/rule/context are omitted. Rendering uses existing escaping and
fallback policy, does not inspect arbitrary causes, catches only RuntimeException and allows Error to propagate.


Acceptance: [operational failure
diagnosis](../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseOperationalFailures.java),
[transaction-state guards](../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseTransactionState.java),
[required
descriptions](../../../src/test/java/com/taitl/specs/library_configuration/UserCanRequireRuleDescriptions.java), and
[configuration loading](../../../src/test/java/com/taitl/ex/logic/library/ConfigureLibraryTest.java).
