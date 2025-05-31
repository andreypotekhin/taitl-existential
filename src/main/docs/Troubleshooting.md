## Troubleshooting

### Problem (PMD): 'Double-brace initialization should be avoided' error
When: Running PMD checks as part of build process  
Error: "[INFO] PMD Failure: [class] :22 Rule:DoubleBraceInitialization Priority:3 Double-brace initialization should be avoided."  
Cause: Default PMD rules frown on double-brace initialization.  
Ref: https://pmd.github.io/pmd/pmd_rules_java_bestpractices.html#doublebraceinitialization
Causing code:  
```java
public void configure()
{
  ex.configure("/api/cats")
      .context(new Context("/api/cats") {{
          enforce(new Invariant<Cat>() {{
              create(c -> "Black".equals(c.color), "Cats are born black");
          }});
          ...
```
Workaround 1: Adjust PMD rules
```
  pmd-ruleset.xml:
	<rule ref="category/java/bestpractices.xml">
		<exclude name="DoubleBraceInitialization" />
```

Workaround 2: Use configure-with-builders style:
```java
  ex.configure("/api/cats")
    .context()
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
       .done()
```
Details: Double-brace initialization creates an anonymous subclass, which is in line 
with the causing code. Double-brace initialization may be overkill when initializing
collections, so it is defaulted in PMD.
