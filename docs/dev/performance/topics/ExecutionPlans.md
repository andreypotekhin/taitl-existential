# Execution Plans and Type Matching Performance

Sprint 07's production comparison is recorded in [Compiled Rule
Selections](../runs/20261023T215346Z.CompiledRuleSelections.md). It includes three fresh
processes per retained scenario, source/JAR identities, individual samples, medians/ranges, timing, allocation, and
reachable cache graphs. The [design](../../design/ExecutionPlans.design.md) explains the architecture and ownership;
the [delivery plan](../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md) records
implementation and closeout evidence. Measurements establish baselines, with no numerical performance gate.

## Production choice and measured tradeoffs

Cold matching uses sorted arrays of compact declaration IDs, raw-type pattern buckets, and structural generic-bound
checks. Warm matching reaches EventField's immutable ordered handler selection before event/type expansion. Arrays
were competitive or faster for primary prepared unions at 100–500 declarations, including overlapping patterns.
BitSet saves broad-row storage; Roaring can win individual multirow combination phases. Those advantages did not
justify additional production machinery or a runtime dependency for this workload. All candidates materialize the
same handler-array form. Production shares each configured pattern row, avoiding the benchmark's deliberate
per-descriptor row copies. RoaringBitmap 1.6.8 stays test-scoped.

The primary 10,000-entity/200-descriptor/100-applicable-rule success median changed from 56,888.3 to 26,336.1
µs/workflow, and allocated bytes fell from 205,599,454 to 49,036,129. Paired dispatch probes changed warm selection
from 2.9 to 0.2 µs/selection, with smaller reachable warm cache graphs. Setup retains more compiled state. These are
environment-bound observations; the run record gives ranges and separates setup, report, commit, and full workflows.
Short rejection batches were slower; a longer paired probe had overlapping ranges and lower allocation, leaving
rejection latency inconclusive. No rejection speedup is claimed.

Shared selection and transaction-overlay caches each have 1,024-entry budgets, operation inventories and stage
profiles each have 128 entries, warning suppression has 64 per instance, and legacy descriptor expansion has 4,096.
Profiles cover 200 and 500 signatures, churn beyond capacity, and zero resident selections after close. Disposed
transaction event-index graphs release backing tables. Per-thread allocation and reachable holder graphs are separate
metrics; graphs exclude class/static/JVM metadata and weak referents and are not exclusive retained heap.

## Workloads and reproduction

Night City supplies deterministic fixtures for 10,000, 50,000, and 100,000 entities, up to 200 class/generic
descriptors,
and 100, 256, and 500 applicable declarations. The comparison separates construction, prepared union/intersection,
ordered materialization, cold/warm dispatch, complete success/rejection/recovery, report/commit phases, immediate
effects, and sixteen repeated reports per entity. Upper bounds and synthetic 128,000-slot ID gaps are separately
labelled. Exact callback totals, generation invalidation, concurrency, failed misses, eviction, and cleanup have
assertions. The independent consumer reuses the shared example fixtures through the installed library artifact.

Use the exact commands and environment in [the run record](../runs/20261023T215346Z.CompiledRuleSelections.md) and the
[agent procedure](../Readme.md#reachable-memory-profiling). Every membership result escapes via volatile publication;
checksum and equivalence checks sit outside timing. Candidate order rotates across three fresh test JVMs. Performance
runs do not replace the ordinary root, consumer, or reactor test suites.

## Earlier samples

The [initial representation/workflow run](../runs/20261023T063904Z.ExecutionPlansAndTypeMatching.md) timed construction
and union together, discarded timed results, and favored BitSet. It identified a candidate rather than a production
winner. The [200-descriptor workflow run](../runs/20261023T064252Z.ExecutionPlans200Descriptors.md),
[validation/immediate phase run](../runs/20261023T064507Z.ValidationAndImmediatePhases.md), and
[allocation run](../runs/20261023T064757Z.ExecutionPlansAllocationAndImmediate.md) established workload/callback runners
but did not implement pre-expansion selection or measure reachable cache graphs. They remain append-only historical
evidence and are superseded for the production decision by Compiled Rule Selections above.

## Preliminary existing-workflow baseline

Three independent `exec:java` process samples were collected on 2026-10-22 before implementation, at source commit
`fab8bed93cad94cedcb18a6f6aeaa5585312081cb2` (the checkout revision; documentation changes were uncommitted).
Environment:
Amazon Corretto OpenJDK 17.0.15, Maven 3.9.10, macOS 15.6.1, arm64. Each process used 3 warmup and 5 measured
workflows, configured before timing; median and full observed range below are microseconds per workflow.

| Lookup rows | Workflow | Median | Range |
| ---: | --- | ---: | ---: |
| 16 | success | 566.1 | 429.4–675.0 |
| 16 | rejection with rollback | 660.2 | 566.5–667.9 |
| 16 | rejection and recovery | 561.1 | 521.0–592.8 |
| 16 | repeated 16 reports | 708.6 | 694.5–754.0 |
| 256 | success | 182.7 | 159.1–245.4 |
| 256 | rejection with rollback | 178.5 | 173.6–218.2 |
| 256 | rejection and recovery | 317.9 | 300.6–432.2 |
| 256 | repeated 16 reports | 527.0 | 477.3–575.8 |
| 4096 | success | 110.4 | 104.2–126.0 |
| 4096 | rejection with rollback | 145.0 | 121.4–216.7 |
| 4096 | rejection and recovery | 219.6 | 189.0–224.8 |
| 4096 | repeated 16 reports | 207.1 | 201.3–208.2 |

Command for each independent sample:

    mvn -q -f examples/night_city/pom.xml \\
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.NightCityWorkflowBenchmark \\
        -Dbenchmark.warmup.iterations=3 -Dbenchmark.measure.iterations=5 compile exec:java

This runner times whole transactions after scenario configuration and closes its library instance after each group.
Its size parameter controls dwelling lookup rows, not reported entity count; successful/rejected cases report one event,
and the repeated case reports the same entity sixteen times. It currently has no callback counters or allocation/heap
measurement. These figures verify the runner and provide a rough pre-change reference only; they must not be used as
the performance conclusion for Sprint 07 or compared as if workload sizes were reported entities.

The first attempted command selected this main class using Surefire (`-Dtest=NightCityWorkflowBenchmark`) and failed
because it is not a JUnit test. The `exec:java` command above is the corrected invocation.
