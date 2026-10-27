# Structured diagnostics performance

The [final baseline/candidate comparison](../runs/20261027T074532Z.Diagnostics.md) measures complete success, rejection/rollback,
fresh recovery, 3,000-violation collection, and ordered-effect stopping on 10,000/50,000-entity workloads. It retains
three required independent JVM samples per variant plus three additional interleaved pairs investigating timing
variation. Every one of 336 scenario samples checks callbacks, violations, folding, and disposal. Source hashes,
setup/allocation data, and first/repeated default-message costs are retained.

Successful warm allocation falls about 2.4 MB per 50,000-entity workflow. Timing varies substantially and drifts for
both variants in the interleaved investigation; a small success-path time cost remains possible, and no speedup or
stable regression is inferred. Detailed failure messages deliberately cost more than the former aggregate count.
Allocation describes bytes created, not retained memory. There is no numerical performance gate.

Related evidence: [access events](AccessEvents.md), [managed indexes](ManagedIndexes.md),
[custom transactions](CustomTransactions.md), and [execution plans](ExecutionPlans.md).


The [transaction-state follow-up](TransactionDiagnostics.md) measures closed/rollback-only guarded operations and
missing active IDs separately, including explicit disposal and fresh successful work.
