# Library and Configuration Design

Status: Sprint 06 isolation and configuration decisions recorded; broader context matching and runtime questions remain open.

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

These observations retain the existing architecture. The decisions below and the paired specification define the
Sprint 06 behavior; they do not resolve the remaining open questions.

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

## Open decisions

- Specify ties among overlapping wildcards, parents, exact contexts, and transaction rules; existing suggestions propose
  different orders. Do not silently turn an implementation detail into a new precedence guarantee.
- Per-instance config source injection is deferred. Sprint 06 keeps the existing classpath/environment sources and
  does not add a new public source API.
- Global `Creator` replacement/injection remains configuration-time process state; concurrent replacement is not
  supported. Further per-instance injection is deferred.
- Distinguish build-time JDK requirements, compiler release 11, and actually tested runtime compatibility.

## Evidence and acceptance work

- [facade tests](../../../src/test/java/com/taitl/specs/library)
- [loading tests](../../../src/test/java/com/taitl/specs/library_configuration)
- [context tests](../../../src/test/java/com/taitl/specs/configuration_workflow/configuring_contexts)

[Night City Core Workflow] owns ordinary exact-context configuration; [Instance Isolation and Configuration] and
Contexts and Type Matching own isolation, options, and broader matching. Add user-facing positive and negative
acceptance cases for each unresolved behavior; passing adjacent unit tests do not establish the full claim. Preserve
inherited completion marks until a requirement-level review changes them.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: specify instance/global state, source precedence, freeze timing, and deterministic context resolution.
- Actionable errors: identify invalid keys/options/context and correction without exposing sensitive configuration.
- Failure recovery: define state after loading/building fails and whether retry, replacement, or close is permitted.
- Resource hygiene: define stream/configuration/factory ownership and release plus shared-state concurrency limits.
- Performance: separate startup/context construction from event dispatch; state operation/context counts and scaling.

## Performance

Measure context resolution/configuration separately from dispatch; do not optimize unmeasured alternatives such as
tries. Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[Night City Core Workflow]: ../project/Backlog.md#night-city-core-workflow
[Instance Isolation and Configuration]: ../project/Backlog.md#instance-isolation-and-configuration
