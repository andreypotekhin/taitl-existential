# Automation

## Automation Contract

Follow [Process](Process.md). Exploratory design may start with a vague topic; when selecting the next work item,
derive its user scenario and documentation example. Resolve the design/specification and finalize the execution plan
before implementation. Topic specifications are normative; plans and conversation do not override their behavior.
Use [release traceability](../project/TraceabilityMatrix.md) to distinguish existing evidence from remaining work.

See these documents for what to focus on:

- [AutomationFocus.md](AutomationFocus.md)
- [TODOs](../todo/)
- [Approved suggestions](../suggestions/approved/)

PR titles and Git branch naming for PRs: use 'auto' followed by role name and brief description
Example: auto/compress/file-extentions, auto/document/configurables

Ensure any code changes adhere to the style guide ([Style.md](../Style.md)).
Run `mvn clean install` at the end of each coding task; inspect required checks, actual coverage data, and disabled
tests. Link relevant [performance records](../performance/Readme.md) and update sprint/release evidence.

Output suggestions to /docs/dev/suggestions/.
Focus each suggestion on a specific topic, so it may be implemented in parallel with other tasks.
Follow 'Documenting' subsections in Agents.md for guidance on item id and formatting.

## Team roles
Roles: [Roles.md](Roles.md)

## Automation instructions for roles
Roles - Automation: [Roles.Automation.md](Roles.Automation.md)
