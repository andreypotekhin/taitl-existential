# Distribution Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Consume a documented library artifact through Maven dependency resolution without example application classes.

The [specification](../specifications/Distribution.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

The current coordinate is com.taitl:existential:0.0.1-SNAPSHOT. Maven creates a normal artifact and an assembled root
existential.jar. Night City is now a shared test source and must be absent from both outputs. No Maven Central
publication is established by a successful local install or by the existing Usage instructions.

- [artifact coordinates, packaging, and build plugins](../../../pom.xml)
- [local dependency instructions](../../../docs/Usage.md)
- [release-facing changes](../../../Changelog.md)

## Approach and tradeoffs

Keep build plugins and test fixtures outside the consumer dependency surface. Prove consumption from a separate Night
City Maven project, rather than relying on the library test classpath. Use the V1/V2/V3 release outcomes.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Confirm publication account/namespace, signing, source/Javadoc artifacts, metadata, and destination prerequisites.
- Define the supported runtime/dependency policy from actual evidence; compiler release is not a runtime matrix.
- Verify normal versus assembled artifact expectations and consumer dependency hygiene.
- Actual external publication needs its own authorized release action and verified destination availability.

## Evidence and acceptance work

- [existing facade tests; not independent consumption evidence](../../../src/test/java/com/taitl/specs/library)

[Night City Artifact Consumption and Performance] makes Night City the independent Maven consumer; V2 tracks publication
readiness separately. Add user-facing positive and negative acceptance cases for each unresolved behavior; passing
adjacent unit tests do not establish the full claim. Preserve inherited completion marks until a requirement-level
review changes them.

## Independent Night City consumption

Night City will have its own POM under `examples/night_city/`, depending on the installed Existential artifact.
Build/install the root library first, then verify Night City through that dependency. Its consumer build must not add
root `src/main/java`, `src/test/java`, `target/classes`, or `target/test-classes` to its source/classpath. It owns its
own example classes and must use public library APIs for the demonstrated workflow. The root library can continue using
Night City sources as shared test fixtures; this does not create a consumer dependency on root test output.

This is a separate Maven project; a reactor module is optional future build orchestration. Avoid a reactor cycle in
which library tests depend on an example artifact that itself requires the library. The POM/demo is Night City Artifact
Consumption and Performance work and is not implemented by this planning update. A locally installed artifact suffices.
Publishing to Maven Central is a separate distribution action, not a prerequisite for proving the public workflow.

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
