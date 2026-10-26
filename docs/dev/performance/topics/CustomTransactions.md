# Custom Transaction Performance

The [benchmark source][source] separates no-op dispatch, empty-handler dispatch, a static context effect, configured
transaction creation, custom rule preparation, and a caller-supplied transaction workflow. It is the first topic in the
[performance guide](../Readme.md).

## Evidence history

| Session | Role | Comparability |
| --- | --- | --- |
| [2026-10-21 UTC initial session][initial] | First baseline; three JVM samples | Uncommitted; source hashes retained |
| [2026-10-24 Sprint 08 session][sprint08] | Expanded dispatch and runtime preparation scenarios; three JVM samples | No comparable baseline; uncommitted; descriptive only |
| [2026-03-14 execution plan][historical] | Historical observation | Environment/workload match not established |

Latest session: Sprint 08; no before/after comparison is supported because its harness adds scenarios. Keep numeric
results in the run record. Future transaction-overlay, context-resolution, or lifecycle work should link a matching
session and explain any material change. There is no performance gate.

[source]: ../../../../src/test/java/com/taitl/existential/benchmarks/CustomTransactionBenchmark.java
[initial]: ../runs/20261021T005604Z.CustomTransactions.md
[sprint08]: ../runs/20261024T061720Z.Sprint08Transactions.md
[historical]: ../../past/planning/P03142601.Custom-transaction-instance.plan.md
