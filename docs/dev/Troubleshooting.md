# Troubleshooting

### Problem (Initialization order): `NullPointerException` during startup
When: Running tests or starting the app after adding or changing constructor
collaborators.
Error example:
```
Could not create an instance of class com.taitl.ex.core.existential.ExistentialConfigs
Caused by: java.lang.NullPointerException: ... \"this.ec\" is null
```
Cause: A collaborator is created in a field initializer before constructor
dependencies are assigned.
Fix: Move collaborator creation from field initializers into the constructor,
after dependency assignment.
Example fixes in this project:
- `ExistentialConfigs`: initialize `ConfigurationLogic` in constructor
- `ConfigurationLogic`: initialize action delegates in constructor
- `EventLogic`: initialize `ReceiveEvent` in constructor after `ev`/`ex` are assigned

### Problem (PMD): 'Double-brace initialization should be avoided' error
When: Running PMD checks as part of the build process.
Error: "[INFO] PMD Failure: [class] :22 Rule:DoubleBraceInitialization Priority:3
Double-brace initialization should be avoided."
Cause: Default PMD rules flag double-brace initialization.
Reference: https://pmd.github.io/pmd/pmd_rules_java_bestpractices.html#doublebraceinitialization
Causing code:
```
public void configure()
{
  Ex.configure()
      .context(new Context("/api/cats") {{
          invariant(new Invariant<Cat>() {{
              create(c -> "Black".equals(c.color), "Cats are born black");
          }});
          ...
```

Workaround 1: Adjust PMD rules.
```
  pmd-ruleset.xml:
    <rule ref="category/java/bestpractices.xml">
        <exclude name="DoubleBraceInitialization" />
```

Workaround 2: Use configure-with-builders style.
```
  Ex.configure()
    .context("/api/cats")
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
```
Details: Double-brace initialization creates an anonymous subclass, which is in
line with the code above. It is often overkill for collections, so PMD flags it
by default.


### Problem (Examples): Night City classes cannot be resolved

When: Tests or benchmarks import Night City after updating the repository layout.
Cause: The IDE or an ad-hoc compiler still treats the former production directory as the example source root.
Fix: Reimport the root Maven project. Build Helper adds `examples/night_city/src/main/java` as a test source during
`generate-test-sources`; run `mvn clean install` from the repository root. Do not copy the examples back into production
sources. The clean build also removes stale example classes from `target/classes` and rebuilt library JARs.
See the [Night City guide](../../examples/night_city/Readme.md).

### Problem (Coverage): Build succeeds but JaCoCo reports missing execution data

When: `mvn clean install` reports a successful build but skips JaCoCo reporting/checking.
Observed output: `Skipping JaCoCo execution due to missing execution data file`.
Cause: The prepared JaCoCo agent is assigned to `argLine`, while Surefire's explicit `argLine` contains only
`--enable-preview`. The test JVM therefore does not collect the intended coverage data.
Remedy: Treat coverage evidence as unavailable, inspect the actual test JVM options, and track the
[coverage repair](suggestions/S10202601.Restore-coverage-evidence.md). The repair must compose agent and JVM options,
then verify that data, a report, and the check are actually produced. Do not infer coverage from passing test totals.

### Problem (Tests): Exact Surefire class filter executes zero nested tests

When using `mvn -Dtest=ExistsTest test`, Surefire 2.22.2 may select no tests when the class's tests are all nested.
Use `mvn '-Dtest=ExistsTest*' test` and inspect the actual count (13 cases at the Sprint 02 baseline). Full-suite discovery
includes these nested cases. Do not set `failIfNoTests=false` to present an empty focused run as passing evidence.

### Problem (Maven): Local installation denied by the execution sandbox

When: tests and verification pass, but `maven-install-plugin` fails with `Operation not permitted` while writing to
`~/.m2/repository`.
Cause: the local Maven repository lies outside the execution sandbox's writable paths.
Fix: rerun the authorized build with permission to write the local repository, or configure `-Dmaven.repo.local` to
an allowed writable directory with the needed dependencies available. Confirm that installation completes; passing
verification alone does not make the failed install successful.

### Problem (Benchmarks): Reachable-memory evidence is unavailable

When: a profile prints `-1` reachable bytes or requests the test-only BenchmarkMemory agent.
Cause: ordinary benchmark execution measures time/allocation without attaching instrumentation. `-1` means unknown,
not zero retained memory. Missing module access can also prevent complete graph traversal.
Fix: follow the [reachable-memory profiling procedure](performance/Readme.md#reachable-memory-profiling), attach the
agent and required module opens, and set `-Dbenchmark.heap.required=true` when collecting heap evidence. The measured
reachable graph excludes class/static metadata and weak referents; it is not exclusive dominator retention.
