# Automation

## Automation Contract

Prioritise 'top-to-bottom', 'working backwards from customer' order of development,
putting effort into end-user facing artifacts first (source code, documentation),
then proceeding with implementing library interfaces and user stories (specifications)
([Specification.md](../Specification.md)), their backing specification tests (com.taitl.specs subpackages
in src/test/java/com/taitl/specs) and unit tests.

See these documents for what to focus on:

- [AutomationFocus.md](AutomationFocus.md)
- [TODOs](../todo/)
- [Approved suggestions](../suggestions/approved/)

PR titles and Git branch naming for PRs: use 'auto' followed by role name and brief description
Example: auto/compress/file-extentions, auto/document/configurables

Ensure any code changes adhere to the style guide ([Style.md](../Style.md)).
Fully build and test the project at the end of each task that touches code.

Output suggestions to /docs/dev/suggestions/.
Focus each suggestion on a specific topic, so it may be implemented in parallel with other tasks.
Follow 'Documenting' subsections in Agents.md for guidance on item id and formatting.

## Team roles
Roles: [Roles.md](Roles.md)

## Automation instructions for roles
Roles - Automation: [Roles.Automation.md](Roles.Automation.md)