## Setup

### Prerequisites
- JDK 17 or higher
- Maven command

### Obtaining and building 
- Clone the repository from GitHub
- Build with Maven command

### IDE
#### IntelliJ IDEA

##### Adjust formatter to ignore chained method identation
By default, the Java formatter removes custom indentation on chained calls.
This can reduce readability when configuring with builders.
Example of custom indentation:
```
  Ex.configure("/api/cats")
    .context()
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
       .done()
```
To avoid automatic removal of indents, use auto-formatter switch comment around the code:
- Before code section: // @formatter:off 
- After code section: // @formatter:on
