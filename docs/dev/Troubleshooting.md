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
