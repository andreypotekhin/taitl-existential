# Sprint 09 operational closeout performance

The [paired record](../runs/20261027T202251Z.Sprint09Closeout.md) retains three required fresh-JVM pairs plus three
investigation pairs, source hashes, setup output and all measured rows. Complete workflows cover success, typed
application values with folding, and memo rejection/explicit disposal/fresh recovery, with optional first-plus-repeated
message rendering. Every workflow asserts exact checks/effects, stock error types, empty registries and released events.

ClassValue inference removes measured per-hit allocation relative to the strong-map baseline. Complete successful
workflow timings vary substantially; a time cost remains possible and no stable speedup or numerical gate is inferred.
Richer memo errors have explicit construction/rendering costs. Allocation is not retained-memory evidence; direct
acceptance separately verifies owner release, naming-mode/class identity, independent instances and terminal Ex.close.

Related: [transaction diagnostics](TransactionDiagnostics.md), [rule diagnostics](Diagnostics.md),
[managed-index cardinality/maintenance](ManagedIndexes.md), and [selection/operation plans](ExecutionPlans.md).
