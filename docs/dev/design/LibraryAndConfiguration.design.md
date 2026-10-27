# Library and Configuration Design

## User outcome

Configure an operation once and use its rules through the static facade or a library instance.

The [specification](../specifications/LibraryAndConfiguration.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Public facades delegate to per-library configuration, event, and transaction components. Context rules are indexed for
operation keys. Global handler ordinals retain declaration order, while parent/wildcard resolution composes several
sources of rules. Existing tests cover context factories, parent rules, wildcards, and TypeKey configuration.

- [library entry point](../../../src/main/java/com/taitl/existential/Existential.java)
- [configuration loading](../../../src/main/java/com/taitl/ex/logic/library/ConfigureLibrary.java)
- [context construction](../../../src/main/java/com/taitl/ex/logic/configuration/actions/BuildContexts.java)
- [handler declaration ordinals](../../../src/main/java/com/taitl/ex/logic/configuration/rules/MaintainGlobalOrder.java)

## Approach and tradeoffs

Keep configuration separate from transaction execution and preserve the small public builder surface. Index shared
configuration outside repeated event dispatch; independently verify instance isolation before claiming it.

Keep instance ownership, configuration finalization, context selection, and type selection as separate responsibilities.
The decisions below and the paired specification define their behavior; project records track delivery and evidence.

Configuration becomes finalized only after context and rule construction completes. A failed build leaves the
configuration builder available so the caller can retry; the synchronized finalization step rechecks the configured
state so concurrent first transactions do not build the same configuration twice.

Each `Existential` owns its configuration, flags, rules, event indexes, transactions, and shutdown policy. `Ex` remains
an explicit JVM-wide facade over one instance, while `Creator` injection remains a JVM-wide construction facility.
Applications may configure an instance on one thread, publish it with normal Java synchronization, and use it from
other threads. Concurrent rule declaration on one instance is outside the contract. Concurrent `begin()` calls are
supported: one call finalizes the instance and the others proceed after successful publication. Finalization locks on
the instance, so unrelated instances do not block one another.

Flags are prospective. A change affects work that consults the flag after the change; it does not redo prior work.
Flags and rule declaration freeze together when the first `begin()` successfully completes configuration. A failed
finalization leaves the instance configurable and retryable. The required-description flag is consulted when each
behavior rule is added, including through a builder obtained earlier. Type-key naming is read when configuration is
indexed and remains consistent for the finalized instance. Event-splitting and inferred-type flags are read for each
new event dispatch. Changes after successful finalization fail with an actionable error.

Configuration sources retain their current order: the bundled classpath properties provide defaults, the path named
by `EXISTENTIAL_CONFIG_FILE` overrides those defaults, and programmatic flag changes may override either before the
first transaction. Each source is parsed and validated fully before its values are applied, so an invalid value does
not leave a partially applied source. Separate instances load the same process-level sources into independent flag and
shutdown-policy state.

## Context resolution and rule ordering

The existing `ConfigurationLogic` delegates selection to `ContextResolutionLogic`, which composes
`WildcardContextLogic` and `RuleOrderingLogic`. Keep builder parent-name validation separate from runtime resolution.
Resolve a concrete operation by walking its path levels from root toward the operation. At each level apply matching
wildcards in declaration order, followed by concrete contexts. A context participates once at its earliest matching
level; its rules are then inherited below it. Rule order inside a context remains declaration order, regardless of
which entity type selected a rule.

For `/api/cats/save`, the sequence `/api`, `/api/*`, `/api/cats`, `/api/cats/save` puts a wildcard before the concrete
parent it matches. Wildcards for the final level run in declaration order before the exact context. Grouping
all concrete parents before all wildcards would violate that relationship; ranking wildcard specificity would add
an unnecessary policy. The same resolved sequence drives indexing and context-created transaction/factory selection.

An asterisk consumes characters only within its path segment. A matched parent contributes to descendant operations;
this is inheritance, not a recursive wildcard operator. Partial-segment stars retain their meaning. Separate context
registrations are distinct even when names repeat, while rediscovering one registration through several ancestor
paths does not register it again. Declaration IDs preserve this distinction for the compiled
[ExecutionPlan](ExecutionPlans.design.md).

## Type diagnostics and configuration scope

Type matching uses the [entity compatibility design](EventsAndConstraints.design.md#type-matching) and retains
structured evidence from explicit TypeKeys and class signatures. When missing arguments could prevent configured
rules from matching, a default-on `typeMatching.warnUnresolvedGenerics` warning explains supplying an adequate key.
Use the normal logging/troubleshooting facilities and suppress repeats through bounded matching-selection state.

The warning property follows classpath/environment-file defaults and programmatic overrides. The positive
`TYPE_MATCHING_WARN_UNRESOLVED_GENERICS` flag supports existing Ex/instance on/off calls and freezes with configuration.
Ex affects its facade instance; independently constructed instances load global files and may override their values.
No process-wide programmatic-default registry or new configuration-source strategy is required. Suppression does not
disable validation or input checks.

Global `Creator` replacement/injection remains configuration-time process state; concurrent replacement is outside
the supported contract. Source streams are loader-owned and closed after reading; configured rule/source references
remain owned by their instance configuration.

## Managed indexing policy

Managed indexing uses a scoped IndexingPolicy rather than unrelated boolean fields spread across components. Its two
independent settings, inheritance, and override order are defined in
[QuantifiersAndIndexes.design.md](QuantifiersAndIndexes.design.md#enrollment-and-explicit-maintenance).
The same choices must be available in global source files, app/instance APIs, context and transaction configuration,
and individual definitions. Typed index policy registration preserves normal source validation, prospective behavior,
and configuration freezing.
It does not introduce a general context property framework.

## Evidence and acceptance work

- [facade tests](../../../src/test/java/com/taitl/specs/library)
- [loading tests](../../../src/test/java/com/taitl/specs/library_configuration)
- [context tests](../../../src/test/java/com/taitl/specs/configuration_workflow/configuring_contexts)

User-facing acceptance must cover overlapping contexts with reversed parent/child registration, multiple wildcard
ties, ancestor wildcard inheritance, declaration identity, matching factories, independent instances, and warning
sources/overrides. Existing adjacent tests do not by themselves establish the complete ordering contract. Delivery
readiness and measured evidence remain in the project records.

## Required quality analysis

Correctness depends on one resolved sequence serving both indexes and transaction/factory creation. Invalid operation
keys, unsupported patterns, and failed construction identify their operation/context and remedy. Parse each source
before applying it and publish finalized configuration/plans only on success, keeping failed finalization retryable.
Instance-owned selections release with configuration; transactions own their local overlays and caller source contents
are not undone. Concurrent first use publishes complete immutable state without holding locks across user callbacks.

## Performance

Resolve context order outside repeated dispatch. The [runtime execution-plan design](ExecutionPlans.design.md) keeps
EventField selections, class/type matching, and stage requirements reusable across many reported entities. Measure
context count, overlap, configuration/first use, reporting, and warm selection separately. Avoid speculative trie or
matching strategy APIs. Use the [performance guide](../performance/Readme.md); project records own delivery evidence.

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Instance Isolation and Configuration]: ../project/Backlog.md#instance-isolation-and-configuration
