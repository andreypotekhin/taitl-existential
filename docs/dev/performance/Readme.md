# Performance Evidence

Performance code grows by topic under `src/test/java/com/taitl/existential/benchmarks/`, using `*Benchmark` class names.
The existing Maven `benchmarks` profile selects those classes. Add a benchmark when a feature creates a measurable
performance question; specification tests still establish correctness. [Custom transactions] is the initial topic. JMH
migration and automatic regression thresholds are separate future decisions.

## Record a baseline or comparison

Run a benchmark in three separate Maven invocations so each sample uses a new test JVM. Keep the machine, JDK/JVM
settings, workload, scenario order, harness version, and iteration counts comparable. From the repository root:

    mvn -q -Pbenchmarks -Dbenchmark.warmup.iterations=5000 -Dbenchmark.measure.iterations=50000 test

For a single topic add `-Dtest=CustomTransactionBenchmark` (replace with the chosen class). Check the output and report
to ensure that the intended benchmark actually ran. The profile runs benchmarks rather than the ordinary test suite; it
does not replace `mvn clean install`.

Create an append-only record in `runs/` named `YYYYMMDDTHHMMSSZ.Topic.md`, using the [run template](RunTemplate.md). One
record may collect the three independent samples of a measurement session. Retain focused output and exact commands,
revision/dirty state, harness/source identity, workload, iterations, runtime, OS/architecture/processor description, JVM
options, per-scenario units, samples, median, range, and interpretation. Use UTC for timestamps. Do not record
usernames, hostnames, home paths, or serial numbers. Mark unknown metadata as unknown instead of guessing. For an
uncommitted baseline, retain source hashes and describe the relevant changes; replace it with a committed-revision
baseline when available.

Keep a short `topics/Topic.md` summary linking the first baseline, latest comparable measurement, and explanations of
material changes. The run record is authoritative for numbers. Link it from the execution plan and sprint review; do not
copy the same table into multiple documents. Historical plan timings remain linked historical observations unless enough
matching environment/workload metadata establishes comparability.

## Interpret measurements

The current harness reports average elapsed nanoseconds per transaction operation and derived operations per second.
These are not latency percentiles. Three samples supply a descriptive median/range, not a confidence interval. Scenarios
run in a fixed order within one JVM; startup/JIT, allocation/GC, thermal state, and background activity can influence
the results. The positive-throughput assertions detect gross failure, not performance regression.

Compare matching scenarios and environments. Report relative change as `(new - baseline) / baseline * 100`, naming the
metric: higher `ns/op` is slower, higher `ops/s` is faster. Investigate material repeated changes before drawing
conclusions. Do not compare different topics, cardinalities, machines, or harnesses as if they were one series.

Add cardinality parameters, repeated-versus-unique event workloads, stage/handler cases, or index maintenance costs when
implementing the relevant feature. Allocation and retained-state measurements need their own methods; throughput cannot
establish resource safety. Adopt JMH when fork/warmup/statistical control or automated thresholds are required.

The [quantifier dispatch topic](topics/QuantifierDispatch.md) records the shared expression-route repair and its
before/after dispatch measurements.

The [relationships and evolution topic](topics/RelationshipsAndEvolution.md) records source-cardinality and repeated
report measurements plus the membership-only lookup improvement.

The [Night City consumer workflow topic](topics/NightCityConsumerWorkflows.md) measures complete workflows against the
installed library artifact, with configuration/source setup excluded and success, recovery, cardinality, and repeated
event cases separated. V1's local `0.0.1` closeout samples are recorded in
[NightCityV1Closeout](runs/20261022T205600Z.NightCityV1Closeout.md).

The [instance configuration topic](topics/InstanceConfiguration.md) separates fresh-instance construction, rule
setup, first transaction, and close from transactions on an already configured instance.

[Custom transactions]: topics/CustomTransactions.md
