<<<<<<< ours
=======
### T02202604 Add Repository Issue Templates

Create GitHub issue templates for bugs and feature requests (and a PR checklist if helpful). This will
standardize incoming reports, reduce back-and-forth for maintainers, and improve triage quality.

### T02202603 Add CHANGELOG and Release Notes Policy

Introduce a `CHANGELOG.md` describing release entries and a brief note in `Readme.md` on how versions are
tracked (for example, semantic versioning). This helps downstream users evaluate risk and upgrades.

### T02202602 Define Non-Null Handler Action Defaults

Replace the placeholder TODO in `ExecuteHandler` by introducing a canonical truth predicate for implicit
handler conditions and tightening handler action invariants (prefer disallowing null actions). Capture the
intended behavior in tests so immutable handlers still validate conditions while actionable handlers execute
predictably.

>>>>>>> theirs
### T02202601 Add OSS License File

Select a concrete OSS license and add a `LICENSE` file at the repo root. Update `Readme.md` to mention the
chosen license and any contribution requirements so downstream users can quickly evaluate compatibility.

### T02202602 Define Non-Null Handler Action Defaults

Replace the placeholder TODO in `ExecuteHandler` by introducing a canonical truth predicate for implicit handler
conditions and tightening handler action invariants (prefer disallowing null actions). Capture the intended behavior
in tests so immutable handlers still validate conditions while actionable handlers execute predictably.

### T02182601 Separate Handler Execution Flow in On

Refactor `com.taitl.existential.handlers.On` to split execution and error handling flow into a dedicated path to
avoid interleaving validation bookkeeping and handler invocation. This will make the execution lifecycle easier to
reason about and reduce the risk of partially applied handler effects.

- Introduce a dedicated execution method with explicit inputs/outputs.
- Move error handling and post-execution bookkeeping into discrete steps.
