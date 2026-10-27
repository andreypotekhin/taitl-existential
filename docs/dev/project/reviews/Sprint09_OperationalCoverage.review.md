# Sprint 09 operational coverage review

Sprint/epic: [Sprint 09](../sprints/Sprint09_V2IndexesAndOperationalCoverage.md) /
[Indexes and Operational Coverage](../Backlog.md#indexes-and-operational-coverage). Review date: 2026-10-27.
Status: complete for admitted Sprint 09 scope. Implementation was committed in the shared workspace as abe3882
during final review. The defensive rollback metadata correction/test and final evidence reconciliation follow that
commit and are included in this checkout. V2 is not released; Sprint 10 hardening remains.

## Outcome and acceptance evidence

The [completed closeout plan](../../past/planning/P10272602.Sprint09-closeout.plan.md) finishes the remaining
operational
contracts. The earlier managed/index/access/diagnostic plans linked from Sprint 09 remain historical delivery evidence.
The new 38 direct cases cover nine typed-value scenarios, nine supplied-rule cases, four shared/global ownership cases
and 16 operational error cases. Existing V1 and Sprint 06–09 suites still pass.

| Sprint acceptance | Evidence and disposition |
| --- | --- |
| Maintained relationships and lifecycle variations | Managed index/join/policy/query/release suites and UserCanMaintainWorkingRelationships pass; standalone sources remain caller-owned. Access/general events, Transit/Port, checkpoints and failure recovery retain their existing direct suites. |
| Additional standard-event values | [Typed envelopes][values] reach ordinary rules with actor/destination data, matching generic/full-name keys, distinct identity/folding, checkpoint continuation and explicit cleanup/fresh recovery. No custom event splitting is introduced. |
| Rich public semantic diagnostics | Rule/state diagnostics plus [operational failures][errors] cover configuration, shutdown, memo, maintenance, cleanup and standalone evaluation. Stock catch types, causes, interrupt status, collection/classification and rollback semantics remain intact. |
| Supplied rule binding | [Reuse and policy][rules] enforce prospective attachment checks without policy mutation, retain equal-type identity and independently bind differing intent types, and preserve declaration provenance across instances. Read-only reuse is the supported contract. |
| Cache/global resources | [Owner acceptance][owners], [selection tests][selection] and [operation reuse][reuse] establish budgets, negative caching, generation invalidation, deterministic release, naming/class identity, concurrency and terminal/idempotent facade close without affecting another instance. |
| Maintenance/cardinality performance | Prior [managed-index record][managed] retains complete maintenance/cardinality/cleanup workloads. [Closeout measurements][performance] add complete typed-value and error/recovery workflows plus inferred-type cache hits. |
| Distribution readiness | [Publication prerequisites][publication] are explicit. Installed artifact/independent consumer are verified; external publication remains a separately authorized release action. |

No admitted Sprint 09 acceptance item is left open. Previously recorded non-goals remain: shared mutable entity indexes,
arbitrary callback deep snapshots or post-binding mutation/hot reload, custom event registration/splitting (V3),
universal
leak detection/GC deadlines and unapproved external publication. These are supported-boundary decisions, not missing
implementation silently moved to hardening.

## Commands and environment

Corretto 17.0.15+6-LTS, Maven 3.9.10, macOS 15.6.1 / Darwin 24.6.0 arm64, 10 logical CPUs. Executed from repository
root:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify dependency:tree -Dscope=compile
    mvn -f reactor/pom.xml clean install
    mvn -f examples/night_city/pom.xml exec:java -Dexec.mainClass=com.taitl.ex.examples.night_city.app.ManagedCityStory

Root and reactor each pass 760 root tests; independent and reactor consumers each pass seven. All failures, errors and
skips are zero. No disabled/ignored annotations or disabled boolean tests were found. Failsafe selects zero cases;
acceptance runs in Surefire. Benchmark tests remain explicitly profile-selected, not silently disabled acceptance.
Checkstyle reports zero errors; PMD reports zero violations. Actual JaCoCo XML is present: instructions 35,549/45,996
(77.29%), branches 2,347/3,268 (71.82%), lines 7,503/9,735 (77.07%). Zero configured thresholds and these overall
results
do not establish the aspirational 89% repository coverage target. ClassValue implementation is 100% instruction/branch
covered; binding is 90.91% instruction/100% branch; operational builder 97.80% instruction/93.75% branch; factory 87.60%
instruction/87.50% branch. The new MemoException snapshot constructor is covered, while its unchanged legacy
constructors
have lower overall coverage. [Exact verification data][verification] retains counters, selected-class coverage and JAR
hashes. No coverage data or required build check is missing.

The first full regression caught two-state exception classification and container identity compatibility; both were
corrected and the full suite rerun. PMD exposed a configuration wrapper that needed original stack preservation; the
wrapper now copies stack/suppressed members while retaining underlying causes. No check was disabled to obtain success.
Local install ran with authorized access to the local Maven repository, following the existing sandbox remedy.

## Example and artifact review

The shared ManagedCityStory runs expected rejection/explicit rollback, seeded fresh success and checkpoint/commit
relocation. The normal JAR (455 entries) and assembled JAR (1,477 entries) contain the new helpers and exclude Night
City,
specification/benchmark tests, JUnit, Mockito, RoaringBitmap and build-tool packages. Legitimate AspectJ runtime types
are not rejected merely because a class name contains Test. The consumer compile tree is Existential 0.0.1 plus
aspectjrt/aspectjweaver 1.9.21, with no root source/test output dependency. Local artifact coordinates are unchanged.

## Design obligations

Correctness has direct positive/negative acceptance and retained V1 suites. Actionable errors use stable category,
escaped detail and targeted troubleshooting remedies; Java catch hierarchies remain exact. Recovery is explicit:
rejected memo registration preserves existing state, failed boundaries remain rollback-only, and failed shutdown
remains draining/retryable without automatic rollback. Ownership tests inspect caches, released views/captures and
independent instances deterministically; throughput does not prove ownership or GC. Performance includes setup,
complete success, maintenance/cardinality, rejection, explicit cleanup and fresh recovery, with exact callback counts.

## Performance and resources

[The paired closeout record][performance] retains 12 fresh-JVM runs (three required plus three investigation pairs),
48 measured workflow rows, all logs and source identities. ClassValue avoids the strong global Class maps and removes
about 1.60 MB of allocation per 100,000 inferred-type hits in these samples. Successful workflow timing varies and a
small cost remains possible; no stable speedup/regression magnitude or numerical gate is claimed. Memo snapshots and
first detailed rendering have explicit failure-path costs. Setup and rendering modes are separate; allocation is created
bytes, not retained heap. [Earlier rule diagnostics][diagnostics] and [transaction guards][state] retain their own
limits.

## Handoff and retrospective

Final producer audit adds metadata to the defensive checked rollback-handler wrapper and directly verifies its stock
catch type, original cause and released handle. This rollback-only branch is outside the benchmark scenarios; the
manifest preserves measured source hashes separately from final verified hashes. Full builds were rerun after review.

The metadata TODO and approved suggestion are archived as implemented for the finalized public-library semantic scope.
The backlog, normative topic marks, traceability, AutomationFocus and V2 handoff reflect completion. V2 still needs its
separate feature-free Sprint 10 audit, runtime/dependency/coverage review and actual release decision. No remote
publication or new branch was created. The agent did not stage or commit; the shared workspace commit is preserved. The main lesson is to preserve occurrence/owner boundaries:
metadata must not rewrite a shared definition or root a reported payload, and exception class alone cannot distinguish
ordinary two-state rejection from callback failure.

[values]: ../../../../src/test/java/com/taitl/specs/library_usage/UserCanPassAdditionalValues.java
[rules]: ../../../../src/test/java/com/taitl/specs/library_usage/UserCanReuseSuppliedRules.java
[owners]: ../../../../src/test/java/com/taitl/specs/library_usage/UserCanReleaseSharedCaches.java
[errors]: ../../../../src/test/java/com/taitl/specs/library_usage/UserCanDiagnoseOperationalFailures.java
[selection]: ../../../../src/test/java/com/taitl/ex/logic/execution/RuleSelectionTest.java
[reuse]: ../../../../src/test/java/com/taitl/specs/library_usage/UserCanReuseRuleSelections.java
[managed]: ../../performance/runs/20261026T062911Z.ManagedIndexes.md
[performance]: ../../performance/runs/20261027T202251Z.Sprint09Closeout.md
[verification]: ../../performance/runs/20261027T202251Z.Sprint09Closeout.verification.json
[diagnostics]: ../../performance/topics/Diagnostics.md
[state]: ../../performance/topics/TransactionDiagnostics.md
[publication]: ../../design/Distribution.design.md#sprint-09-publication-readiness
