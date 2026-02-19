### T02182601 Separate Handler Execution Flow in On
Refactor `com.taitl.existential.handlers.On` to split execution and error handling flow into a dedicated path to avoid interleaving validation bookkeeping and handler invocation. This will make the execution lifecycle easier to reason about and reduce the risk of partially applied handler effects.
- Introduce a dedicated execution method with explicit inputs/outputs.
- Move error handling and post-execution bookkeeping into discrete steps.

### T02182601 Retire Deprecated Unused Types
Plan a cleanup for the deprecated `com.taitl.ex.logic.unused` package: decide whether to remove it, relocate it to a legacy module, or wrap it behind a clear compatibility boundary. Include a migration note in Readme/Troubleshooting so consumers know how to move to `EventKey`.

### T02182602 Resolve CacheHandlers Stub
Either implement the `com.taitl.ex.cross.caching.CacheHandlers` API or delete/replace it with a documented placeholder that throws a clear exception and links to a troubleshooting entry. Leaving TODO stubs in a public surface area is misleading for users.
