# Transaction-state diagnostics performance

The [baseline/candidate record](../runs/20261027T192137Z.TransactionDiagnostics.md) compares complete successful
transactions with
closed-handle, rollback-only and missing-ID rejection/recovery workflows. Three fresh JVM samples per final variant
use identical harnesses, three warmups, five measured workflows, and exact callback/rollback/disposal assertions.
Setup and first-plus-repeated message rendering are reported separately. Source hashes and all output are retained.

Successful branches add no diagnostic construction; observed success ranges overlap. Unrendered failures allocate
about 560–750 additional bytes per rejected request for detached facts. Detailed rendering has an additional explicit
cost. The investigation removed redundant exception construction and stack copying before final measurement.
These are descriptive short-workflow samples; no speedup, numerical gate or arbitrary leak detection is claimed.

Related: [rule diagnostics](Diagnostics.md), [managed indexes](ManagedIndexes.md), and
[custom transactions](CustomTransactions.md).
