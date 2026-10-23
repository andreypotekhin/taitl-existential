# Execution Plans and Type Matching Performance

Status: representation, complete-workflow, validation/immediate phase, and per-thread allocation samples recorded;
byte-level retained-heap evidence remains pending. This topic covers context/type selection and validation-only reporting,
retaining `EventField` as the prepared-selection cache. The governing
[design](../../design/ExecutionPlans.design.md) defines the workload and ownership model; the
[delivery plan](../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md) defines commands,
comparisons, and acceptance evidence to collect.

Night City supplies deterministic shared fixtures for 10,000, 50,000, and 100,000 distinct entities, 20, 100, and 200
class/generic descriptors, and 100, 256, and 500 rule declarations. Cardinalities are independently controlled. Include
repeated reports, sparse versus broad applicability, inherited signatures, upper bounds, empty selections, concurrent
first use, and more unique signatures than the selected cache capacity.

Measure setup/finalization, reporting, first boundary validation, and warmed complete transactions separately. Compare
validation-only against immediate configurations using the same fixtures. Compare sorted integer membership arrays,
JDK `BitSet`, and Java `RoaringBitmap` from the initial prototype. Include the primary compact-ID workload and labelled
larger sparse, dense, and clustered-ID stress cases. Measure construction, union/intersection, and enumeration into
the same final ordered EventField selection, as well as complete workflow cost.

Record allocations and retained heap separately from elapsed time and serialized sizes, with actual
callback/resolution counts and cleanup. Roaring starts as a test-scoped prototype dependency with its exact version
recorded; this does not select it as a library runtime dependency. A production dependency requires a measured
advantage and consumer/artifact review.

Collect three comparable fresh-JVM samples per retained scenario under the
[performance guide](../Readme.md). Publish append-only run records with source identities before claiming improvement
or choosing a numerical gate. Existing relationship and empty-transaction measurements use different work and do not
substitute for this baseline.

## Sprint 07 samples

The initial compact-ID prototype comparison is recorded in
[the first representation/workflow run](../runs/20261023T063904Z.ExecutionPlansAndTypeMatching.md). A follow-up
[200-descriptor workflow run](../runs/20261023T064252Z.ExecutionPlans200Descriptors.md) includes three independent
10,000-entity/200-descriptor/100-rule samples, one 100,000-entity smoke, and one 500-rule smoke. The
[validation/immediate phase run](../runs/20261023T064507Z.ValidationAndImmediatePhases.md) separates report and commit
times and compares immediate effects on the same fixture at smaller scale. The follow-up
[allocation run](../runs/20261023T064757Z.ExecutionPlansAllocationAndImmediate.md) adds per-thread allocated bytes to
three-process success/rejection/recovery and report/commit phase samples. These establish reproducible workloads and
callback counts; they do not establish a throughput promise, retained-heap byte limit, or numeric performance gate.

## Preliminary existing-workflow baseline

Three independent `exec:java` process samples were collected on 2026-10-22 before implementation, at source commit
`fab8bed93cad94cedcb18a6f6aeaa5585312081cb2` (the checkout revision; documentation changes were uncommitted).
Environment:
Amazon Corretto OpenJDK 17.0.15, Maven 3.9.10, macOS 15.6.1, arm64. Each process used 3 warmup and 5 measured
workflows, configured before timing; median and full observed range below are nanoseconds per workflow.

| Lookup rows | Workflow | Median | Range |
| ---: | --- | ---: | ---: |
| 16 | success | 566,075 | 429,425–674,967 |
| 16 | rejection with rollback | 660,158 | 566,542–667,933 |
| 16 | rejection and recovery | 561,142 | 521,017–592,825 |
| 16 | repeated 16 reports | 708,567 | 694,475–754,008 |
| 256 | success | 182,725 | 159,108–245,392 |
| 256 | rejection with rollback | 178,508 | 173,567–218,225 |
| 256 | rejection and recovery | 317,867 | 300,633–432,167 |
| 256 | repeated 16 reports | 527,025 | 477,300–575,758 |
| 4096 | success | 110,400 | 104,242–126,033 |
| 4096 | rejection with rollback | 145,008 | 121,358–216,675 |
| 4096 | rejection and recovery | 219,617 | 188,975–224,800 |
| 4096 | repeated 16 reports | 207,092 | 201,333–208,183 |

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
