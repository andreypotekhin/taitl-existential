## Style Guide

### Priorities
Coding priorities:
- Convenience of human code reader
- Convenience of library human end-user
- Performance where it matters

### Coding Style
General rules:
- Follow the surrounding project's style on coding, decomposition, documentation, etc.
- Refactor with minimal code
- Move general (non-specific to project business logic/reusable) code into separate components, 
e.g. by adding to ex.common.helper 

### Naming
#### Naming - Identifiers
Avoid abbreviations in identifiers, with an exception for well-known and widely accepted
ones when used in compound identifiers, such as 'Doc' for document. Do not use
vowel dropping, and also limit the use of numbered identifiers.
Avoid abbreviations in single-word identifiers.
In math-like contexts, e.g. around looping, use single-character identifiers for brevity.

#### Naming - Loops
Prefer single-word identifiers for the 'for' loop variables.
Prefer single-character identifiers for loop counters and other math-like variables.

#### Naming - Abbreviations
In compound identifiers, do not convert all-capital abbreviations (HTML) to camel-case (Html).

### OOP
Instantiation
- Objects are normally instantiated with Creator.create()
- Singletons are maintained with Creator.singleton()
- For dependency injections, one can use Creator.inject()
- Some classes (All, Exists) are instantiated with 'new' by end-user. This is part of library contract.
Since this precludes from using Creator.create(), we have a workaround: these classes delegate to
a corresponding concrete implementation (e.g. ConcreteExists); and Creator is used to instantiate/inject 
these concrete implementations.

Inheritance
- Avoid deep inheritance chains
- We do not use 'final' or 'locked'  

Object and package decomposition
- We divide the classes into 'public', 'orchestration' and 'logic' classes
- The 'public' classes are the onece facing end-user classes from 'public' packages (com.taitl.existential)
- The 'orchestration' classes are top-level classes to which the public classes delegate. Example: ConfigBuilder 
- The 'logic' classes implement business logic. Example: BuildConfigs
- The logic classes are characterized by
  - Action-oriented name (verb+noun) (BuildConfigs instead of ConfigBuilder)
  - Focus on a single task
  - Less reliance on state: most methods can be thought of as 'static', even if they formally aren't,
  and the context is normally passed by method parameters.
  - Belong to 'logic' packages (com.taitl.ex.logic)
  - Deep on implementation details
- The 'logic' packages are thought of semi-autonomous 'apps':
  - Define own subpackage. Example: com.taitl.ex.logic.configuration, com.taitl.ex.logic.validation
  - Implement business logic as 'actions' (.actions subpackage), mappings (.maps subpackage),
    data model (.data subpackage), business rules (.rules subpackage), outputs (.output subpackage)
  - Integrate with other 'apps' using their corresponding data model structures 

### What to avoid
As a principled org, we fight a few dogmas.

We generally avoid, unless there is a valid reason:
- non-wildcard imports 
- 'final' keyword
- reference syntax in lambdas 
- 'private' access modifier (prefer default or protected)
- Optionals
- @Override annotation 
- reflection
- overuse of streams
- HTML tags in Javadocs
- use 'brief' notation for getters and setters (x() instead of getX())