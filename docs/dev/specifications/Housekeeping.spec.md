# Housekeeping Specification

These are the intended behavior claims migrated from the original library specification.
Implementation and evidence are tracked separately in the [traceability matrix](../project/TraceabilityMatrix.md).
Legacy `+` marks are preserved. Add `+` to a requirement only when its current acceptance evidence passes; see the
traceability matrix.
Reference requirements by their descriptive headings. The claims describe library behavior, not legal commitments.

See the paired [design](../design/Housekeeping.design.md) and [specification index](../Specification.md).

## Caching

### Caching between transactions

+ Immutable operation plans, indexes and selections are cached between transactions within their configuration owner.
See [cache ownership and global close](#cache-ownership-and-global-close) for bounds and invalidation.

### Caching between contexts

+ Shared metadata and configuration-owned selections can serve matching contexts; each declaration occurrence keeps
its origin. No event payload or validation result is shared between contexts. See [cache
ownership](#cache-ownership-and-global-close).

## Transaction Cleanup

### Transaction resource release

+ Transaction-owned resources are released on successful commit, explicit rollback or close; failed boundaries detach
active lookup and require explicit cleanup through a retained handle. See [transaction-local
release](#transaction-local-state-release).

### Reported event resource release

+ Disposal clears reported event and memo/index contents as well as transaction-local callback and selection owners.
Caller-owned objects/collections are not cleared. See [runtime index release](#runtime-event-index-release).

### Runtime event index release

+ Successful completion or explicit rollback clears the transaction's runtime event, event-key, and type-key
collections, including when a caller retains the closed handle or index reference. Failed commit vacates active ID
lookup and leaves explicit rollback available through the handle. This does not undo caller-owned relationship sources.

Acceptance: [relationship lifecycle
cases](../../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).

### Transaction-local state release

+ Closing a transaction clears transaction-owned memo values, prepared indexes, prepared event overlays, and cached
event handlers in addition to runtime event indexes. Shared configured rules and caller-owned relationship sources
remain owned by their existing configuration or application owners.

### Managed relationship index release

+ Transaction-local managed contents, pending changes, seed references, identity/key/supersession records, and
  bound-view
storage are released on disposal even if a closed handle or view is retained. Successful checkpoints retain them for
the continuing transaction. Failed checkpoints/commits use the existing detach/rollback-only contract, without a
secondary retention registry. Shared configuration caches retain no managed runtime entities. Acceptance: [managed
release](../../../src/test/java/com/taitl/specs/library_usage/UserCanReleaseManagedIndexes.java).

### Graceful library shutdown

+ Library close waits for active transactions to finish, then releases library resources. The timeout is configurable
through `ShutdownPolicy` or the `shutdown.timeoutMillis` properties key; zero means unlimited waiting. Timeout and
interruption leave the library draining and allow a later close attempt. Shutdown never invokes rollback callbacks or
forces caller-owned effects to be undone. A close call from a transaction callback fails promptly, even when another
thread has already started shutdown, so the callback can return and allow that shutdown to proceed.

Acceptance: [shutdown policy and
concurrency](../../../src/test/java/com/taitl/specs/library_usage/UserCanShutdownLibrary.java).

## Global Cleanup

### Transaction recycling

+ The library drops completed transaction ownership and releases transaction state deterministically on disposal.
Detached failed handles are not retained in a secondary registry. Caller-retained handles remain caller-owned.
See [managed release](#managed-relationship-index-release) and [cache/global
ownership](#cache-ownership-and-global-close).

### Rule recycling

+ Transaction-local rule captures are released on disposal; configuration-owned definitions are released with their
library owner after shutdown drains. Caller-retained definitions/sources remain caller-owned. No object pool or GC
deadline is promised. See [cache/global ownership](#cache-ownership-and-global-close).

### Memory leak reporting

+ The library reports known outstanding resources when shutdown fails because of timeout or interruption. Diagnostics
identify known active transactions and in-flight work, include a total count and bounded detail, and explain completing
or cleaning up those resources before retrying close. Details use operation/transaction metadata rather than entity
payloads. Reporting is based on library-owned registries and accounting, not deterministic garbage collection.

This is not a guarantee to detect arbitrary application leaks or enumerate detached failed handles. No secondary
registry retains failed handles solely for reporting, and no rollback callback is invoked automatically. The narrower
known-resource contract replaces the historical universal leak-detection claim; it is backed by [shutdown
diagnostics](../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseOutstandingResources.java).

## Cache ownership and global close

+ Immutable operation indexes/execution plans are cached within a library configuration (128 operations per cache).
Prepared EventField selections, including negative matches, are bounded to 1024 entries per inventory generation; local
merged selections are transaction-owned and bounded to 1024. Inventory identity changes invalidate prepared selections.
Eviction drops cache ownership without closing generations still used by an active handle. Configuration freezes at
first begin; arbitrary caller mutations and hot reload do not invalidate configuration caches.

+ Inferred TypeKeys use ClassValue associations with two naming modes per runtime Class, avoiding strong process-wide
Class-key maps. Shared legacy descriptor expansion is limited to 4096 entries with weak Class keys; unresolved-warning
suppression is instance-owned and bounded to 64 signatures. Shared caches retain metadata, never event payloads or
transaction-local captures. The unused internal CacheHandlers stub is not the public caching API or execution path.

+ Library close drains active work, then clears its owned operation/selection/warning caches and references to
  configured
rules. One instance closing does not clear another instance's configuration or shared reusable ClassValue metadata.
Caller-retained configuration/definitions/sources remain caller-owned; close does not clear those application objects.
No garbage-collection deadline, arbitrary leak detector or class-unloading timing guarantee is implied.

+ Ex.close closes the current facade instance idempotently and does not replace it. Further begin is rejected.
  Independent
instances stay usable; a new facade instance requires explicit installation. The test-only setter returns its previous
instance without implicitly closing it. No automatic global reset or process-wide configuration defaults are added.


Acceptance: [shared/global
ownership](../../../src/test/java/com/taitl/specs/library_usage/UserCanReleaseSharedCaches.java),
[operation reuse/eviction](../../../src/test/java/com/taitl/specs/library_usage/UserCanReuseRuleSelections.java),
[selection bounds/invalidation](../../../src/test/java/com/taitl/ex/logic/execution/RuleSelectionTest.java),
[local capture release](../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseRuleViolations.java), and
[transaction resource release](../../../src/test/java/com/taitl/existential/transactions/TrTest.java).
