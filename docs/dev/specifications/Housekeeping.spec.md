# Housekeeping Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
A leading `+` preserves an existing completion mark; this migration does not add new completion assertions.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/Housekeeping.design.md) and [specification index](../Specification.md).

## Caching

### Caching between transactions

Intermediate data structures are cached between transactions.

### Caching between contexts

Intermediate data structures are cached between contexts.

## Transaction Cleanup

### Transaction resource release

Resources taken during a transaction are released upon its completion (commit or rollback).

### Reported event resource release

The above applies to the transaction resources as well as all reported events.

## Global Cleanup

### Transaction recycling

System ensures that all transactions and their resources are recycled.

### Rule recycling

System ensures that rules, such as constraints, invariants, intents are recycled.

### Memory leak reporting

System ensures that any memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down).
