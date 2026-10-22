# Night City Example

Night City demonstrates constraints on cats, mice, houses, and their relationships. Configure rules for a business
operation, begin a transaction, report entity events, and commit to evaluate the applicable rules.

## Source layout

Java sources live in `src/main/java/com/taitl/ex/examples/night_city/`. `model/` defines the entities, `configure/`
shows fluent and instance-based rules, `commands/` and `access/` illustrate application operations, and `data/` provides
shared sample data. `tests/CityTests` is a configuration fixture, not a JUnit test suite. Some examples still contain
unfinished illustrations; their presence does not establish supported library behavior.

Maven compiles this tree as additional test sources for the root project. Existing unit tests, specification tests, and
benchmarks use the same examples. The source packages are unchanged; examples are excluded from library JARs. This
directory is not a separate Maven module and has no standalone application entry point.

## Planned independent Maven project

[Night City Artifact Consumption and Performance] will give Night City its own `pom.xml`, depending on the installed
Existential artifact. Build/install the library first, then verify the example project against that dependency, using
its own sources and public library APIs. Root
library tests can retain the current shared-fixture source arrangement. The independent POM/demo is planned, not yet
present; no Maven Central publication is needed for local consumption.

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
[UserCanConstrainEntityRelationships.java](../../src/test/java/com/taitl/specs/library_usage/UserCanConstrainEntityRelationships.java).

## Run the demonstrated scenarios

From the repository root, run the existing context and custom-transaction specifications:

    mvn -Dtest=UserCanConfigureContexts,UserCanConfigureCustomTransactions test

The representative relationship/evolution cases run with:

    mvn -Dtest=UserCanConstrainEntityRelationships,UserCanCreateAndRelocateCats test

Run all examples backed by the test suite with `mvn test`. Run the transaction benchmark with `mvn -Pbenchmarks test`. A
complete clean build is `mvn clean install`.

See [usage](../../docs/Usage.md), the [specification index](../../docs/dev/Specification.md), and
[performance guidance](../../docs/dev/performance/Readme.md) for the supported workflow and measurement details.

[Night City Artifact Consumption and Performance]:
  ../../docs/dev/project/Backlog.md#night-city-artifact-consumption-and-performance
