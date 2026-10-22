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

### Runtime event index release

+ Successful completion or explicit rollback clears the transaction's runtime event, event-key, and type-key
collections, including when a caller retains the closed handle or index reference. Failed commit vacates active ID
lookup and leaves explicit rollback available through the handle. This does not undo caller-owned relationship sources.

Acceptance: [relationship lifecycle cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).

### Transaction-local state release

Closing a transaction clears transaction-owned memo values, prepared indexes, prepared event overlays, and cached
event handlers in addition to runtime event indexes. Shared configured rules and caller-owned relationship sources
remain owned by their existing configuration or application owners.

### Graceful library shutdown

+ Library close waits for active transactions to finish, then releases library resources. The timeout is configurable
through `ShutdownPolicy` or the `shutdown.timeoutMillis` properties key; zero means unlimited waiting. Timeout and
interruption leave the library draining and allow a later close attempt. Shutdown never invokes rollback callbacks or
forces caller-owned effects to be undone. A close call from a transaction callback fails promptly, even when another
thread has already started shutdown, so the callback can return and allow that shutdown to proceed.

Acceptance: [shutdown policy and concurrency](../../../src/test/java/com/taitl/specs/library_usage/UserCanShutdownLibrary.java).

## Global Cleanup

### Transaction recycling

System ensures that all transactions and their resources are recycled.

### Rule recycling

System ensures that rules, such as constraints, invariants, intents are recycled.

### Memory leak reporting

System ensures that any memory leaks are reported (Log memory leaks, e.g. when the calling app shuts down).
