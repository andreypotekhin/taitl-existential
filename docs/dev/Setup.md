## Setup

### Prerequisites
- JDK 17 for the currently verified build; compiler release is 11, not proof of Java 11 runtime support.
- Maven

### Obtaining and building
- Clone the repository from GitHub.
- Run tests with `mvn test`; complete a clean build with `mvn clean install`.
- Maven downloads pinned plugins/dependencies and installs artifacts into the local Maven repository.
- Night City is added as a test source automatically; reimport Maven in the IDE after source-layout changes.

### IDE
#### IntelliJ IDEA

##### Adjust formatter to preserve chained-call indentation
By default, the Java formatter removes custom indentation on chained calls.
This can reduce readability when configuring with builders.
Example of custom indentation:
```java
  Ex.configure()
    .context("/api/cats")
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
```
To preserve indentation, wrap the section with formatter switch comments:
- Before the code section: `// @formatter:off`
- After the code section: `// @formatter:on`
