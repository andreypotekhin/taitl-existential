# Style Guide

## Priorities
Coding priorities:
- Convenience of human code reader
- Convenience of human end-user
- Performance where it matters

## Coding

### Coding Style
General rules:
- Follow the surrounding project's style on coding, decomposition, documentation, etc.
- Refactor with minimal code
- Move general (non-specific to project business logic/reusable) code into separate components, 
e.g. by adding to ex.common.helper 
- Less code, less bugs

#### Naming
##### Naming - Identifiers
Avoid abbreviations in identifiers, with an exception for well-known and widely accepted
ones when used in compound identifiers, such as 'Doc' for document. Do not use
vowel dropping, and also limit the use of numbered identifiers.
Avoid abbreviations in single-word identifiers.
In math-like contexts, e.g. around looping, use single-character identifiers for brevity.

##### Naming - Loops
Prefer single-word identifiers for the 'for' loop variables.
Prefer single-character identifiers for loop counters and other math-like variables.

##### Naming - Abbreviations
In compound identifiers, do not convert all-capital abbreviations (HTML) to camel-case (Html).

#### Comments
Javadoc comments
- Avoid HTML formatting tags in Javadocs, such as <p>, <br>, inline {@code}
- In user-facing code packages, use Javadoc comments with parameter, return and throws tags 
- Add Javadoc comments on non-trivival private methods
- In implementation (non end-user-facing) code packages, avoid parameter, return and throws tags
- In implementation code packages, Javadoc comments are more free-form, used to explain the rationale,
not required on class level, used mostly on essential or non-trivial methods.

We discourage non-Javadoc comments: the meaning should stem from code itself.
Example: instead of creating a comment on a method call, we can
create more context by extracting the method into a well-named method or lightweight component 

#### Code Formatting
Code formatting is taken care of automatic build step (with Maven plugin).
Some parts of code, such as builder chained method calls, tend to be a challenge for automatic fomatter.
We normally surround such sections with @formatter:off / @formatter:on directives.
Example: ConfigureClassRules.configure()
- Auto-formatting switch around builder section (@formatter:off / @formatter:on)
- Intelligently indent contexts, configurables and rules within chained method structure


### OOP
#### Instantiation
- We generally prefer Builder pattern for multi-field classes or where readability is crucial  
- Objects are normally instantiated with Creator.create() - prefer that over the 'new' for extensibility
- Singletons are maintained with Creator.singleton()
- For dependency injections, one can use Creator.inject()
- Some classes (All, Exists) are instantiated with 'new' by end-user. This is part of library contract.
Since this precludes from using Creator.create(), we have a workaround: these classes delegate to
a corresponding concrete implementation (e.g. ConcreteExists); and Creator is used to instantiate/inject 
these concrete implementations.

#### Inheritance
- Avoid deep inheritance chains
- We do not use 'final' or 'locked'  

#### Object and package decomposition
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
  - Implement business logic as close-to-stateless 'actions' (.actions subpackage), mappings (.maps subpackage),
    data model (.data subpackage), business rules (.rules subpackage), outputs (.output subpackage)
  - Integrate with other 'apps' using their corresponding data model structures 



### Testing
Test cases for code units live in src/test/java.
Test cases backing specifications (from /docs/dev/Specification.md) are in src/test/java/com/taitl/existential/specs.

#### Testing Standards
For each implemented specification from /docs/dev/Specification.md, create a test case in the corresponding
subpackage of com.taitl.existential.specs (src/test/java/com/taitl/existential/specs).

#### Testing Guidelines
Some rules around testing we adopt:
- It is ok to test protected and private methods
- It is ok to make private methods/fields protected/default to allow testing
- As well as to make adjustments to classes to facilitate testability

#### Test Structure
Use modern test frameworks capabilities for structuring the tests to the maximum:
- Liberally use test nesting for coherent parts within unit test source file
- We often use user story text as name for nesting test case
- Take advantage of the fact that test initialization is shared by the nested tests
- Liberally use test parameterization and other techniques

#### Test coverage and isolation
- Try to achieve significant (89%) coverage, but do not insist on coverage of units which are in active development
- Test by coherent sets of units (e.g. class+immediate dependencies) rather than testing each class in total isolation
- The above means our unit tests are often also end-to-end tests (that's ok)
- We include all tests into test coverage
- Regression tests refer to issue number in name and title in test description


### Various
- Use 'brief' notation for getters and setters (x() instead of getX())


### What to avoid
Being a principled team, we fight a few dogmas.

We generally avoid, unless there is a valid reason:
- non-wildcard imports 
- 'final' keyword
- reference syntax in lambdas 
- 'private' access modifier (prefer default or protected)
- Optionals
- @Override annotation 
- reflection
- overuse of streams
- Camel-case abbreviations (e.g. HTML -> Html)
- HTML formatting tags in Javadocs, such as <p> and <br> 
- non-Javadoc comments (the meaning should stem from code)
- testing a class in total isolation (we test coherent clusters of classes instead of mocking around) 
