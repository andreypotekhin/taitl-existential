# Night City Example

Night City demonstrates constraints on cats, mice, houses, and their relationships. Configure rules for a business
operation, begin a transaction, report entity events, and commit to evaluate the applicable rules.

## Source layout

Java sources live in `src/main/java/com/taitl/ex/examples/night_city/`. `model/` defines the entities, `configure/`
shows fluent and instance-based rules, `commands/` and `access/` illustrate application operations, and `data/`
provides shared sample data. `tests/CityTests` is a configuration fixture, not a JUnit test suite.
Some examples still contain unfinished illustrations; their presence does not establish supported library behavior.

Maven compiles this tree as additional test sources for the root project. Existing unit tests, specification tests,
and benchmarks use the same examples. The source packages are unchanged; examples are excluded from library JARs.
This directory is not a separate Maven module and has no standalone application entry point.

## Run the demonstrated scenarios

From the repository root, run the existing context and custom-transaction specifications:

    mvn -Dtest=UserCanConfigureContexts,UserCanConfigureCustomTransactions test

Run all examples backed by the test suite with `mvn test`. Run the transaction benchmark with
`mvn -Pbenchmarks test`. A complete clean build is `mvn clean install`.

See [usage](../../docs/Usage.md), the [specification index](../../docs/dev/Specification.md), and
[performance guidance](../../docs/dev/performance/Readme.md) for the supported workflow and measurement details.
