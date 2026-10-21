# Definition of Done

A task is done when the relevant criteria below have evidence. Apply them proportionally to the change; documentation
maintenance does not require new Java tests. Release completion also requires the release's hardening exit criteria.

## Behavior and tests

- The intended observable behavior and errors are specified in the governing topic specification.
- Relevant unit and specification tests pass, including negative cases and retained behavior.
- New completion marks identify implemented claims backed by direct acceptance tests.
- A full coding-task build, `mvn clean install`, passes; inspect required checks and skipped/disabled tests explicitly.
- Missing coverage data or a skipped required check is a verification gap, even if Maven reports build success.
- Night City-backed tests compile from the shared example tree; example classes do not enter library artifacts.

## Performance and resources

- Changes to transaction-path behavior have relevant topic benchmarks or an explicit reason measurement is inapplicable.
- Compare matching workload/environment samples and link the [performance record](../performance/Readme.md).
- Investigate material regressions; do not infer performance improvement from one noisy timing.
- Check resource ownership and concurrency where affected; throughput alone cannot prove absence of leaks.

## Diagnostics and documentation

- Error messages explain the relevant context and remedy; troubleshooting links point to the appropriate document.
- User documentation describes supported behavior; proposed behavior is identified as such.
- Paired designs/specifications, requirement references, and examples match implemented decisions.
- The design addresses correctness, actionable errors, failure recovery, resource hygiene, and performance with
  concrete decisions/evidence or justified inapplicability for the selected capability.
- The epic, sprint, traceability, and execution-plan evidence reflect actual completion and remaining gaps.
- Archive completed plans and implemented suggestion/TODO items under the existing project conventions.

## Review and release

- Review public API naming, compatibility, dependency hygiene, and algorithmic/resource implications where affected.
- A release ends with its dedicated feature-free hardening sprint, not merely a documentation reconciliation.
- Record exact commands, runtime/tool versions, passed/skipped totals, required-check results, artifact/consumer
  evidence, performance links, and explicit blockers or approved deferrals in the release review.
- Local installation does not establish remote Maven availability or compatibility with untested runtime versions.
- For V1, Night City must verify the public workflow as an independent Maven consumer of the installed artifact.

Agent sequencing is described in [Process](../auto/Process.md); human contributors may use it as a reference.
