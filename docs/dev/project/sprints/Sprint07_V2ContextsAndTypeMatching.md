# Sprint 07: V2 Contexts and Type Matching

Status: complete; implementation and acceptance evidence recorded through 2026-10-23. Dates/capacity: unassigned. Epic:
[Contexts and Type Matching](../Backlog.md#contexts-and-type-matching). Release: [V2](../V2.md).
Design readiness: selected behavior and integration choices are recorded. Verification: root, standalone consumer, and
reactor builds pass; context/type selection, warning configuration, execution plans, bounded descriptor caching, and
representative performance workflows have test-backed evidence. Measurements remain exploratory and establish no
numeric performance gate.

## Goal and user outcome

Apply parent/wildcard rules and generic/subclass/interface matching deterministically, using compiled runtime plans
and EventField selections to support large validation-only transactions efficiently.

## Dependencies and readiness

Review [Sprint 06](Sprint06_V2InstanceIsolationAndConfiguration.md) for actual dependencies and evidence. Allow
independent investigation and design ahead of later sprints. Select a ready capability using
[Process](../../auto/Process.md).

## Governing designs and specifications

- [LibraryAndConfiguration.spec.md] and [LibraryAndConfiguration.design.md].
- [EventsAndConstraints.spec.md] and [EventsAndConstraints.design.md].
- [ExecutionPlans.spec.md] and [ExecutionPlans.design.md].

The [delivery plan] links exact requirements, records decisions, and defines acceptance and evidence collection.

## Remaining work

- [x] Implement root-to-operation parent-level ordering with wildcards before concrete contexts; share it between
  rule indexing and transaction/factory selection. Acceptance verifies overlapping wildcard declaration order.
- [x] Match reflected generic superclass/interface signatures with multi-edge substitution, interface diamonds,
  direct wildcarding, raw matching, and nested invariance; reject unsupported lower-bounded rules and diagnose
  unresolved runtime signatures. Runtime descriptor cache identity includes actual `Class` identity. Legacy public
  TypeKey/EventKey equality and serialized handler keys remain string-based; same-named classes from distinct
  loaders are not promised distinct rule registrations.
- [x] Add global-file/Ex/instance configuration for unresolved-generic warnings. Warning suppression is bounded and
  owned by each evaluation/library instance.
- [x] Compile and bind internal ExecutionPlan objects, extending EventField rather than replacing its selection cache.
- [x] Specialize validation-only reporting while preserving immediate behavior, callbacks, folding, and input errors.
- [x] Build shared Night City workloads up to 100,000 entities, 200 descriptors, and 500 rules.
- [x] Compare sorted arrays, BitSet, and test-scoped RoaringBitmap; bound descriptor-cache growth and verify immutable
  publication, concurrent first use, and class-loader-distinct descriptor keys. Collect complete-workflow, report,
  commit, allocation, callback, and cleanup evidence. Retained heap is constrained structurally by bounded-cache tests;
  no byte-level retained-heap profile or performance gate is claimed.

## Implementation evidence to date

`CityWorkload` provides 200 distinct `Look<T>` descriptors, including domain and collection-projection types.
`NightCityExecutionBenchmark` runs success, rejection with explicit rollback, and recovery with setup outside timing,
and can compare validation-only and immediate effects on the same fixture. Root and installed-consumer acceptance
verify deferred callbacks, exact callback totals, and cleanup. `UserCanApplyInheritedRules` covers generic superclass
substitution, interface diamonds, nested invariance, upper bounds, raw signatures, and lower-bound rejection.
`UserCanValidateLargeTransactions` covers 64-entity and 400-entity behavior and stage specialization. The bounded
4,096-entry descriptor cache verifies immutable reuse, concurrent first publication, eviction, full-name separation,
and same-named runtime classes from distinct class loaders. The TypeKey/EventKey public string representation is
preserved, so class-loader distinction is guaranteed for descriptor-cache identity, not for string-keyed rule
registration.

Three-process performance samples include 10,000 entities/200 descriptors/100 rules, report and commit phase timing,
allocation counters, and a same-fixture immediate comparison. Separate smoke runs cover 100,000 entities and 500
rules. Sorted array, BitSet, and test-scoped RoaringBitmap prototype results are recorded. These are environment-bound
exploratory samples; retained-heap bytes and numerical performance thresholds were not established. On 2026-10-23,
`mvn clean install` passed with 477 tests, zero failures/errors/skips, Checkstyle 0, PMD clean, and JaCoCo checks met
across 248 classes. `mvn -f examples/night_city/pom.xml clean verify` passed its two acceptance tests as a standalone
consumer; `mvn -f reactor/pom.xml clean install` passed all three reactor projects. Artifact scans found no
example/spec/test classes or test dependencies in either the normal or assembled library JAR. The consumer dependency
tree contains the installed library and required AspectJ runtime dependencies, with JUnit test-scoped. The standalone
consumer compiled its own 40 source files and acceptance test without using root source or root class directories.

## Acceptance

- Overlapping rules execute in one specified order reproduced by tests.
- Supported type/hierarchy combinations and unsupported forms have clear positive/negative cases.
- Retained V1 results, diagnostic context, and matching performance remain sound.
- Validation-only reporting performs no validation selection/evaluation before the boundary; custom immediate rules
  still execute while reporting. Selections are shared by signature without caching entity validity.
- Transaction/library cleanup and bounded cache growth have direct assertions; the independent consumer uses the same
  Night City fixtures without introducing a library dependency on the example artifact.

## Quality and performance

The design addresses correctness, actionable errors, failure recovery, resource hygiene, and performance. The recorded
samples measure setup outside timed workflows, reporting, commit, allocation, callbacks, and cleanup. Cold/warm cache
timing and byte-level retained-heap profiling remain limitations; bounded capacity, eviction, concurrency, and
class-loader-distinct cache identity have deterministic unit coverage. RoaringBitmap remains test-scoped and is not
adopted by the library. See the [execution-plan performance topic] and
[performance records](../../performance/Readme.md).

## Demo and validation

Existing tests are starting evidence, not proof of all new acceptance cases. From the repository root:

    mvn test
    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify
    mvn -f reactor/pom.xml clean install

The [delivery plan] records implementation commands and evidence. Root, independent-consumer, and reactor validation
passed; see the performance topic for benchmark commands and measured limitations.

## Non-goals

Custom event registration, lower-bounded generic matching, hot reload, speculative trie/strategy APIs, a public
execution-mode API, a process-wide programmatic defaults registry, and theorem-solving behavior. Broader stage policy
and terminology remain in Sprint 08; this slice preserves existing stage semantics while optimizing their execution.

## Plan and review

The [delivery plan] records accepted behavior, conceptual classes/actions/business rules, staged implementation, and
prototype results. Final choices and limitations are documented there. Review evidence is complete for this sprint;
follow-up profiling can extend the append-only performance records.

[LibraryAndConfiguration.spec.md]: ../../specifications/LibraryAndConfiguration.spec.md
[LibraryAndConfiguration.design.md]: ../../design/LibraryAndConfiguration.design.md
[EventsAndConstraints.spec.md]: ../../specifications/EventsAndConstraints.spec.md
[EventsAndConstraints.design.md]: ../../design/EventsAndConstraints.design.md
[ExecutionPlans.spec.md]: ../../specifications/ExecutionPlans.spec.md
[ExecutionPlans.design.md]: ../../design/ExecutionPlans.design.md
[delivery plan]: ../../past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md
[execution-plan performance topic]: ../../performance/topics/ExecutionPlans.md

[review template]: ../templates/SprintReview.md
