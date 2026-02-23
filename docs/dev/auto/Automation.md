# Automation

## Automation Contract

Prioritise 'top-to-bottom', 'working backwards from customer' order of implementation,
putting effort into end-user facing artifacts first (source code, documentation),
then proceeding with implementing library interfaces and user stories (specifications)
(docs/dev/Specification.md) and their backing test cases (com.taitl.existential.specs subpackages)
and unit tests.

See these documents on various levels of the code tree for what to focus on:

- AutomationFocus.md
- docs/dev/todo/approved
- docs/dev/suggestions/approved

PR titles and Git branch naming for PRs: use 'auto' followed by role name and brief description
Example: auto/compress/file-extentions, auto/document/configurables

Ensure any code changes adhere to the style guide (Style.md)
Fully build and test the project at the end of each task that touches code.

### Mastermind role

See 'Mastermind role' section in 'Team roles' of AGENTS.md

Output suggestions to docs/dev/suggestions/. 
Focus each suggestion on a specific topic, so it may be implemented in parallel with other
tasks.

Follow 'Documenting' subsections in AGENTS.md for guidance on item id and formatting.

Observe existing suggestions: move the ones approved for execution to docs/dev/suggestions/approved.
For an approved suggestion, create the corresponding todo item in docs/dev/todo/approved.

### Design scrutinizer role

See 'Design scrutinizer role' section in 'Team roles' of AGENTS.md

Automation instructions
- Find a poorly designed or design opportunity area in the existing code, suggest improvements
- Find a poorly designed or design opportunity in library specifications, suggest improvements

### Simplification specialist role

See 'Simplification specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Analyze the codebase for opportunity to simplify, such as areas of overdesign, convolution, or difficulty to understand.
- If no opportunities found, wrap up.
- Focus on opportunity that can benefit from simplification the most.
- Provide fixes and document the rationales for simplification.

Limits

- Only consider stable parts of the codebase that are not under active development.

### Planner role

See 'Planner role' section in 'Team roles' of AGENTS.md

Automation instructions

- Consider a suggestion under /docs/dev/suggestions/planned
- Create ExecPlan as described in docs/dev/auto/Plans.md
- Output the resulting ExecPlan to docs/dev/planning/ with short descriptive name
- Discuss and refine the plan with human user
- Upon approval from human user, proceed with plan implementation
- Move implemented plan to docs/dev/planning/done/

Notes

- Example plan: docs/dev/planning/done/LibraryConfiguration.plan.md

### End-user advocate role

See 'End-user advocate role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 2-3 opportunities to improve the library for end user, and provide fixes.
  (e.g. better documentation, error messages, exceptions, logging, public code structure for readability and
  maintainability).
- Create suggestions for broader refactorings.

Limits

- Focus on public-facing public code, documentation;
  but be all-encompassing on the error messages/logging (cause they eventually bubble up to end-user).
- Ensure to follow the style guide (/docs/dev/Style.md)

### Open source specialist role

See 'Open source specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 2-3 opportunities to improve the library for open source contribution and delivery,
  (e.g. better documentation, better error messages, more helpful exceptions, better logging, better test coverage,
  better code structure for readability and maintainability)
- Provide changes or add a todo item document.
- For more extensive refactorings, add todo items or suggestions
- Update Changelog.md with recent changes

Limits

- Only consider stable parts of the codebase not under active development.
- Ensure to follow the style guide (/docs/dev/Style.md)

### Extensibility specialist role

See 'Extensibility specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find library code area with poor extensibility, provide fixes, tests or todo item or suggestions for bigger issues
- If no issues found, wrap up.
- Provide suggestion for overall extensibility

### Concurrency specialist role

See 'Concurrency specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Analyze library code for potential problems with external concurrency
- Analyze library code for potential problems with internal concurrency
- If no issues found, wrap up.
- For found potential external concurrency issues, provide fixes, tests and documentation
- For found internal concurrency issues, provide fixes, tests and documentation
- Provide suggestion for overall improvements of external concurrency

### Technical debt specialist role

See 'Technical debt specialist roles' section in 'Team roles' of AGENTS.md

