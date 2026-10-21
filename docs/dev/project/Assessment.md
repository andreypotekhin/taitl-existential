# Current-State Assessment

Assessed 2026-10-20 at revision `a1c9abd` plus this task's documentation and example/build changes. V1/V2 were the
initial planned releases. The [approved rebaseline] now plans V1/V2/V3; no release is complete. See [traceability] for
every roadmap item and [backlog](Backlog.md) for canonical epics and existing suggestion/TODO intake.

## Findings and changes

The old backlog contained only an assessment task and no epics or sprint charters. Planning templates contained
Structure-specific compiler/PySpark instructions and invented example completion/date entries. They are replaced by
Java-library criteria, initial sprint charters, release exit criteria, and explicit evidence requirements. Those
charters are now archived as superseded; the current plan has thirteen planned sprints across V1/V2/V3.

The original specification mixed user claims, implementation notes, and empty sections. Its content now lives in seven
topic specifications with 153 requirements and all 51 original completion marks preserved. Paired designs separate
current architecture from unresolved choices; the original specification retains forwarding section anchors. A
completion mark does not replace current acceptance evidence. Topic files are not all implementation-ready.

The strict interaction process belongs to agents in [Process](../auto/Process.md). Human contributors may use the same
designs, specifications, and evidence without following a prescribed conversation sequence. Agents can begin with
exploratory discussion or derive scenarios/examples when selecting the next backlog item.

Night City's 34 Java sources formerly shipped inside the production tree and were imported by 20 test sources. They now
compile as shared test sources under `examples/night_city/`, retaining package names and Java behavior. Build Helper is
a build plugin, not a consumer dependency. Formatting and source checks cover the extracted example.

Performance observations previously lived inside archived plans. The existing benchmark now has an
[initial three-sample session](../performance/topics/CustomTransactions.md), repeatable metadata, and topic history. No
new performance guarantee or threshold is inferred from these measurements.

## Historical validation before the rebaseline

The clean command `mvn clean install` passed on Amazon Corretto JDK 17.0.15 and Maven 3.9.10, compiling production
sources at release 11 and examples with tests. Surefire reported 386 tests run: 383 passed, zero failures/errors, three
skipped. Checkstyle and PMD passed, including the relocated example source roots. Artifact inspection confirmed that
both normal and assembled library JARs contain the public facade and no example classes. No separate integration or
independent-consumer evidence was established by this run.

The three skipped tests are disabled methods in `EvaluationLogicTest.EvaluateValidation`:
`executesEffectsAndCollectsViolations`, `rethrowsAndKeepsPartialReport`, and `collectsPredicateFailure`. Their reasons
report missing configurations. The Failure Recovery and Resource Hygiene epic owns repairs and direct verification; V1
Hardening and Closeout owns release closure. Their absence is not proof of correct report collection or failure
propagation.

JaCoCo logged that execution data was missing and skipped its report/check. Surefire's explicit `argLine` does not
include the agent prepared by JaCoCo. No coverage percentage is established by this build. See the
[coverage follow-up](../suggestions/S10202601.Restore-coverage-evidence.md), [development troubleshooting][cov], and the
early Night City Core Workflow repair/final V1 Hardening and Closeout verification.

The existing benchmark ran successfully in three independent JVM invocations with 5,000 warmup and 50,000 measured
transactions per scenario. These descriptive timings are not a performance regression gate or percentile latency. This
task does not establish Java 11 runtime compatibility merely because the compiler target is release 11.

## Current priority and design obligations

Begin with Night City Core Workflow: define the documented representative Night City workflow and its
success/rejection/recovery, relationship, and evolution acceptance cases. Broader
instance/configuration/context/stage/transaction/index work is V2; custom events are V3. See
[traceability](TraceabilityMatrix.md) and the [roadmap](Roadmap.md).

Correctness, actionable errors, failure recovery, resource hygiene, and performance are explicit design obligations in
every release. Record decisions and proof for each concern rather than treating them as hardening-only checks.
Robustness and performance take priority over minimizing release boundaries or hardening cycles.

Night City will become an independent Maven project consuming the locally installed artifact in Night City Artifact
Consumption and Performance. Its POM/demo is not implemented by the rebaseline. The current shared fixture tests and old
installation do not establish this consumer evidence, and no remote publication is required for local consumption.

## Scope and evidence rules

The original scope assignments are superseded by [ReleaseRebaseline](ReleaseRebaseline.md), which maps all 36 original
items to current capabilities. Requirements, inherited completion marks, existing implementations, and intake approval
states remain. Each of V1/V2/V3 ends with feature-free hardening. Readiness and verification are separate.

## Initial documentation migration validation

A repository-local audit verified preservation of the original claim wording, completion marks, and specification
anchors; checked relative documentation links; and compared all 34 moved Java source bodies with the original files.
Both library JARs contain the public facade and no `com/taitl/ex/examples/` classes. The user-edited Usage.md file and
taitl-structure's original diff/status were unchanged. All 36 original roadmap scope items retain their release
assignments during that initial migration. The current approved rebaseline changes those assignments and archives those
eleven charters as superseded before implementation, not completed work.

[cov]: ../Troubleshooting.md#problem-coverage-build-succeeds-but-jacoco-reports-missing-execution-data


## Rebaseline validation

The documentation-only pivot adds current V1/V2/V3 charters, thirteen planned sprints, capability traceability, and a
complete old-scope disposition. Topic designs now state all five quality obligations. No new feature tests, benchmark
samples, Maven project, or build results are claimed by this update. Historical build/measurement evidence above is
retained as historical, with its disabled-test and coverage gaps still explicit.

[approved rebaseline]: ReleaseRebaseline.md
[traceability]: TraceabilityMatrix.md
