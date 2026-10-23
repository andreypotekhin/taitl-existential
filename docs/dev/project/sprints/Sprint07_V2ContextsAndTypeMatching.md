# Sprint 07: V2 Contexts and Type Matching

Status: complete on 2026-10-23, including the reopened production-matching continuation.
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
   Sorted arrays are the competitive simpler choice; every warm path uses immutable ordered handler arrays.
2. Moved compact-signature EventField lookup ahead of event/type expansion and MultiKey construction. The signature
   distinguishes class identity, declared descriptor, event shape, naming/splitting mode, and memo availability;
   the owner supplies operation/stage and inventory generation. Warm hits reuse selections while checking each
   entity's memo/payload requirements separately.
3. Integrated compact declaration IDs, raw-type pattern buckets, independent structural upper-bound checks, and
   ordered merges with EventField and transaction-owned overlays. Local changes replace their generation.
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
Existing transaction failure/recovery, callback, rollback, and shutdown tests remain green.

## Build and artifact evidence

Exact commands, from the repository root on Amazon Corretto OpenJDK 17.0.15+6-LTS, Maven 3.9.10, macOS 15.6.1 arm64:

    mvn -o clean install
    mvn -o -X -f examples/night_city/pom.xml clean verify
    mvn -o -f reactor/pom.xml clean install
    mvn -o -f examples/night_city/pom.xml dependency:tree

Root: 495 tests, zero failures/errors/skips; Checkstyle 0, PMD clean. JaCoCo produced actual execution data, XML/HTML,
and passing configured checks across 271 classes. Final reactor coverage totals: 27,472 covered instructions of
38,212 (71.9%), 5,814 lines of 8,128 (71.5%), and 1,685 branches of 2,526 (66.7%). No @Disabled/@Ignore cases were
found.
Standalone consumer: 3 tests, zero failures/errors/skips; compiled its own 40 production sources and one test source.
Debug compiler/test classpaths contain the installed existential JAR and AspectJ 1.9.21, with no root source/class
folders. Reactor: all three projects pass, repeating the 495 root and 3 consumer tests.

Normal `target/existential-0.0.1.jar` and assembled `existential.jar` contain no Night City, root test/spec/benchmark,
JUnit, Mockito, RoaringBitmap, or build-tool classes. Dependency-tree inspection shows the library plus required
AspectJ runtime dependencies, with JUnit test-scoped and no RoaringBitmap transitively. Local installation does not
publish artifacts externally. The [final run record] retains artifact sizes/digests and focused verification evidence.

## Performance and resource evidence

The [execution-plan performance topic] links [the final comparison][final run record], all individual samples, source
identities, methods, and exact commands. Three-process primary success/recovery comparisons, comparable baseline and
candidate cold/warm selection probes, 50,000/100,000-entity scale cases, immediate effects, and sixteen-report workflows
are recorded. Selection profiles assert 200/500 signatures, churn to the finite bound, no fixture payloads in shared
cache graphs, empty disposed event indexes, and zero cached selections after library close.

Arrays are competitive for compact membership; BitSet saves broad-row storage and Roaring wins some combination
phases. Warm execution is identical across alternatives. Short rejection samples were slower; a longer paired probe
had overlapping ranges and lower allocation, leaving rejection latency inconclusive. Reachable-object graphs exclude
class/static/JVM metadata and weak referents; they are not exclusive retained heap. No numerical performance gate or
application throughput guarantee is established.

## Remaining scope

No selected Sprint 07 implementation or evidence item remains open. Broader stage policy, defaults, authorization,
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
[final run record]: ../../performance/runs/20261023T215346Z.CompiledRuleSelections.md
