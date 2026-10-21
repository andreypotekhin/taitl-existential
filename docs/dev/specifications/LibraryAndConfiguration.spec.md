# Library and Configuration Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/LibraryAndConfiguration.design.md) and [specification index](../Specification.md).

## Library

<a id="lc-001"></a>

+**LC-001** User can access the library using the static facade.

<a id="lc-002"></a>

+**LC-002** User can access the library using a singleton.

<a id="lc-003"></a>

**LC-003** User can independently configure and use multiple instances of the library.

<a id="lc-004"></a>

**LC-004** Within an Existential instance, the user can configure multiple business operations.

<a id="lc-005"></a>

**LC-005** Within a business operation configuration, the user can configure multiple operation contexts.

<a id="lc-006"></a>

**LC-006** Within an operation context, the user can configure multiple rules such as invariants, effects, and intents.

## Library configuration

<a id="lc-007"></a>

+**LC-007** User can change library configuration options programmatically.

<a id="lc-008"></a>

+**LC-008** User can affect library behavior by setting behavioral flags.

<a id="lc-009"></a>

+**LC-009** User must configure the library before use.

<a id="lc-010"></a>

+**LC-010** User can configure library options using a config file.

<a id="lc-011"></a>

+**LC-011** User can configure library options using a classpath resource.

<a id="lc-012"></a>

+**LC-012** User can specify the config file with an environment variable.

<a id="lc-013"></a>

+**LC-013** An initial config file is auto-created or otherwise available.

## Configuring contexts

<a id="lc-014"></a>

**LC-014** User can configure the rules (constraints) on a class with a custom Context.

<a id="lc-015"></a>

**LC-015** Contexts are keyed by operation key.

<a id="lc-016"></a>

**LC-016** The rules from parent contexts apply to child contexts.

<a id="lc-017"></a>

**LC-017** The rules from parent contexts execute before the rules of child contexts.

<a id="lc-018"></a>

**LC-018** User can specify a custom factory for creation of all Contexts.

<a id="lc-019"></a>

**LC-019** User can specify a custom factory for creation of a Context for a specific op.

<a id="lc-020"></a>

**LC-020** User can't configure a context without defining any rules.

## Wildcard contexts

<a id="lc-021"></a>

**LC-021** User can define a Context for an operation key with a wildcard in it.

<a id="lc-022"></a>

**LC-022** The wildcard contexts whose paths match a concrete op participate in its validation.

<a id="lc-023"></a>

**LC-023** The wildcard contexts matching the currently evaluated context participate in its validation.

<a id="lc-024"></a>

**LC-024** The rules from matching contexts apply to the context.

<a id="lc-025"></a>

**LC-025** The rules from matching contexts execute before the rules of the context.

## Configuring constraints

<a id="lc-026"></a>

**LC-026** User can specify an invariant (predicate) for a class event.

<a id="lc-027"></a>

**LC-027** User can specify an invariant (predicate) for transaction lifecycle event (begin, commit, rollback).

<a id="lc-028"></a>

+**LC-028** User can specify an invariant for generic types using a TypeKey.

## Configs

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Contexts

The original specification left this section empty. Its missing decisions are tracked in the paired design.

## Public classes

(Technical user stories)

<a id="lc-029"></a>

**LC-029** User can interact with the library as a whole using Ex, Existential classes.

<a id="lc-030"></a>

**LC-030** User can configure rules per op using builders (ConfigBuilder, ContextBuilder, etc.).

<a id="lc-031"></a>

**LC-031** User can specify configuration rules per context using Context, ContextBuilder.

<a id="lc-032"></a>

**LC-032** User can specify configuration rules per business method invocation using Transaction, TransactionBuilder.

<a id="lc-033"></a>

**LC-033** User can interact with the business transaction using Ex, Existential and Tr classes.

<a id="lc-034"></a>

**LC-034** User can send messages to library using Ex, Existential and Tr classes.

<a id="lc-035"></a>

+**LC-035** User can register memo state for a transaction entity so bi-event rules receive before-state snapshots.
