## Setup

### Prerequisites
- JDK 17 or higher
- Maven

### Obtaining and building
- Clone the repository from GitHub.
- Build with Maven using `mvn test`.

### IDE
#### IntelliJ IDEA

##### Adjust formatter to ignore chained method indentation
By default, the Java formatter removes custom indentation on chained calls.
This can reduce readability when configuring with builders.
Example of custom indentation:
```java
  Ex.configure("/api/cats")
    .context()
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
       .done()
```
To avoid automatic removal of indents, use formatter switch comments around the code:
- Before the code section: `// @formatter:off`
- After the code section: `// @formatter:on`
