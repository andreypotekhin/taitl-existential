# Quantifier Dispatch

The [before/after record](../runs/20261021T223641Z.QuantifierDispatch.md) measures transaction dispatch overhead using
the unchanged CustomTransactionBenchmark in three fresh JVM samples per phase. Samples are noisy and do not establish
a consistent speed change. The record explains lookup/cache complexity and unmeasured cardinality/allocation limits.

Correctness is backed by [entity quantifier acceptance](../../../../src/test/java/com/taitl/specs/library_usage/UserCanValidateEntityQuantifiers.java).
This performance slice does not establish maintained-index or collection-scan performance.
