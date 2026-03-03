## Setup

### Prerequisites
- JDK 17 or higher
- Maven

### Obtaining and building
- Clone the repository from GitHub.
- Build with Maven: `mvn test`.

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
       .done()
```
To preserve indentation, wrap the section with formatter switch comments:
- Before the code section: `// @formatter:off`
- After the code section: `// @formatter:on`
