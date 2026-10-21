# Library and Configuration Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Configure an operation once and use its rules through the static facade or a library instance.

The [specification](../specifications/LibraryAndConfiguration.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

Public facades delegate to per-library configuration, event, and transaction components. Context rules are indexed
for operation keys. Global handler ordinals retain declaration order, while parent/wildcard resolution composes
several sources of rules. Existing tests cover context factories, parent rules, wildcards, and TypeKey configuration.

- [library entry point](../../../src/main/java/com/taitl/existential/Existential.java)
- [configuration loading](../../../src/main/java/com/taitl/ex/logic/library/ConfigureLibrary.java)
- [context construction](../../../src/main/java/com/taitl/ex/logic/configuration/actions/BuildContexts.java)
- [handler declaration ordinals](../../../src/main/java/com/taitl/ex/logic/configuration/rules/MaintainGlobalOrder.java)

## Approach and tradeoffs

Keep configuration separate from transaction execution and preserve the small public builder surface. Index shared
configuration outside repeated event dispatch; independently verify instance isolation before claiming it.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Specify ties among overlapping wildcards, parents, exact contexts, and transaction rules; existing suggestions
- propose different orders. Do not silently turn an implementation detail into a new precedence guarantee.
- Define configuration freezing and concurrency guarantees, including global Creator injection.
- Distinguish build-time JDK requirements, compiler release 11, and actually tested runtime compatibility.

## Evidence and acceptance work

- [facade tests](../../../src/test/java/com/taitl/specs/library)
- [loading tests](../../../src/test/java/com/taitl/specs/library_configuration)
- [context tests](../../../src/test/java/com/taitl/specs/configuration_workflow/configuring_contexts)

V1-E01 and V1-E02. Cover independent instances, overlapping contexts, registration order, and frozen configuration.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Performance

Measure context resolution/configuration separately from dispatch; do not optimize unmeasured alternatives such as
tries.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.
