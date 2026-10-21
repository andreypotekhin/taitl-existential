# Distribution Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/Distribution.design.md) and [specification index](../Specification.md).

## Maven

<a id="dist-001"></a>

**DIST-001** User can add the Library as a dependency to their project using Maven-based dependency resolution.

Local consumption can resolve an installed artifact without a Maven Central account. Remote publication requires
separate destination/account/signing prerequisites and evidence of availability; local installation is not remote
publication. V1 acceptance uses Night City as an independent Maven project depending on the installed library.
