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

## Planned large-transaction workload

The [runtime execution-plan design](../../docs/dev/design/ExecutionPlans.design.md) selects Night City as the shared
fixture source for 10,000–100,000 reported entities, 20–200 class/generic descriptors, and 100–500 rules. Model,
data,
and configuration extensions will be reused by specifications and the independent consumer. Validation-only reporting,
first validation, warmed transactions, immediate-stage comparisons, and resource evidence are covered by the
[delivery plan](../../docs/dev/past/planning/P10222607.Sprint07-contexts-type-matching-and-execution-plans.plan.md) and
[performance topic](../../docs/dev/performance/topics/ExecutionPlans.md). `CityWorkload` supplies distinct entities
across 200 real `Look<T>` descriptors, including beings, buildings, houses, dwellings, and collection projections. The consumer-side
`NightCityExecutionBenchmark` exercises success, rejection with rollback, and rejection/recovery across that
population. Setup and fixture creation remain outside timing, and each transaction is closed. The fixture supports up
to 200 descriptors. Root and independent-consumer acceptance verify all 200 descriptors, deferred callbacks, and
cleanup. A 100,000-entity/200-descriptor smoke, a 500-rule smoke, three 10,000-entity/200-descriptor workflow samples,
phase samples, and per-thread allocation samples are recorded in the
[execution-plan performance topic](../../docs/dev/performance/topics/ExecutionPlans.md). Byte-level retained-heap
profiling remains uncollected; bounded cache capacity and eviction are verified by tests.

Run it from the repository root with:

    mvn -f examples/night_city/pom.xml \
        -Dexec.mainClass=com.taitl.ex.examples.night_city.app.NightCityExecutionBenchmark \
        -Dbenchmark.entities=50000 -Dbenchmark.types=200 -Dbenchmark.rules=256 \
        -Dbenchmark.warmup.iterations=3 \
        -Dbenchmark.measure.iterations=5 compile exec:java

## Run the demonstrated scenarios

From the repository root, run the existing context and custom-transaction specifications:

    mvn -Dtest=UserCanConfigureContexts,UserCanConfigureCustomTransactions test

The representative relationship/evolution cases run with:

    mvn -Dtest=UserCanConstrainEntityRelationships,UserCanCreateAndRelocateCats test

Run all examples backed by the test suite with `mvn test`. Run the transaction benchmark with `mvn -Pbenchmarks test`. A
complete clean build is `mvn clean install`.

See [usage](../../docs/Usage.md), the [specification index](../../docs/dev/Specification.md), and
[performance guidance](../../docs/dev/performance/Readme.md) for the supported workflow and measurement details.
