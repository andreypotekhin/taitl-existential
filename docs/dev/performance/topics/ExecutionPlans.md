# Execution Plans and Type Matching Performance

Sprint 07's final production decision is recorded in [Many-Row Matching: BitSet Re-evaluation and
Integration](../runs/20261023T222929Z.ManyRowBitSet.md). Three fresh processes per retained scenario, source/JAR
identities, individual samples, medians/ranges, timing, allocation, and reachable graphs are retained. The
[design](../../design/ExecutionPlans.design.md) explains architecture and ownership; the [archived delivery
plan](../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md) records implementation
and closeout. Measurements establish baselines, with no numerical performance gate.

## Production choice and measured tradeoffs

Cold matching uses compact declaration IDs in native BitSet rows, raw-type pattern buckets, and structural
upper-bound checks. A single matching pattern reuses its precompiled immutable handler selection. Multiple patterns
use a private pre-sized bitmap accumulator and direct ordered handler materialization. Warm EventField hits reach
the same immutable handler-array form before event/type expansion. No runtime dependency or strategy flag is added;
RoaringBitmap 1.6.8 remains test-scoped.

The prior [Compiled Rule Selections](../runs/20261023T215346Z.CompiledRuleSelections.md) fixture selected zero/one
row for sparse/broad/clustered descriptors and averaged only 1.34 overlapping rows at 256 declarations. Its comparator
also converted bitmap output through an intermediate ID array. That evidence supported arrays for those cases but
did not exercise the original twenty-row combination opportunity. Historical records remain unchanged.

The shared dwelling fixture now proves 1–32 genuine event/type rows using concrete, wildcard, raw, and upper-bound
subjects across Create/CU/CUD/Expression. At 256 declarations, native union plus final selection takes 278.5 versus
89.2 µs per 64 selections for arrays versus BitSet at 20 rows, and 406.3 versus 91.3 at 32 rows. Grouped declarations
and the original labelled synthetic shapes confirm many-row benefits. Two-row array materialization can be faster;
one-row production dispatch bypasses both materializers. The evidence does not justify a second membership store or
an invented density threshold.

Paired actual-library probes at 20 rows reduce fresh-cache selection from median 13.6 to 5.3 µs and allocation from
about 26,027 to 5,560 bytes per selection. Structural bounds dominate more of the 32-row cold resolver. Complete
10,000-entity workflow ranges overlap: most entities already reuse a cached selection and execute identical rules.
No universal transaction speedup is claimed. Prepared one-row selections slightly increase full multirow holder
graphs despite lower combination allocation; the report records that storage tradeoff explicitly.

Shared selection/overlay caches each retain at most 1,024 entries, operation inventories/profiles 128, warnings 64
per instance, and legacy descriptors 4,096. Correctness tests cover order, independent declarations, memo requirements,
concurrent ownership, retry, eviction, and cleanup. Reachable graphs exclude class/static/JVM metadata and weak
referents; they are not exclusive retained heap. Every measured library closes and has zero cached selections.

## Workloads and reproduction

Night City fixtures cover 10,000–100,000 entities, 20–200 class/generic descriptors, 100–500 declarations, and the new
1/2/3/4/8/16/20/32 applicable-row matrix. Row count varies independently of entity/rule counts. Native construction,
union, and final selection are separate. Actual-library probes separate setup, fresh-cache selection, warm hits,
complete success/rejection/explicit rollback/recovery, repeated reporting, allocation, and reachable storage.

Use exact commands and environment in [the final report](../runs/20261023T222929Z.ManyRowBitSet.md) and the
[agent procedure](../Readme.md#reachable-memory-profiling). ManyRuleRowsBenchmark uses time-based warmup/measurement,
volatile result publication, full equivalence assertions, and rotated candidate order. Its small-row selector proves
2/3 rows; grouped declarations and original synthetic shapes are separate controls. All three alternatives materialize
the same final handler form. Independent-consumer acceptance uses the same configuration and application operation.
Performance runs do not replace root, standalone consumer, or reactor verification.

## Earlier samples

The [initial representation/workflow run](../runs/20261023T063904Z.ExecutionPlansAndTypeMatching.md) timed construction
and union together, discarded timed results, and favored BitSet. It identified a candidate rather than a production
winner. The [200-descriptor workflow run](../runs/20261023T064252Z.ExecutionPlans200Descriptors.md),
[validation/immediate phase run](../runs/20261023T064507Z.ValidationAndImmediatePhases.md), and
[allocation run](../runs/20261023T064757Z.ExecutionPlansAllocationAndImmediate.md) established workload/callback runners
but did not implement pre-expansion selection or measure reachable cache graphs. They remain append-only historical
evidence. Compiled Rule Selections established production selection reuse; the many-row follow-up above supersedes
its membership representation choice.

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
