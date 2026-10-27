# Night City Example

Night City demonstrates constraints on cats, mice, houses, and their relationships. Configure rules for a business
operation, begin a transaction, report entity events, and commit to evaluate the applicable rules.

## Source layout

Java sources live in `src/main/java/com/taitl/ex/examples/night_city/`. `model/` defines the entities, `configure/`
shows fluent and instance-based rules, `commands/` and `access/` illustrate application operations, and `data/` provides
shared sample data. `app/CityApp` exposes configuration examples as an application entry point. Some examples still
contain
unfinished illustrations; their presence does not establish supported library behavior.

Maven compiles this tree as additional test sources for the root project. Existing unit tests, specification tests, and
benchmarks use the same examples. The source packages are unchanged; examples are excluded from library JARs. The
independent consumer also provides runnable application entry points.

## Independent Maven consumer and runnable story

Night City has its own `pom.xml` and depends on the installed `com.taitl:existential` artifact. Its sources compile in
the consumer module; the root project separately keeps them as shared test fixtures. The consumer does not inherit the
root POM or use root source/test output.

From the repository root, build and install the library, verify the consumer, and run its noninteractive story:

    mvn clean install
    mvn -f examples/night_city/pom.xml clean verify
    mvn -f examples/night_city/pom.xml exec:java

The story reports an expected rule rejection, explicitly rolls it back, completes a fresh valid creation, and relocates
a cat while preserving its color and destination-dwelling rule. Every transaction and the library instance are closed.
The consumer acceptance test runs the same story. A separate reactor build is available with
`mvn -f reactor/pom.xml clean install`; Maven Central publication is not part of local consumption.

Run the consumer-side workflow measurement with setup excluded from the timed section:

    mvn -f examples/night_city/pom.xml -Dbenchmark.warmup.iterations=50 -Dbenchmark.measure.iterations=250 \
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.NightCityWorkflowBenchmark compile exec:java

## Caller-supplied relationships and evolution

`ConfigureCats.configureRelationships` uses the same birth, location, and color-preserving evolution rules as the
other cat workflow, with an All-Exists constraint over a caller-owned map. Each cat snapshot maps to one selected
dwelling at its location. The library reads that map during validation; it does not populate, maintain, or undo it.

```java
Cat before = new Cat("Black", "Park");
Cat after = new Cat("Black", "Garden");
Map<Cat, Dwelling<Cat, ?>> dwellings = new HashMap<>();
dwellings.put(before, new Dwelling<Cat, Rugs>("Grey", "Park"));
dwellings.put(after, new Dwelling<Cat, Rugs>("Red", "Garden"));
String operation = "/night-city/cats/relocate";
new ConfigureCats().configureRelationships(operation, dwellings);
Tr tr = Ex.begin(operation);
tr.transit(before, after);
tr.commit();
```

A missing map key, null dwelling, mismatched destination, or changed cat color rejects commit. The exception identifies
the operation and failed rules. Retain the `Tr` and explicitly call `rollback()` after rejected commit; a new attempt
uses a fresh transaction. The map and snapshots remain caller-owned. Keep their hash keys stable during validation.
For creation/deletion use `port(null, after)` or `port(before, null)`; Transit requires both snapshots.

`ConfigureCats.configureMembership` demonstrates All-Exists over a caller-supplied collection. Membership uses the
source equality contract. Runtime folding instead identifies repeated events on the same entity instance; distinct
equal snapshots are separate reports. Predicate-bearing Exists receives the stored matching source object.

Executable positive, negative, empty-source, endpoint, recovery, and source-ownership cases are in
[UserCanConstrainEntityRelationships.java][relationships].

[relationships]: ../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java

## Large-transaction workload

The [runtime execution-plan design](../../docs/dev/design/ExecutionPlans.design.md) uses Night City fixtures for
10,000–100,000 reported entities, up to 200 class/generic descriptors, and 100–500 applicable rules. `CityWorkload`
supplies distinct Look entities spanning beings, buildings, houses, dwellings, and collection projections. Shared
configuration and application operations serve root specifications and the independent consumer through the installed
library artifact.

`NightCityExecutionBenchmark` exercises success, rejection with explicit rollback, and fresh recovery. Setup stays
outside timing and every transaction closes. The [performance
topic](../../docs/dev/performance/topics/ExecutionPlans.md)
links three-process scale, immediate, repeated-report, cold/warm selection, allocation, and reachable-cache evidence.
Root and independent-consumer acceptance verify deferred callbacks, exact counts, sparse applicability, and cleanup.
The [archived delivery
plan](../../docs/dev/past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md)
records Sprint 07 completion and preserves stage-policy deferrals. Cache graphs are measured independently from
per-thread allocation; no numerical throughput guarantee is established.

Run it from the repository root with:

    mvn -f examples/night_city/pom.xml \
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.NightCityExecutionBenchmark \
        -Dbenchmark.entities=50000 -Dbenchmark.types=200 -Dbenchmark.rules=256 \
        -Dbenchmark.warmup.iterations=3 \
        -Dbenchmark.measure.iterations=5 compile exec:java

## Many applicable rule patterns

DwellingWorkload supplies distinct Dwelling<Cat,Brick> objects. ConfigureDwellings spreads declarations across
1–32 real subject/event rows, using concrete, wildcard, raw, and upper-bound dwelling types across
Create/CU/CUD/Expression. DwellingOperations reports those objects with their explicit TypeKey. The same fixtures
support root acceptance, standalone NightCityStoryTest, and the many-row performance comparison; entity count,
declaration count, and distinct applicable-row count are independent.

Run root acceptance with:

    mvn -Dtest=UserCanApplyManyRulePatterns test

The case proves deferred checks, declaration order per entity, sixteen-report folding, rejection with explicit
rollback, and a fresh successful attempt. The
[many-row evidence](../../docs/dev/performance/runs/20261023T222929Z.ManyRowBitSet.md)
compares array, BitSet, and Roaring preparation and complete transactions with cleanup. These measurement helpers
are test-only; the normal library artifacts exclude the example and benchmark classes.

## Run the demonstrated scenarios

From the repository root, run the existing context and custom-transaction specifications:

    mvn -Dtest=UserCanConfigureContexts,UserCanConfigureCustomTransactions test

The representative relationship/evolution cases run with:

    mvn -Dtest=UserCanConstrainEntityRelationships,UserCanCreateAndRelocateCats test

Run all examples backed by the test suite with `mvn test`. Run the transaction benchmark with `mvn -Pbenchmarks test`. A
complete clean build is `mvn clean install`.

See [usage](../../docs/Usage.md), the [specification index](../../docs/dev/Specification.md), and
[performance guidance](../../docs/dev/performance/Readme.md) for the supported workflow and measurement details.


## Managed relationships

ManagedCityStory reuses ConfigureManagedRelationships and the normal cat rules to demonstrate seeded dwellings,
automatically maintained relationships, explicit rejection/rollback, fresh success, and relocation through checkpoint
and commit. Every handle and library instance closes on success and failure. Root and independent-consumer acceptance
run the same application operations.

After installing the library, run:

    mvn -f examples/night_city/pom.xml \
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.ManagedCityStory compile exec:java

ManagedRelationshipWorkload supplies equal-but-distinct cats and dwelling groups for maintenance measurements. The
root ManagedIndexBenchmark separates construction, folded reporting, population, warm queries, incremental maintenance,
commit/disposal, success, rejection/rollback, recovery, and a manual control. Setup is excluded; counts and
deterministic
release are checked separately from timing. See the [performance guide](../../docs/dev/performance/Readme.md).
