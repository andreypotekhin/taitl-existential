# Troubleshooting

### Problem (Initialization order): `NullPointerException` during startup
When: Running tests or app startup after adding or changing constructor collaborators.
Error example:
```
Could not create an instance of class com.taitl.ex.core.existential.ExistentialConfigs
Caused by: java.lang.NullPointerException: ... \"this.ec\" is null
```
Cause: A collaborator is created in a field initializer before constructor dependencies are assigned.
Fix: Move collaborator creation from field initializers into the constructor, after dependency assignment.
Example fixes in this project:
- `ExistentialConfigs`: initialize `ConfigurationLogic` in constructor
- `ConfigurationLogic`: initialize action delegates in constructor
- `EventLogic`: initialize `ReceiveEvent` in constructor after `ev`/`ex` are assigned

### Problem (PMD): 'Double-brace initialization should be avoided' error
When: Running PMD checks as part of the build process.
Error: "[INFO] PMD Failure: [class] :22 Rule:DoubleBraceInitialization Priority:3 Double-brace initialization should be
avoided."
Cause: Default PMD rules flag double-brace initialization.
Reference: https://pmd.github.io/pmd/pmd_rules_java_bestpractices.html#doublebraceinitialization
Causing code:
```
public void configure()
{
  Ex.configure("/api/cats")
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
  Ex.configure("/api/cats")
    .context()
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
       .done()
```
Details: Double-brace initialization creates an anonymous subclass, which is in line with the code above. It is often
overkill for collections, so PMD flags it by default.
