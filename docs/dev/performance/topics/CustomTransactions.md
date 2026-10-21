# Custom Transaction Performance

The [benchmark source][source] compares begin/update/commit using static context rules, a configured transaction
factory, and a caller-supplied transaction. It is the first topic in the [performance guide](../Readme.md).

## Evidence history

| Session | Role | Comparability |
| --- | --- | --- |
| [2026-10-21 UTC initial session][initial] | First baseline; three JVM samples | Uncommitted; source hashes retained |
| [2026-03-14 execution plan][historical] | Historical observation | Environment/workload match not established |

Latest comparable session: the initial session; no before/after comparison exists yet. Keep numeric results in the run
record. Future transaction-overlay, context-resolution, or lifecycle work should link a matching new session and
explain any material change. Add broader benchmarks as their topic features are implemented.

[source]: ../../../../src/test/java/com/taitl/existential/benchmarks/CustomTransactionBenchmark.java
[initial]: ../runs/20261021T005604Z.CustomTransactions.md
[historical]: ../../past/planning/P03142601.Custom-transaction-instance.plan.md
