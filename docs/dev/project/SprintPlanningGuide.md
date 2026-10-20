# Sprint Planning Guide

## Sprint Length

Use one- or two-week iterations. The early work benefits from short feedback cycles and demonstrable vertical slices.

## Sprint Planning Inputs

Each sprint should include:

- Sprint goal.
- User-facing outcome.
- Implementation tasks.
- Relevant specification items.
- Acceptance criteria.
- Risks.
- Explicit non-goals.

Template: [SprintTemplate.md](templates%2FSprintTemplate.md)

## Estimation Guidance

Prefer small tasks that produce visible progress:

- Specification item implemented
- Source fixture added
- Execution test added
- Structured error implemented

Avoid broad tasks such as “implement compiler.” Split them by focused deliverables with defined output.

## Spikes

Spikes must include short, written outcomes before implementation work proceeds. Each spike should produce:

- the smallest executable proof;
- the design decision or remaining risk;
- links to follow-up backlog items when the proof changes scope;
- any fixture or test worth keeping.

Do not let a spike become implementation by stealth. It is complete when it proves or disproves the design assumption
clearly enough to plan the vertical slice.

## Performance Tracking

Each sprint should track performance time and its improvements/regressions.

- Benchmarks are maintained in taitl/existential/benchmarks/ 
- New benchmarks are created as part of feature implementation.
- Performance is measured as part of execution plans and sprints.
- Performance reports are accumulated, per-topic and aggregate.
- Performance regressions are avoided.

Example: see section 'Artifacts and Notes' in [P03142601.Custom-transaction-instance.plan.md](..%2Fpast%2Fplanning%2FP03142601.Custom-transaction-instance.plan.md)

Optimize early; prevent obviously non-linear algorithms from entering core paths.

## Demo Expectations

Every sprint after groundwork should be demoable using test cases such as using example night_city app.

## Version Hardening Sprint

Every version (`vN`) must end with a dedicated hardening sprint. Schedule it when the version is planned and place it
after every feature-delivery sprint in that version; do not treat release evidence and documentation closure as a
substitute for the sprint.

The hardening sprint admits no new features. It closes release-blocking defects and verifies the version's public
contract through regression, online/generated parity, supported-target compatibility, generated-artifact freshness,
documentation, diagnostics, and performance-baseline checks. Its charter must name the version exit criteria, the
release evidence to collect, and deferred follow-up work discovered during hardening.

Keep release evidence lightweight. The hardening ExecPlan's `Outcomes & Retrospective` records the exact commands,
backend versions, passed/skipped totals, and links to explicit deferrals. It does not require an approval workflow or a
separate scorecard. `make build` is necessary but insufficient: release closure also runs the relevant classic Compose
lanes and Spark Connect lanes only where support is claimed. A skipped live lane is not release evidence.
