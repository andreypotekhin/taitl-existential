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

## Access-event coverage and general On correction

The second selected slice verifies read, read-and-lock, write, and explicit access across stages, policy modes, entry
points, and recovery. The [delivery plan](../../past/planning/P10262601.Sprint09-access-event-acceptance.plan.md)
records
completed verification. Acceptance exposed missing general On dispatch/authorization and event-family intent
ordering; the correction reuses EventField ownership. All 54 direct cases and final verification pass.
No new public APIs, storage locks, or inferred mutation events are added.

Verification on 2026-10-26: `mvn clean install` passes 605 tests, no failures/errors/skips, zero Checkstyle violations,
and passing PMD. `mvn -f examples/night_city/pom.xml clean verify` passes seven tests; the reactor clean install passes
all modules and the same root/consumer counts. Normal and assembled artifacts exclude examples, specs, benchmarks,
JUnit, Mockito, RoaringBitmap, and Maven classes. Actual final reactor JaCoCo coverage is 7,027/9,239 lines (76.06%) and
2,136/2,992 branches (71.39%); existing zero thresholds are unchanged. No disabled tests were found; Failsafe selected
zero cases, with acceptance running in Surefire. Local Markdown targets/anchors and whitespace pass.

The [access-event comparison](../../performance/runs/20261027T050744Z.AccessEvents.md) compares three fresh-JVM typed
controls against the prior revision and separately measures corrected general rules. Restoring skipped callbacks is
not a comparable speed measurement. Repeated-report intent ordering adds measured overhead and remains a target;
no numerical gate is introduced. See
[T10262601](../../past/todo/T10262601.General-on-rules-skipped-by-event-dispatch.md).

## Required-description option coverage

Selected next on 2026-10-26: verify the settled prospective flag behavior across fluent context/transaction builders,
event checks, intents, effects, and caller-owned/managed quantifiers. Add correction guidance to configuration errors.
Acceptance exposed missing effect enforcement and rejected optional quantifier descriptions, tracked in
[T10262602](../../past/todo/T10262602.Required-description-policy-gaps.md). Effect now has the same prospective
requireDescriptions control as other rule sets. The fixes preserve flag timing and add no runtime hot-path checks.
All 33 targeted cases pass. Root `mvn clean install` passes 638 tests, standalone consumer clean verify passes seven,
and the reactor clean install passes all modules, with zero failures/errors/skips. Checkstyle reports zero violations
and PMD passes. Final reactor JaCoCo has 7,068/9,245 lines (76.45%) and 2,152/2,992 branches (71.93%); configured zero
thresholds are unchanged and do not establish full-repository coverage closure. Failsafe selects zero integration
cases; acceptance runs in Surefire. No disabled tests were found. Both library artifacts pass hygiene checks, and
Markdown links/anchors and whitespace pass. Standalone-object ownership and
structured-diagnostic implementation remain separate.

The workload evidence convention now uses ms/workflow and decimal MB, beginning with the access-event record. The
harness
prints those units for future runs and the guide/template use them. Earlier
reports remain unchanged; the current report was converted without rerunning measurements.

## Structured diagnostics delivery

Selected on 2026-10-26: Rust-inspired rule diagnostics with structured fields and detailed multiline getMessage()
by default. The [design](../../design/EventsAndConstraints.design.md#structured-violation-diagnostics) and
[specification](../../specifications/EventsAndConstraints.spec.md#structured-violation-diagnostics) identify rules by
their existing descriptions, with unprefixed library codes identifying failure categories. Field order is operation,
rule, context, event, evaluation, then optional notes/actions. Context preserves parent/wildcard declaration origins.
Shared report state appears once; obvious stock recovery advice is omitted.

The [delivery plan](../../past/planning/P10262602.Sprint09-structured-diagnostics.plan.md) now implements immutable
Diagnostic snapshots, five stable categories, occurrence-based provenance in shared/local selections, actual rejected
intent details, callback normalization, lazy multiline rendering, and aggregate state notes. Caller-supplied exceptions
remain causes of fresh stock exceptions; library failures preserve underlying causes/stacks/suppressed members.
No separate caller IDs or new declaration syntax was added.

UserCanDiagnoseRuleViolations supplies 29 direct cases; DiagnosticLogicTest adds eight focused cases. Checks include
combined selections and full names, reused local handlers, stage/boundary distinctions, explicit rollback semantics,
concurrent reuse of a supplied exception, detached local capture/index cleanup, serializable snapshots, immutable
lists, escaped raw descriptions, no entity rendering, fallback, and memoization.

[Performance evidence](../../performance/topics/Diagnostics.md) retains three independent JVM samples per variant
with identical harnesses and source hashes. All 336 measured scenario samples assert callbacks, violations, folding,
and cleanup. Initial smaller repeated-report medians were about 15% slower; interleaved follow-ups show common timing
drift,
so a small success-path time cost remains possible. Warm allocation falls about 2.4 MB per 50,000-entity workflow.
Detailed failure output has an explicit allocation/rendering tradeoff. No speedup, numerical gate, or arbitrary
leak-detection guarantee is claimed.

Design readiness and selected implementation verification are complete. Broader Sprint 09 work remains open.

## Remaining work

- Additional event values and remaining broader combinations beyond the verified access/general-rule slice.
- Extend metadata to remaining public-exception paths and settle supplied-rule binding, cache ownership/invalidation,
  and global close behavior.
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

## Structured diagnostics verification

On 2026-10-27, `mvn clean install` passed 676 root tests with no failures/errors/skips; Checkstyle and PMD passed.
The independent `mvn -f examples/night_city/pom.xml clean verify` passed seven tests against the installed artifact.
The final reactor result and coverage/artifact inspection are recorded in the archived delivery plan. JaCoCo data is
present; the new diagnostics implementation has 95.2% instruction coverage and the public diagnostic catalog 100%.
Overall coverage remains lower, so passing zero-threshold checks is not treated as repository-wide coverage closure.
No disabled test annotations were found. The normal/assembled JAR inspections exclude application fixtures, test
frameworks, test/benchmark suites, and build-tool packages; legitimate AspectJ runtime AST types named Test are
retained.
The consumer dependency tree remains scoped to the library, AspectJ runtime/weaver, and test-scoped JUnit.
