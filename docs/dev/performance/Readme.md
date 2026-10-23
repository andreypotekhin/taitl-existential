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

## Timing units

Report elapsed time in microseconds (µs), using `µs/op` or `µs/workflow` and one decimal place for samples, medians,
and ranges. Keep the monotonic clock's full resolution internally; divide elapsed totals by 1,000 and by the iteration
count using floating-point arithmetic before formatting. Round only for presentation, so fractional microseconds are
not truncated. Throughput stays in `ops/s`; allocation and retained memory keep their byte units.

Historical timing tables, prose, and focused output excerpts were converted on 2026-10-23 without rerunning any
measurements. Each run record identifies this presentation conversion; recorded revisions, source hashes, workload
counts, throughput, and percentage comparisons retain their original meaning. Converted excerpts show normalized
units rather than verbatim historical output. New measurement sessions still use separate append-only run records.

## Reachable-memory profiling

`BenchmarkMemory` is test-only instrumentation. Timing/allocation profiles work without its agent and print `-1`
for unknown reachable bytes. Evidence runs set `-Dbenchmark.heap.required=true` to fail when instrumentation is absent.
Prepare the agent after compiling test sources, from the repository root:

    mvn test-compile
    mkdir -p /tmp/existential-profile
    printf 'Premain-Class: com.taitl.existential.benchmarks.BenchmarkMemory\n' > /tmp/existential-profile/agent.mf
    jar --create --file /tmp/existential-profile/heap-agent.jar \
        --manifest /tmp/existential-profile/agent.mf \
        -C target/test-classes com/taitl/existential/benchmarks/BenchmarkMemory.class

Set these options for the measured command so Maven's forked test JVM also receives them:

    export JAVA_TOOL_OPTIONS="-javaagent:/tmp/existential-profile/heap-agent.jar \
        --add-opens=java.base/java.lang=ALL-UNNAMED \
        --add-opens=java.base/java.util=ALL-UNNAMED \
        --add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
        --add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED \
        --add-opens=java.base/java.lang.ref=ALL-UNNAMED"
    mvn -Pbenchmarks -Dtest=ExecutionPlanBenchmark -Dbenchmark.types=200 -Dbenchmark.rules=100 \
        -Dbenchmark.warmup.iterations=3 -Dbenchmark.measure.iterations=5 -Dbenchmark.heap.required=true test
    unset JAVA_TOOL_OPTIONS

The agent sums shallow instrumented object sizes over an identity-deduplicated graph of instance fields and reference
arrays. Class objects, static roots, ClassValue/JVM metadata, and weak referents are excluded. Results describe storage
reachable from the selected holder, including shared objects reached from it; they are not exclusive dominator
retention, process heap, a class-loader collection proof, or a byte limit. Inaccessible fields fail rather than produce
partial evidence. Per-thread allocated bytes are a separate metric.

`MembershipRepresentationBenchmark` reports µs per descriptor batch and bytes per selection. It compares actual
matched declaration rows using construction, prepared union/intersection, and identical ordered materialization.
Every measured result escapes through a volatile reference; checksums and equivalence checks run outside timing.
Rotate `-Dbenchmark.sample=1`, `2`, and `3` across fresh JVMs. Use explicit modest iteration counts for
`ExecutionPlanBenchmark`: its complete-transaction phase can execute millions of callbacks. Large iteration counts
intended for tiny operation benchmarks are unsuitable for every workload.

## Interpret measurements

The current harness reports average elapsed microseconds (µs) per transaction operation and derived operations per
second.
These are not latency percentiles. Three samples supply a descriptive median/range, not a confidence interval. Scenarios
run in a fixed order within one JVM; startup/JIT, allocation/GC, thermal state, and background activity can influence
the results. The positive-throughput assertions detect gross failure, not performance regression.

Compare matching scenarios and environments. Report relative change as `(new - baseline) / baseline * 100`, naming the
metric: higher `µs/op` is slower, higher `ops/s` is faster. Investigate material repeated changes before drawing
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

The [execution plans and type matching topic](topics/ExecutionPlans.md) links the real Night City representation
comparison and production selection integration: native BitSet rows, compiled one-row selection, EventField lookup,
bounded caches, and comparable baseline/candidate cold/warm, complete-workflow, allocation, and reachable-memory evidence.
Earlier prototype records remain historical; no numerical performance gate is established.

[Custom transactions]: topics/CustomTransactions.md

For the genuine many-row comparison, run from the repository root after compiling test sources:

    mvn -o -Pbenchmarks -Dtest=ManyRuleRowsBenchmark \
        -Dbenchmark.rules=256 -Dbenchmark.sample=1 \
        -Dbenchmark.warmup.millis=200 -Dbenchmark.measure.millis=300 test

Repeat with sample=1,2,3 and rules=100,256,500. Optional controls use benchmark.small=true (2/3 rows),
benchmark.grouped=true, or benchmark.synthetic=true. Logs assert actual row cardinalities. Graph collection requires
the agent and module opens above; the [many-row record](runs/20261023T222929Z.ManyRowBitSet.md) gives exact methods,
paired library probes, workload sizes, source identities, and all retained samples.
