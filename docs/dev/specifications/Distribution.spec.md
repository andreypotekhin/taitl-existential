# Distribution Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/Distribution.design.md) and [specification index](../Specification.md).

## Maven

### Maven dependency consumption

User can add the Library as a dependency to their project using Maven-based dependency resolution.

Local consumption can resolve an installed artifact without a Maven Central account. Remote publication requires
separate destination/account/signing prerequisites and evidence of availability; local installation is not remote
publication.

+ V1 acceptance builds Night City as an independent Maven consumer of the installed library, runs its documented
workflow, and verifies that the consumer has no dependency on root source or test output. The normal and assembled
library artifacts exclude Night City and test classes, and the consumer dependency tree contains only runtime library
dependencies plus consumer-scoped test dependencies.

Acceptance: [Night City consumer workflow](../../../examples/night_city/src/test/java/com/taitl/ex/examples/night_city/NightCityStoryTest.java).
