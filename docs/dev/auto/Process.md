# Agent Development Process

This process guides agents working on Existential. Human contributors may use it as a reference; it does not impose
an interaction sequence on them. Start with [Automation](Automation.md), the [backlog](../project/Backlog.md), and
[development guidance](../Development.md).

## Two ways to begin

An exploratory design discussion can start with a vague topic, question, observed problem, or hypothesis. Investigate
existing documentation and code, explore alternatives, and develop the intended outcomes through discussion.
A user scenario or documentation example is useful input, not a prerequisite for starting that discussion.

When asked to take up the next piece of work, inspect the active release, dependencies, and evidence gaps. Select the
next ready backlog item, derive its user scenario and a focused documentation example, then discuss unresolved design
choices. Do not ask the user for repository facts that can be discovered by inspection.

Both entry paths converge on the progression below. Discussion can revisit earlier steps as understanding improves.

## From discussion to delivery

1. Discuss the design and investigate the current implementation, existing specifications, risks, and alternatives.
2. Draft an execution plan that organizes the investigation, document changes, delivery steps, and validation.
3. Record the chosen approach, rationale, alternatives, and open decisions in the topic `design/*.design.md` document.
4. Write the observable behavior, guarantees, errors, and acceptance cases in the matching
   `specifications/*.spec.md` document. Develop user scenarios and examples by this point.
5. Finalize the execution plan against the design and specification before implementation begins.
6. Add or strengthen meaningful tests, implement the behavior, and collect relevant performance evidence.
7. Reconcile documentation and planning records with the observed result; record remaining gaps and close the task.

For substantial work, follow [Plans.md](Plans.md) and keep the plan's progress, discoveries, decisions, and outcomes
current. A draft plan is an investigation/delivery outline; a finalized plan is ready to implement. The same file
progresses through these states. Archive completed plans under `docs/dev/past/planning/`.

## What each document does

The design explains how the solution works and why it was chosen. Include alternatives, performance implications,
resource ownership, concurrency concerns, and unresolved decisions where relevant. Observed code is evidence, not an
automatic decision to make that behavior the intended design.

The specification defines what a caller can observe: supported behavior, guarantees, errors, and acceptance cases.
Each requirement has one authoritative topic location and a stable identifier. The
[specification index](../Specification.md) routes readers there. A leading `+` preserves the project's convention
for implemented, test-backed claims; do not add it without evidence.

The execution plan explains how to deliver and verify the change, including concrete steps and dependencies.
Embed the context needed to execute it, identify the governing requirements, and reconcile it when they change.
Plans and chat history do not supersede the normative specification.

The backlog and sprints explain what remains and in what order. Link to designs, specifications, plans, tests, and
[performance records](../performance/Readme.md) rather than copying their contents into each planning document.

## Readiness for implementation

Before implementation, establish a clear user outcome, documentation example where useful, resolved behavioral
decisions, requirement references, acceptance cases, dependencies, and relevant performance considerations.
Unresolved choices that affect behavior keep that portion of implementation blocked; independent ready work can proceed.
Document unavailable evidence explicitly. A partial test run or an inherited completion mark is not release evidence.

Small corrections can update the existing topic documents and tests directly. Do not create a new plan or design
file for every typo, straightforward bug fix, or mechanical change. Use an execution plan when there are several
steps, significant uncertainty, or a meaningful handoff.

## Learning and revising

A prototype may precede the finalized specification when it answers a concrete design question. State its question,
smallest proof, and criteria for retaining or discarding it. A prototype does not establish supported behavior.

If tests, implementation, or benchmarks challenge a decision, revisit the design and any affected specification,
then reconcile the execution plan before proceeding. Record why the decision changed. Keep end-user documentation
consistent with supported behavior and label future examples as proposals.

## Completion

Use the [Definition of Done](../project/DefinitionOfDone.md). Build the project with tests, record exact commands and
results, update applicable troubleshooting guidance, and link benchmark records when transaction paths change.
Update epic/sprint progress and traceability, preserve explicit deferrals, and archive completed plans and accepted
suggestion/TODO work according to [Agents.md](../../../Agents.md). Never close a release merely because its documents
exist; each release ends with its scheduled hardening sprint and actual exit evidence.