Suggest steps to cut on the technical debt in a specific module, package or class.

- Analyse codebase for new technical debt issues
- Identify 1–2 candidate code pieces for refactoring due to technical debt
- Address 1–2 candidate technical debt issues found in todo documents
- Address smaller issues directly
- Add larger issues to todo documents in the appropriate scopes
- Add suggestions 

Limits

- Only consider the parts of the codebase that are not under active development.
- Consider Style.md for style guidance and what to avoid (e.g. @Override annotations)

### Code shrinking specialist role

See 'Code shrinking specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Identify 1–2 candidate code pieces for factoring out into generalized components
- Create classes and generalized code under ex.common.helper
- Identify 1–2 duplicated code occurrences and replace with a shared helper/abstraction (only if it reduces complexity)
- No public API changes
- Preserve existing behavior, prove via tests

Limits
- Limit yourself to externalizing general purpose parts of code, that is, the ones not related to library subject.  
- Do not place any code related to library business/use case into ex.common.helper - that package is only for general
  (not library-specific) helper code. You can still factor out, but to proximity (e.g. to same package, module).

### Code scrutinizer role

See 'Code scrutinizer role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find a bug or issue in the existing code, provide a fix or add a todo item
- Find 1-2 code smells in the existing code, provide a fix or add a todo item

Limits

- Only consider stable parts of the codebase that are not under active development.

### Performance specialist role

See 'Performance specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Identify an area of poor performance and improve it
- Point out less-than-optimal use of data structures in existing code and suggest alternatives for improved performance.
- For more extensive refactorings, add todo items or suggestions
- Preserve existing behavior, prove via tests

Limits

- Only consider performance-crucial paths (e.g. things that take place between transaction start and finish),
  omitting less-critical ones (configuring the library, configuring the rules).
- Only consider stable parts of the codebase not under active development.

### Security specialist role

See 'Security specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find a security issue or bugs in the code, provide a fix or add a todo item.
- Suggest a broader security improvement / better adherence to security best practices.

### Consistency scrutinizer role

See 'Consistency scrutinizer role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 3-4 opportunities to improve consistency in codebase, documentation, public API, error messages, logging,
  or other aspects.
- Provide fixes and tests, or add todo items for larger issues
- Suggests refactorings for bigger inconsistencies

Limits

- Only consider stable parts of the codebase that are not under active development.

### Expressivenes specialist role

See 'Expressivenes specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 1-2 code areas with poor expressiveness, provide fixes
- Find 1-2 documentation areas with poor expressiveness, provide improvements

Limits

- Only consider stable parts of the codebase that are not under active development.

### Style scrutinizer role

See 'Style scrutinizer role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 3-4 code areas with poor styling, provide fixes
- Find 3-4 with poor Javadoc, provide improvements

### Testing specialist role

See 'Testing specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 1-2 code areas with poor coverage, provide tests

Limits

- Only consider stable parts of the codebase that are not under active development.

### QA specialist role

See 'QA specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Find 1-2 bugs or quality issues in codebase
- If no bugs/issues found, wrap up.
- Provide fixes and tests, or add todo items for larger issues
- Suggests refactorings to improve end product quality

### Documentation specialist role

See 'Documentation specialist roles' section in 'Team roles' of AGENTS.md

Automation instructions

- Only consider public packages (com.taitl.existential) for Javadoc commenting
- Select top 3–5 poorly documented source code files, prioritize by proximity to end-user
- Add/repair Javadoc
- No logic changes

Limits

- Only consider public packages (com.taitl.existential) for Javadoc commenting.
- Consider Style.md for style guidance and what to avoid (e.g. HTML formatting in Javadocs)

### Proofreader specialist role

See 'Proofreader specialist role' section in 'Team roles' of AGENTS.md

Automation instructions

- Prioritize public packages (com.taitl.existential) and dirs (/docs) for proofreading
- Select 3–5 source code files with poorly reading Javadoc
- Select 1-2 poorly reading .md file
- Add/repair Javadoc. Ensure to adhere to style guide (/docs/dev/Style.md) 
- Add/repair .md
- No logic changes

