## Setup

[To be added]

### IDE

#### IntelliJ IDEA

##### Adjust formatter to ignore chained method identation
By default, Java formatter removes custom indentation on chained calls.
This may get in the way of readability when configuring with builders.
Example of custom indentation:
```java
  ex.configure("/api/cats")
    .context()
       .invariant(Cat.class)
         .create(c -> "Black".equals(c.color), "Cats are born black")
       .done()
```
To avoid automatic removal of indents, adjust formatter settings:
Changes: **Settings | Editor | Code Style | Formatter | Wrapping & Braces | Chained Method Calls | Keep builder method indents **
