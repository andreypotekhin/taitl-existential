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
- Place general/reusable code into separate components, e.g. by adding classes to ex.common.helper 

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

### What to avoid
We generally avoid, unless there is a valid reason:
- non-wildcard imports 
- 'final' keyword
- reference syntax in lambdas 
- 'private' access modifier (prefer 'protected')
- Optionals
- @Override annotation 
- reflection
- overuse of streams
- HTML tags in Javadocs
