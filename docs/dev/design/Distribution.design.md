# Distribution Design

Status: current-state design with open decisions; not a declaration that all specified behavior is implemented.

## User outcome

Consume a documented library artifact through Maven dependency resolution without example application classes.

The [specification](../specifications/Distribution.spec.md) defines intended behavior. See the
[assessment](../project/Assessment.md) for release evidence and [agent process](../auto/Process.md) for the next steps.

## Current implementation

The current coordinate is com.taitl:existential:0.0.1-SNAPSHOT. Maven creates a normal artifact and an assembled
root existential.jar. Night City is now a shared test source and must be absent from both outputs. No Maven Central
publication is established by a successful local install or by the existing Usage instructions.

- [artifact coordinates, packaging, and build plugins](../../../pom.xml)
- [local dependency instructions](../../../docs/Usage.md)
- [release-facing changes](../../../Changelog.md)

## Approach and tradeoffs

Keep build plugins and test fixtures outside the consumer dependency surface. Prove consumption from a separate
consumer project, rather than relying on the library test classpath. Retain existing V1/V2 release labels.

These observations retain the existing architecture. They do not approve new public interfaces or resolve the open
choices below. Record future decisions here, update the normative specification, and reconcile the execution plan.

## Open decisions

- Confirm publication account/namespace, signing, source/Javadoc artifacts, metadata, and destination prerequisites.
- Define the supported runtime/dependency policy from actual evidence; compiler release is not a runtime matrix.
- Verify normal versus assembled artifact expectations and consumer dependency hygiene.
- Actual external publication needs its own authorized release action; this task only prepares local artifacts.

## Evidence and acceptance work

- [existing facade tests; not independent consumption evidence](../../../src/test/java/com/taitl/specs/library)

V2-E05 owns independent consumption and publication readiness; V2-E06 verifies release artifacts.
Add user-facing positive and negative acceptance cases for each unresolved behavior; passing adjacent unit tests do
not establish the full claim. Preserve inherited completion marks until a requirement-level review changes them.

## Performance

Track consumer startup only when a specific startup goal exists; transaction benchmarks do not measure packaging.
Use the [performance guide](../performance/Readme.md) and link records from the owning sprint and execution plan.
