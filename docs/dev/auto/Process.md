# Agent Development Process

This process guides agents working on Existential. Human contributors may use it as a reference; it does not impose an
interaction sequence on them. Start with [Automation](Automation.md), the [backlog](../project/Backlog.md), and
[development guidance](../Development.md).

## Two ways to begin

An exploratory design discussion can start with a vague topic, question, observed problem, or hypothesis. Investigate
existing documentation and code, explore alternatives, and develop the intended outcomes through discussion. A user
scenario or documentation example is useful input, not a prerequisite for starting that discussion.

When asked to take up the next piece of work, inspect the active release, dependencies, and evidence gaps. Select the
next ready backlog item, derive its user scenario and a focused documentation example, then discuss unresolved design
choices. Do not ask the user for repository facts that can be discovered by inspection.

Both entry paths converge on the progression below. Discussion can revisit earlier steps as understanding improves.

## From discussion to delivery

1. Discuss the design and investigate the current implementation, existing specifications, risks, and alternatives. 2.
Draft an execution plan that organizes the investigation, document changes, delivery steps, and validation. 3. Record
the chosen approach, rationale, alternatives, and open decisions in the topic `design/*.design.md` document. 4. Write
the observable behavior, guarantees, errors, and acceptance cases in the matching `specifications/*.spec.md` document.
Develop user scenarios and examples by this point. 5. Finalize the execution plan against the design and specification
before implementation begins. 6. Add or strengthen meaningful tests, implement the behavior, and collect relevant
performance evidence. 7. Reconcile documentation and planning records with the observed result; record remaining gaps
and close the task.

For substantial work, follow [Plans.md](Plans.md) and keep the plan's progress, discoveries, decisions, and outcomes
current. A draft plan is an investigation/delivery outline; a finalized plan is ready to implement. The same file
progresses through these states. Archive completed plans under `docs/dev/past/planning/`.

## What each document does

The design explains how the solution works and why it was chosen. Include alternatives, performance implications,
resource ownership, concurrency concerns, and unresolved decisions where relevant. Observed code is evidence, not an
automatic decision to make that behavior the intended design.

The specification defines what a caller can observe: supported behavior, guarantees, errors, and acceptance cases. Each
requirement has one authoritative topic location and a descriptive Markdown heading. The
[specification index](../Specification.md) routes readers there. A leading `+` preserves the project's convention for
implemented, test-backed claims; do not add it without evidence.

The execution plan explains how to deliver and verify the change, including context, concrete steps, dependencies, and
validation commands. Link to the governing specification sections by name and reconcile the plan when they change.
Self-containment means providing enough context to execute the work without prior conversation. A plan may summarize
accepted behavior for context, but it does not become a second authoritative specification. Specifications remain
authoritative for behavior; designs retain rationale and tradeoffs. Plans and chat history do not supersede either.

The backlog and sprints explain what remains and in what order. Link to designs, specifications, plans, tests, and
[performance records](../performance/Readme.md) rather than copying their contents into each planning document.

## Names and links

Refer to epics and capabilities by their descriptive names. Use release labels when needed to distinguish similar names,
such as V1 Hardening and Closeout. Refer to topic documents by their full names; include `.design.md` or `.spec.md` when
distinguishing rationale from behavior. Link requirements by their named Markdown sections. Prefer stable descriptive
headings and their generated anchors over abbreviated codes or embedded HTML anchors. Keep release labels, sprint order,
and existing action filenames where they serve a useful purpose.

## Required design analysis

For every selected capability, the design must explicitly address the following concerns. State the decisions,
constraints, tradeoffs, and acceptance/evidence needed, or explain why a concern is inapplicable to that capability.
These are design inputs in every release; they are not deferred to hardening or to a later quality release.

- Correctness: define valid/invalid inputs, invariants, evaluation timing, supported scope, and expected outcomes.
- Actionable errors: identify the rule/operation/context needed to diagnose a failure and the caller's remedy.
- Failure recovery: define state after failure, permitted retry/rollback/close actions, and caller responsibilities.
- Resource hygiene: define ownership and release on success/failure, retained references, cache bounds, and relevant
  concurrency restrictions. Distinguish library cleanup from undoing caller objects or external side effects.
- Performance: identify hot paths, workload/cardinality assumptions, algorithmic and maintenance costs, and the evidence
  needed to evaluate tradeoffs. Distinguish setup from transaction costs; justify budgets with measurements rather than
  inventing thresholds or deferring obviously inefficient algorithms.

Reflect observable guarantees and acceptance cases in the normative specification, and implementation/measurement steps
in the execution plan. Keep readiness separate from actual verification. A throughput sample alone establishes neither
correctness nor resource safety. Robustness and performance take priority over minimizing release boundaries or
hardening cycles.

## Readiness for implementation

Assess readiness for each selected capability rather than requiring an entire topic to be settled. For that capability,
establish a clear user outcome, documentation example where useful, resolved behavioral decisions, requirement
references, acceptance cases, dependencies, and the required design analysis above.

Track design readiness and implementation verification independently in existing planning records. Design readiness
means the capability's behavioral decisions and acceptance cases are settled. Verification means its implementation has
direct passing evidence for those cases. Record both in the existing execution plan, sprint, or traceability entry; do
not create another tracker. Inherited completion marks establish neither current readiness nor current verification.
Document unavailable evidence explicitly. A partial test run is not release evidence.

A well-defined capability may proceed while unrelated questions in its topic remain open. Unresolved decisions block
only work that depends on them; independent ready implementation can proceed. Preserve ordered delivery and actual
dependencies while allowing independent investigation and design for later sprints before earlier sprints close.

Small corrections can update the existing topic documents and tests directly. Do not create a new plan or design file
for every typo, straightforward bug fix, or mechanical change. Use an execution plan when there are several steps,
significant uncertainty, or a meaningful handoff.

## Learning and revising

A prototype may precede the finalized specification when it answers a concrete design question. State its question,
smallest proof, and criteria for retaining or discarding it. A prototype does not establish supported behavior.

If tests, implementation, or benchmarks challenge a decision, revisit the design and any affected specification, then
reconcile the execution plan before proceeding. Record why the decision changed. Keep end-user documentation consistent
with supported behavior and label future examples as proposals.

## Completion

Use the [Definition of Done](../project/DefinitionOfDone.md). Build the project with tests, record exact commands and
results, update applicable troubleshooting guidance, and link benchmark records when transaction paths change. Update
epic/sprint progress and traceability, preserve explicit deferrals, and archive completed plans and accepted
suggestion/TODO work according to [Agents.md](../../../Agents.md). Never close a release merely because its documents
exist; each release ends with its scheduled hardening sprint and actual exit evidence.
