# Current-State Assessment

Assessed 2026-10-20 at revision `a1c9abd` plus this task's documentation and example/build changes. V1 and V2 are
planned releases, not completed milestones. See [traceability](TraceabilityMatrix.md) for every roadmap item and
[backlog](Backlog.md) for canonical epics and existing suggestion/TODO intake.

## Findings and changes

The old backlog contained only an assessment task and no epics or sprint charters. Planning templates contained
Structure-specific compiler/PySpark instructions and invented example completion/date entries. They are replaced by
Java-library criteria, eleven undated sprint charters, release exit criteria, and explicit evidence requirements.

The original specification mixed user claims, implementation notes, and empty sections. Its content now lives in
seven topic specifications with 153 requirement IDs and all 51 original completion marks preserved. Paired designs
separate current architecture from unresolved choices; the original specification retains forwarding section anchors.
A completion mark does not replace current acceptance evidence. Topic files are not all implementation-ready.

The strict interaction process belongs to agents in [Process](../auto/Process.md). Human contributors may use the
same designs, specifications, and evidence without following a prescribed conversation sequence. Agents can begin
with exploratory discussion or derive scenarios/examples when selecting the next backlog item.

Night City's 34 Java sources formerly shipped inside the production tree and were imported by 20 test sources.
They now compile as shared test sources under `examples/night_city/`, retaining package names and Java behavior.
Build Helper is a build plugin, not a consumer dependency. Formatting and source checks cover the extracted example.

Performance observations previously lived inside archived plans. The existing benchmark now has an
[initial three-sample session](../performance/topics/CustomTransactions.md), repeatable metadata, and topic history.
No new performance guarantee or threshold is inferred from these measurements.

## Current validation

The clean command `mvn clean install` passed on Amazon Corretto JDK 17.0.15 and Maven 3.9.10, compiling production
sources at release 11 and examples with tests. Surefire reported 386 tests run: 383 passed, zero failures/errors,
three skipped. Checkstyle and PMD passed, including the relocated example source roots. Artifact inspection confirmed
that both
normal and assembled library JARs contain the public facade and no example classes.
No separate integration or independent-consumer evidence was established by this run.

The three skipped tests are disabled methods in `EvaluationLogicTest.EvaluateValidation`:
`executesEffectsAndCollectsViolations`, `rethrowsAndKeepsPartialReport`, and `collectsPredicateFailure`.
Their reasons report missing configurations. Sprint 03 owns repairs and direct verification; Sprint 05 owns release
closure. Their absence is not proof of correct report collection or failure propagation.

JaCoCo logged that execution data was missing and skipped its report/check. Surefire's explicit `argLine` does not
include the agent prepared by JaCoCo. No coverage percentage is established by this build. See the
[coverage follow-up](../suggestions/S10202601.Restore-coverage-evidence.md),
[development troubleshooting][cov],
and Sprint 05.

The existing benchmark ran successfully in three independent JVM invocations with 5,000 warmup and 50,000 measured
transactions per scenario. These descriptive timings are not a performance regression gate or percentile latency.
This task does not establish Java 11 runtime compatibility merely because the compiler target is release 11.

## Highest-priority remaining decisions

- V1-E01: independent-instance isolation, configuration freeze/concurrency, and supported runtime evidence.
- V1-E02: conflicting context/wildcard ordering suggestions and transaction rule composition.
- V1-E03: migrated Precondition/Early wording versus six implemented StageName values; event identity/deduplication.
- V1-E04: checkpoint behavior, nested independence, and lifecycle failure/recovery/resource ownership.
- V1-E05: actual coverage collection and resolution of disabled-test/release evidence gaps.
- V2-E01–E04: subclass matching, relationship/index naming and semantics, custom events, effects, diagnostics, cleanup.
- V2-E05: independent consumer evidence and external Maven publication prerequisites/availability.

## Scope and evidence rules

Preserve current release assignments. V1 requires existing event, constraint/effect, diagnostic, and disposal behavior
to exercise its transaction model, while broader completion stays in V2. A failing prerequisite is an explicit blocker.
Do not turn old suggestions, passing neighboring unit tests, or a local install into newly approved scope or support.
Both releases end with feature-free hardening. Mark completed claims/milestones only with their direct evidence.

The user's existing `docs/Usage.md` edits are preserved. No changes are made to taitl-structure.


## Documentation and scope validation

A repository-local audit verified preservation of the original claim wording, completion marks, and specification
anchors; checked relative documentation links; and compared all 34 moved Java source bodies with the original files.
Both library JARs contain the public facade and no `com/taitl/ex/examples/` classes. The user-edited Usage.md file and
taitl-structure's original diff/status were unchanged. All 36 original roadmap scope items retain their release
assignments and have traceability rows; all eleven sprint charters remain planned.

[cov]: ../Troubleshooting.md#problem-coverage-build-succeeds-but-jacoco-reports-missing-execution-data
