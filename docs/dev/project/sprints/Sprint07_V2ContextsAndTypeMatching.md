# Sprint 07: V2 Contexts and Type Matching

Status: complete on 2026-10-23, including the many-row representation follow-up.
Epic: [Contexts and Type Matching](../Backlog.md#contexts-and-type-matching). Release: [V2](../V2.md).
Design readiness: selected behavior and internal ownership are resolved. Verification: direct acceptance, required
builds/checks, installed-consumer isolation, artifact hygiene, and comparable performance/resource evidence pass.

## Goal and user outcome

Applications apply parent/wildcard rules and supported generic/subclass/interface rules in deterministic order.
Large validation-only transactions record/fold events before their boundary checks; entities share handler selection
without sharing predicate outcomes or memo payloads. Explicit immediate behavior retains its timing.

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [ExecutionPlans.spec.md] and [ExecutionPlans.design.md].

The archived [delivery plan] records decisions, implementation, discoveries, and the earlier reopening. The first
closeout was premature: stage specialization and a representation prototype had not delivered the agreed matching
path. This record closes that gap with implemented behavior and final evidence.

## Completion of the four agreed actions

1. Compared actual Night City descriptor/declaration membership using sorted arrays, BitSet, and test-scoped
   RoaringBitmap 1.6.8. Construction, prepared union/intersection, ordered materialization, allocation, and reachable
   row graphs are separate. Three fresh processes rotate candidate order; outputs escape and are checked.
   Sparse, broad, clustered, overlapping, upper-bound, and separately labelled larger-ID stress cases are recorded.
   The follow-up proves 1–32 distinct applicable rows and compares native materialization directly. BitSet now
   handles multirow combination; a single row reuses precompiled handlers. Warm execution uses immutable arrays.
2. Moved compact-signature EventField lookup ahead of event/type expansion and MultiKey construction. The signature
   distinguishes class identity, declared descriptor, event shape, naming/splitting mode, and memo availability;
   the owner supplies operation/stage and inventory generation. Warm hits reuse selections while checking each
   entity's memo/payload requirements separately.
3. Integrated compact declaration IDs, raw-type pattern buckets, independent structural upper-bound checks, and
   ordered BitSet combination with EventField and transaction-owned overlays. Local changes replace their generation.
   Shared/overlay selections are bounded at 1,024, operation inventories/profiles at 128, warning suppression at 64,
   and legacy descriptor expansion at 4,096. Lazy storage, empty-stage traversal avoidance, and backing-table disposal
   reduce transaction overhead. Deletion-port identity now uses the available endpoint.
4. Verified correctness, concurrency, failure retry, recovery, cleanup, cache churn, performance/resources, independent
   consumption, reactor execution, and artifact hygiene. Expanded class-level JavaDocs across 46 non-trivial internal
   components and measurement helpers. Final commands and evidence are below.

## Direct behavior evidence

`UserCanConfigureContexts` verifies deterministic parent/wildcard/concrete ordering. `UserCanApplyInheritedRules`
verifies generic superclass/interface substitution, diamond deduplication, nested invariance, independent and
parameterized upper bounds, lower-bound rejection, and unresolved-generic warning/opt-out behavior. Public TypeKey
and EventKey registration equality remains string-based; descriptor/signature caches additionally preserve actual
Class identity across loaders.

`RuleSelectionTest` verifies warm lookup before expansion, declaration identity/order, immutable reuse, failed misses,
concurrent first publication, bounded empty selections, mode/endpoint separation, local generation invalidation,
class-loader identity, and matching without wildcard products. `UserCanReuseRuleSelections` verifies fresh values at
repeated checkpoints and across transactions, per-entity before-state, operation eviction/recomputation, deletion
identity, and shutdown release. `TypeMatchingLogicTest` verifies the whole-instance warning budget and close.

`UserCanValidateLargeTransactions` verifies validation-only deferral, transaction-local immediate fallback, distinct
equal entities, sixteen repeated reports, and 400 entities across 200 descriptors. Independent `NightCityStoryTest`
reuses fixtures through the installed artifact and includes sparse 100-rule applicability across those descriptors.
UserCanApplyManyRulePatterns verifies 1/2/3/4/8/16/20/32 actual dwelling pattern/family rows, deferred callbacks,
exact declaration order for each entity, sixteen-report folding, negative rules, explicit rollback, and fresh recovery.
RuleMembershipTest verifies read-only prepared rows, private concurrent accumulators, empty/one-row handling, native
materialization, and public collection copying. RuleSelectionTest verifies cross-signature single-row reuse.
Installed NightCityStoryTest covers the same dwelling fixture at 1/20/32 rows. Existing transaction failure/recovery,
callback, rollback, and shutdown tests remain green.

## Build and artifact evidence

Exact commands, from the repository root on Amazon Corretto OpenJDK 17.0.15+6-LTS, Maven 3.9.10, macOS 15.6.1 arm64:

    mvn -o clean install
    mvn -o -X -f examples/night_city/pom.xml clean verify
    mvn -o -f reactor/pom.xml clean install
    mvn -o -f examples/night_city/pom.xml dependency:tree

Root: 509 tests, zero failures/errors/skips; Checkstyle 0, PMD clean. JaCoCo produced actual execution data, XML/HTML,
and passing configured checks across 271 classes. Final reactor coverage totals: 27,511 covered instructions of
38,252 (71.9%), 5,825 lines of 8,131 (71.6%), and 1,676 branches of 2,522 (66.5%). No @Disabled/@Ignore cases were
found.
Standalone consumer: 6 tests, zero failures/errors/skips; compiled its own 43 production sources and one test source.
Debug compiler/test classpaths contain the installed existential JAR and AspectJ 1.9.21, with no root source/class
folders. Reactor: all three projects pass, repeating the 509 root and 6 consumer tests.

Normal `target/existential-0.0.1.jar` and assembled `existential.jar` contain no Night City, root test/spec/benchmark,
JUnit, Mockito, RoaringBitmap, or build-tool classes. Dependency-tree inspection shows the library plus required
AspectJ runtime dependencies, with JUnit test-scoped and no RoaringBitmap transitively. Local installation does not
publish artifacts externally. The [final run record] retains artifact sizes/digests and focused verification evidence.

## Performance and resource evidence

The [execution-plan performance topic] links [the many-row follow-up][final run record], individual samples,
source identities, methods, and exact commands. Arrays/BitSet/Roaring native construction, union, and final selection
are separate; three processes rotate candidate order. The earlier [Compiled Rule
Selections](../../performance/runs/20261023T215346Z.CompiledRuleSelections.md) retains scale/immediate/repeated,
200/500-signature bounds, and original production integration evidence.

The previous Night City comparison selected zero/one row or averaged 1.34 overlapping rows. The dwelling fixture
now proves up to 32 distinct rows across generic subjects and applicable event families. At 256 declarations,
20/32-row native union plus final selection improves from 278.5/406.3 to 89.2/91.3 µs per 64 selections. Grouped
layout and original twenty-row synthetic controls confirm the many-row opportunity. Two-row arrays can be faster;
production retains one membership representation and bypasses materialization for a single matching pattern.

Paired actual-library probes show lower fresh-selection cost/allocation; complete 10,000-entity success, rejection,
rollback/recovery, repeated-report, and original Look workflow ranges overlap. No universal transaction speedup is
claimed. Precompiled single-row handlers slightly increase multirow holder graphs; all closed fields have zero
cached selections. Graphs exclude class/static/JVM metadata and weak referents and are not exclusive retained heap.
No numerical performance gate or application throughput guarantee is established.

## Remaining scope

The many-row comparison, optimized native materialization, representation decision, and final verification are
complete. Earlier passing evidence remains historical. Broader stage policy, defaults, authorization,
intent/effect ordering, and callback semantics remain in [Sprint 08](Sprint08_V2StagesIntentsAndCustomTransactions.md).
Custom events belong to V3. Lower bounds, hot reload, speculative strategy/trie APIs, a public execution-mode API,
and transaction-ID-less/thread-local event APIs remain outside this slice. Existing future proposals retain their
backlog review status rather than inheriting completion from this sprint.

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[ExecutionPlans.spec.md]: ../../specifications/ExecutionPlans.spec.md
[ExecutionPlans.design.md]: ../../design/ExecutionPlans.design.md
[delivery plan]: ../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md
[execution-plan performance topic]: ../../performance/topics/ExecutionPlans.md
[final run record]: ../../performance/runs/20261023T222929Z.ManyRowBitSet.md
