# Distribution Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Consume a documented library artifact through Maven dependency resolution without example application classes.

The [specification](../specifications/Distribution.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

The local V1 release coordinate is com.taitl:existential:0.0.1. Maven creates a normal artifact and an assembled root
existential.jar. Night City is now a shared test source and must be absent from both outputs. No Maven Central
publication is established by a successful local install or by the existing Usage instructions.

- [artifact coordinates, packaging, and build plugins](../../../pom.xml)
- [local dependency instructions](../../../docs/Usage.md)
- [release-facing changes](../../../Changelog.md)

## Approach and tradeoffs

Keep build plugins and test fixtures outside the consumer dependency surface. Prove consumption from a separate Night
City Maven project, rather than relying on the library test classpath. Use the V1/V2/V3 release outcomes.

The root POM remains the library build and keeps Night City as shared test fixtures. A separate consumer POM proves
artifact use independently; a separate reactor POM provides one-command ordering without making the library depend on
the example artifact. This preserves the root build commands and existing library coordinates while avoiding a
source/test classpath dependency in the consumer. These are build-structure choices, not new public library APIs.

Remote publication and the broader runtime matrix remain open and are not implied by local installation or reactor
success.

## Open decisions

- Confirm publication account/namespace, signing, source/Javadoc artifacts, metadata, and destination prerequisites.
- Define the supported runtime/dependency policy from actual evidence; compiler release is not a runtime matrix.
- Actual external publication needs its own authorized release action and verified destination availability.

## Verification evidence

The consumer story and its independent acceptance test are the evidence for the documented workflow. Artifact contents,
standalone compilation, and the dependency tree establish the local consumer boundary. See the [distribution
specification](../specifications/Distribution.spec.md) for the user-visible requirement.

## Local consumer and reactor

Night City is an independent Maven consumer of the installed library artifact. It owns its application and acceptance
sources, while the library may continue compiling the shared example sources as test fixtures. A distinct reactor POM
orders the root library before Night City. The consumer does not inherit from that reactor and never consumes root
sources or class directories. This avoids a dependency cycle while allowing both the independent and combined builds.

A local consumer build proves the documented public workflow and dependency surface. Local installation and reactor
execution do not establish remote publication; destination, signing, and publication remain separate decisions.

## Required quality analysis

These obligations must be resolved for each selected capability before its design is ready. Record concrete decisions
and acceptance/evidence in this design and its specification, or justify inapplicability; this list does not itself
establish passing evidence. See [Process](../auto/Process.md#required-design-analysis).

- Correctness: verify documented coordinates, artifact contents, dependencies, and the consumer-visible public workflow.
- Actionable errors: distinguish dependency/build failures from runtime configuration errors and state corrections.
- Failure recovery: define reproducible clean rebuild/retry after failed or stale local artifact preparation.
- Resource hygiene: exclude tests/examples/build plugins from the library consumer surface and close demo resources.
- Performance: measure the representative workflow against consumed artifacts; separate startup from transaction costs.

## Performance

Track consumer startup only when a specific startup goal exists; transaction benchmarks do not measure packaging. Use
the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.

[Night City Artifact Consumption and Performance]: ../project/Backlog.md#night-city-artifact-consumption-and-performance
