# Automation

## Automation Contract
Prioritise 'top-to-bottom', 'working backwards from customer' order of implementation, 
putting effort into end-user facing artifacts first (source code, documentation), 
then proceeding with implementing library interfaces and user stories (specifications)
(docs/dev/Specifications.md) and their backing test cases (com.taitl.existential.specs subpackages)
and unit tests. 

See tracking documents on various levels of the code tree for what to focus on:
- AutomationFocus.md
- Todo.md
- Suggestions.md

PR titles and Git branch naming for PRs: use 'auto' followed by role name and brief description 
Example: auto/compress/file-extentions, auto/document/configurables

Ensure any code changes adhere to style guide (Style.md)
Fully build and test the project in the end of each task that alters code.

### Mastermind role
See 'Mastermind role' section in 'Team roles' of AGENTS.md

Output suggestions into the Suggestions.md documents (on same level as module or package Readme.md), create
new ones when necessary. Focus each suggestion on a specific topic, so it may be implemented in parallel with other
tasks.

Add suggestions to Suggestions.md in stack manner: most recent on top.
Place each suggestion under its own separate section (H3 heading) with suggestion id and title.
Inside the section, include one paragraph describing the suggestion followed by an empty line.

Observe existing suggestions: the ones approved by management for execution have exclamation point (!)
in front of the suggestion id; the ones declined have a minus (-).
For an approved suggestion, copy it to the corresponding Todo.md document on same level as Suggestions.md,
most recent to top.

### Technical debt specialist role
See 'Technical debt specialist roles' section in 'Team roles' of AGENTS.md

Suggest steps to cut on the technical debt in a specific module, package or class.
- Analyse codebase for new technical debt issues
- Identify 1–2 candidate code pieces for refactoring due to technical debt
- Address 1–2 candidate technical debt issues found in Todo.md documents
- Address smaller issues directly
- Add larger issues to TODO.md in the appropriate scopes
- Add suggestions to Suggestions.md in the appropriate scopes

Limits
- Only consider the parts of the codebase that are not under active development.

### Code compression specialist role
See 'Code compression specialist roles' section in 'Team roles' of AGENTS.md

Automation instructions
- Identify 1–2 candidate code pieces for factoring out into generalized components
- Create classes and generalized code under ex.common.helper
- Identify 1–2 duplicated code occurrences and replace with a shared helper/abstraction (only if it reduces complexity)
- No public API changes
- Preserve existing behavior, prove via tests

### Code scrutinizer role
See 'Code scrutinizer role' section in 'Team roles' of AGENTS.md

Automation instructions
- Find a bug or issue in the existing code, provide a fix or add to TODO.md document
- Find 1-2 code smells in the existing code, provide a fix or add to TODO.md documents

Limits
- Only consider stable parts of the codebase that are not under active development.

### Performance specialist role
See 'Performance specialist role' section in 'Team roles' of AGENTS.md

Automation instructions
- Identify an area of poor performance and improve it 
- Point out less-than-optimal use of data structures in existing code and suggest alternatives for improved performance. 
- For more extensive refactorings, add items to TODO.md or suggestions to Suggestions.md 
- Preserve existing behavior, prove via tests

Limits
- Only consider performance-crucial paths (e.g. things that take place between transaction start and finish), 
omitting less-critical ones (configuring the library, configuring the rules). 
- Only consider stable parts of the codebase not under active development.

### Documentation specialist role
See 'Documentation specialist roles' section in 'Team roles' of AGENTS.md

Automation instructions
- Only consider public packages (com.taitl.existential) for Javadoc commenting
- Select top 3–5 poorly documented source code files, prioritize by proximity to end-user 
- Add/repair Javadoc
- No logic changes

Limits
- Only consider public packages (com.taitl.existential) for Javadoc commenting.

### Proofreader specialist role
See 'Proofreader specialist roles' section in 'Team roles' of AGENTS.md

Automation instructions
- Prioritize public packages (com.taitl.existential) and dirs (/docs) for proofreading
- Select 3–5 source code files with poorly reading Javadoc
- Select 1-2 poorly reading .md file
- Add/repair Javadoc
- Add/repair .md
- No logic changes
