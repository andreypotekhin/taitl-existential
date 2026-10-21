# Housekeeping Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Requirement identifiers are stable. The claims describe library behavior, not legal commitments.

See the paired [design](../design/Housekeeping.design.md) and [specification index](../Specification.md).

## Caching

<a id="hk-001"></a>

**HK-001** Intermediate data structures are cached between transactions.

<a id="hk-002"></a>

**HK-002** Intermediate data structures are cached between contexts.

## Transaction Cleanup

<a id="hk-003"></a>

**HK-003** Resources taken during a transaction are released upon its completion (commit or rollback).

<a id="hk-004"></a>

**HK-004** The above applies to the transaction resources as well as all reported events.

## Global Cleanup

<a id="hk-005"></a>

**HK-005** System ensures that all transactions and their resources are recycled.

<a id="hk-006"></a>

**HK-006** System ensures that rules, such as constraints, invariants, intents are recycled.

<a id="hk-007"></a>

**HK-007** System ensures that any memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down).
