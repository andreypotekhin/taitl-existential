## Existential package

### Purpose
The Existential package (com.taitl.existential) defines end-user interfaces and classes for Existential library.

### Claims
See /doc/Claims.md for the list of library claims.

### Goals
These interfaces and classes serve as DSL for 
1. Configuring Existential library, Contexts and Transactions
2. Beginnging, committing and rolling back an Existential transaction
3. Recording the events in the course of the transaction
4. Automatically executing event handlers as configured in step 1
5. Automatically executing entity validation as configured in step 1

### Implementation
Classes in this package delegate processing to implementation-specific components
located in a separate, non-public package, com.taitl.ex. 
Normally, classes in com.taitl.existential are sufficient for use, unless you are customizing the library.
