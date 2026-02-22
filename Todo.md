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
