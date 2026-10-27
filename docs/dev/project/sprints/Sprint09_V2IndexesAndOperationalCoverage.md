# Sprint 09: V2 Indexes and Operational Coverage

Status: in progress; selected managed-index and known-resource slice complete on 2026-10-25. Epic:
[Indexes and Operational Coverage](../Backlog.md#indexes-and-operational-coverage). Release: [V2](../V2.md).
Design readiness: selected contracts and interfaces finalized. Verification: direct acceptance, installed consumer,
reactor, artifact/resource checks, and three-process performance baseline pass. Broader scope below remains open.

## Goal and user outcome

Use maintained relationships and broader lifecycle/reporting/resource behavior with measured scaling.

## Dependencies and readiness

Review [Sprint 08](Sprint08_V2StagesIntentsAndCustomTransactions.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Implementation waits only for its actual behavioral and
technical dependencies. Select a ready capability using [Process](../../auto/Process.md).

## Governing designs and specifications

- [QuantifiersAndIndexes.spec.md] and [QuantifiersAndIndexes.design.md].
- [TransactionsAndEvaluation.spec.md] and [TransactionsAndEvaluation.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [Housekeeping.spec.md] and [Housekeeping.design.md].
- [Distribution.spec.md] and [Distribution.design.md].

Selected contracts: [managed transaction-local indexes][managed-spec] and
[known-resource shutdown diagnostics](../../specifications/Housekeeping.spec.md#memory-leak-reporting).
The [delivery plan][managed-plan] owns readiness decisions, implementation milestones, and verification status.

## Recorded decisions

- Automatically maintain explicitly declared transaction-local indexes/joins; defer shared mutable entity contents.
- Support read enrollment and seed collections, with independent automatic/read opt-outs at app/global, instance,
  context, transaction, and individual-index scopes. Defaults enable both.
- Use object identity for enrollment and normal key equality for lookup. Single-valued replacement does not resurrect
  a displaced snapshot when the replacement moves or is deleted.
- Allow uniform empty-index construction at begin. Defer population and fold maintenance for proven boundary-only
  consumers; earlier queries flush folded pending changes before returning.
- Keep automatic maintenance separate from user Effects; retain execution-plan and event-folding semantics.
- Report known outstanding resources during failed shutdown without GC-based tracking or a secondary failed registry.

## Readiness and retained scope

Immutable IndexDefinition tokens bind through Tr.index; IndexingPolicy resolves two independent properties at every
selected scope. Seeds use facade bulk operations, and explicit maintenance remains available with automatic mode on/off.
Report-time rules see previously accepted state. Single keys capture replacement history; multi-valued keys and physical
maintenance fold until query/boundary. The [completed delivery plan][managed-plan] records decisions and discoveries.

Standalone SingleIndex/MultiIndex/SingleJoin/MultiJoin remain the compatibility baseline. Existing caller-owned sources
do not become managed by being passed to Exists. Shared persistent indexes, general Transaction definition reuse and
post-begin mutation semantics, richer violation metadata, and remote publication are not settled by this slice.

## Remaining work

- Retain Sprint 08 checkpoint continuation/failure; resolve remaining access events and broader combinations.
- Complete richer violation metadata, description options, cache ownership/invalidation, and global close behavior.
- Record optional remote-publication readiness separately; remote availability is claimed only after actual evidence.

## Acceptance

- Maintained relationships, lifecycle variations, and metadata have direct positive/failure evidence.
- Cache/global resources have defined ownership and demonstrated release/retention behavior.
- Performance includes maintenance and cardinality scaling, rather than lookup-only timing.
- Managed-index acceptance covers policy inheritance, read/seed/manual modes, identity and replacement, mutable keys,
  early/boundary equivalence, folded repeated events, successful checkpoints, failed boundaries, and disposed views.

## Quality and performance

The design must explicitly address correctness, actionable errors, failure recovery, resource hygiene, and performance.
Link decisions and proof obligations for each concern, or justify inapplicability; quality is required in every release.
Measure maintenance, matching, callback, and retained-state costs under the broader workloads. Use
[performance records](../../performance/Readme.md), naming workloads and comparable evidence.

## Demo and validation

The shared ManagedCityStory demonstrates expected rejection/explicit rollback, fresh success, and seeded relocation.
Root acceptance and the standalone consumer call the same configuration/workflow. Run from the repository root:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify
    mvn -f reactor/pom.xml clean install
    mvn -f examples/night_city/pom.xml exec:java \
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.ManagedCityStory

The [performance record](../../performance/runs/20261026T062911Z.ManagedIndexes.md) contains exact benchmark commands,
source/environment identity, three samples, phase/complete-workflow medians and ranges, allocation, and storage graphs.
Automatic folding meets exact update/extraction counts but does not consistently outperform explicit eager maintenance.
No numerical gate is established. Global cache/metadata and broader event guarantees are not inferred from this slice.

## Selected delivery evidence

Direct acceptance: UserCanUseManagedIndexes, UserCanUseManagedJoins, UserCanConfigureManagedIndexPolicies,
UserCanQueryManagedIndexesDuringTransaction, UserCanMaintainManagedIndexesExplicitly, UserCanReleaseManagedIndexes,
UserCanDiagnoseOutstandingResources, and UserCanMaintainWorkingRelationships under
src/test/java/com/taitl/specs/library_usage.
ManagedMembershipTest compares 2,000 randomized changes with early/periodic/end-only queries against an eager reference.

Correctness covers scoped inheritance, seeding/manual operations in both policy modes, identity/replacement, generic
subjects, deferred extraction, repeated checkpoints, early/failing access, both join directions, large groups, and
concurrent independent handles. Error messages identify index/operation/side and link to recovery guidance. Failed
maintenance detaches the handle and rejects further work; explicit cleanup remains available. Deterministic retained
handle/view inspection proves pending and backing entity references are released, without forcing GC. Known-resource
shutdown evidence covers bounded timeout/interruption details and deliberately excludes detached failures.

Final validation on 2026-10-25 used Amazon Corretto 17.0.15, Maven 3.9.10, macOS 15.6.1 aarch64:

- Root `mvn clean install`: BUILD SUCCESS; 551 tests, zero failures/errors/skips. Checkstyle zero violations;
  PMD passed. Failsafe was enabled and selected zero integration tests; acceptance runs in Surefire.
- Independent `mvn -f examples/night_city/pom.xml clean verify`: BUILD SUCCESS; seven consumer tests, zero skips.
  A copy containing only the consumer POM/src under /private/tmp/ex-s09-independent-night-city also passed
  `mvn -f /private/tmp/ex-s09-independent-night-city/pom.xml clean verify dependency:tree -Dscope=runtime`.
  Its runtime tree resolved com.taitl:existential:0.0.1 and AspectJ runtime/weaver only; no root sources/classes.
- Combined `mvn -f reactor/pom.xml clean install`: all three reactor entries SUCCESS; root 551 and consumer seven
  tests passed. The managed application exec command also completed with expected rejection/recovery messages.
- ZIP inspection of target/existential-0.0.1.jar and assembled existential.jar found no Night City, specs, benchmark,
  JUnit, Mockito, RoaringBitmap, or Maven classes. The normal artifact retains existing coordinates.
- JaCoCo XML contains actual data: total line coverage 6,888/9,197 (74.89%), branch 2,062/2,960 (69.66%).
  Managed components cover 731/741 lines (98.65%) and 242/284 branches (85.21%); facade views cover 236/237 lines.
  Existing configured thresholds remain 0.0, so their passing check alone is not a quality claim. Repository coverage
  remains below the aspirational 89% style target; this slice does not claim full-repository coverage closure.
- Source inspection found no @Disabled/@Ignore tests. Benchmark tests remain deliberately selected only by the
  benchmarks profile, whose three separate invocations passed. Markdown local targets/anchors and whitespace pass.

A restricted first install attempt could not write the local Maven repository; rerunning the authorized Maven build
with local-repository write permission succeeded. No code change or disabled check was used to bypass that failure.

## Non-goals

Unapproved dashboard, hot-reload, or specialized optimization features; actual remote publication without authorization.

## Plan and review

The [archived execution plan][managed-plan] records the selected delivery and verification. Remaining sprint work is
listed above; the whole sprint and V2 release remain open. Use the [review template] for their eventual closeout.

[QuantifiersAndIndexes.spec.md]: ../../specifications/QuantifiersAndIndexes.spec.md
[QuantifiersAndIndexes.design.md]: ../../design/QuantifiersAndIndexes.design.md
[TransactionsAndEvaluation.spec.md]: ../../specifications/TransactionsAndEvaluation.spec.md
[TransactionsAndEvaluation.design.md]: ../../design/TransactionsAndEvaluation.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[Housekeeping.spec.md]: ../../specifications/Housekeeping.spec.md
[Housekeeping.design.md]: ../../design/Housekeeping.design.md
[Distribution.spec.md]: ../../specifications/Distribution.spec.md
[Distribution.design.md]: ../../design/Distribution.design.md

[review template]: ../templates/SprintReview.md
[managed-plan]: ../../past/planning/P10252601.Sprint09-managed-indexes-and-diagnostics.plan.md
[managed-spec]: ../../specifications/QuantifiersAndIndexes.spec.md#managed-transaction-local-indexes
