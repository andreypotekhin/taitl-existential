# Troubleshooting

## Unsupported lower-bounded type rules

**Problem: Configuration fails for a rule key such as `Box<? super Cat>`**

Lower-bounded generic rule patterns are not supported. Use a concrete type argument, an unbounded wildcard, or a
supported upper bound such as `Box<? extends Animal>`. The library rejects the rule during configuration so it cannot
silently remain unapplied.

## Unresolved generic types

**Problem: A warning says a reported entity has unresolved generic parameters**

The runtime class does not preserve the generic argument needed to choose a safe rule. Report the event with an
explicit `TypeKey`, such as `new TypeKey<Box<Cat>>() {}`, or disable `TYPE_MATCHING_WARN_UNRESOLVED_GENERICS` on the
`Existential` instance if the warning is expected. The warning is enabled by default and can also be controlled with
`typeMatching.warnUnresolvedGenerics` in the library properties file.

## Library configuration load failure

**Problem: Startup fails with an IllegalStateException while loading library configuration**

Common causes include:
- `EXISTENTIAL_CONFIG_FILE` points to a missing or unreadable file.
- `EXISTENTIAL_CONFIG_FILE` points to a symlink or an oversized file.
- `EXISTENTIAL_CONFIG_FILE` points to a file with insecure permissions or unexpected ownership.
- `EXISTENTIAL_CONFIG_FILE` is stored under a group/world writable directory.
- The properties file includes an unknown key.
- A boolean value is not `true` or `false` (case sensitive).

Fix:
1. If `EXISTENTIAL_CONFIG_FILE` is set, verify the file exists and is readable.
2. Ensure the configuration file is a real file (not a symlink) and under 1 MB.
3. On POSIX systems, ensure the file is owned by the current user and not group/world writable (for example,
   `chmod 600 /path/to/config.properties`).
4. On POSIX systems, ensure the configuration directory is owned by the current user and not group/world writable
   (for example, `chmod 700 /path/to`).
5. Keep only supported keys:
   - `behavior.rules.requireDescriptions`
   - `shutdown.timeoutMillis` (nonnegative milliseconds; `0` waits without a time limit)
   - `typeMatching.warnUnresolvedGenerics`
6. Use only `true` or `false` for boolean values.
7. Unset `EXISTENTIAL_CONFIG_FILE` to fall back to classpath `existential.properties`.

## Library configuration after finalization

**Problem: A flag change or retained configuration builder fails because the library has been finalized**

The first successful `begin()` fixes an instance's flags and rules. Make programmatic flag changes and declare all
rules before that call. If the application needs a different configuration, create and configure a new `Existential`
instance, then safely publish it to the threads that will use it. A failed first `begin()` leaves configuration open
for correction and retry.

### Library shutdown waits for an active transaction

When: `Existential.close()` or `Ex.close()` is called while a transaction is still active.
Cause: Graceful shutdown stops new begins and waits for each registered handle to commit, roll back, or close.
Fix: Finish the transaction, or close its `Tr` handle explicitly if rollback callbacks should not run, then retry
library close. Set `shutdown.timeoutMillis` or call `shutdownPolicy(ShutdownPolicy.graceful(duration))` to bound the
wait. Zero means unlimited waiting. A timeout or interruption leaves the library draining so you can finish active
work and retry close. Close the library after transaction callbacks return. Calling close inside a callback fails
promptly, including while another thread is already closing the library, so the callback cannot deadlock that shutdown.

## Condition Not Met

**Problem: Rule or handler fails with `ConditionNotMetException` or `EventHandlerException`**

Common causes include:
- The condition predicate evaluates to `false` for the current entity state.
- A handler was configured as a constraint (no action), so unmet condition raises an exception.

Fix:
1. Verify the condition logic against the entity values passed to the handler.
2. If the rule is informational only, configure it as an action handler instead of a constraint.
3. Add a description to the handler to make diagnostics clearer.

## Event Handler Execution Failure

**Problem: A handler throws `EventHandlerException` during validation or execution**

Common causes include:
- The handler action threw an exception (NPE, validation failure, illegal state).
- A handler depends on external state that is not initialized or is stale.

Fix:
1. Inspect the exception cause for the original failure.
2. Guard against nulls and invalid state in the handler action.
3. Add a description to the handler and log key inputs for diagnostics.

## Intent violation

**Problem: Event emission fails with `IntentViolation`**

Typical error messages:
- `No intent is configured for event 'Read<...>'`
- `Intent condition is not met for event 'Write<...>'`

Common causes include:
- You configured an intent for an event type (for example `read`) but did not configure the same intent for another
  entity class that emits that event.
- The configured intent predicate evaluated to `false` for the emitted event.

Fix:
1. If you use event-type intent gating, declare intents for every entity type that can emit that event type.
2. Review intent predicates and make sure they match the entity state at event time.
3. If no gating is needed for an event type, remove intents for that event type.

## Memo state missing

**Problem: Event evaluation fails with `MemoException` stating memo state is missing or invalid**

Typical error messages:
- `Memo state not present for event 'Update<...>'`
- `Argument 'before' must be a detached snapshot, not the same instance as 'live'`
- `Memo state is already registered for this entity and type key`

Common causes include:
- A transaction mutates an entity in place and then relies on `Transit` or `Port` rules without calling `memo(...)`
  first.
- The same live object instance was passed as both `live` and `before`, so no detached snapshot exists.
- The code tried to register a second before-state for the same live object and type key after the first snapshot was
  already stored.

Fix:
1. Before mutating a tracked entity in place, call `memo(snapshot, live, ...)` with a detached snapshot created by
   `clone()`, a copy constructor, or another copy strategy.
2. Do not pass the same object reference as both `live` and `before`.
3. Register memo state once per live object and type key, as early in the transaction as possible.
4. If your flow only needs single-entity rules, remove the `Transit`/`Port` handlers or intents that force bi-state
   evaluation.

## Transaction not found

**Problem: A call fails with `NotFoundException` stating `Transaction not found`**

Common causes:
- The transaction was already committed or rolled back, which invalidates its id.
- The transaction id was never created, or it was modified before reuse.
- The transaction id came from a different runtime or test scope.

Fix:
1. Call `begin()` and use the returned id exactly as-is.
2. Do not reuse transaction ids after `commit()` or `rollback()`.
3. Keep transaction ids scoped to the runtime that created them.

## Transaction closed

**Problem: A call fails with `ExistentialException` stating `Cannot ... because the transaction is closed`**

Common causes:
- A transaction object was reused after `commit()` or `rollback()`.
- A transaction object was closed by cleanup logic and then referenced again.

Fix:
1. Do not reuse `Tr` instances after `commit()` or `rollback()`.
2. Start a new transaction with `begin()` for new work and event emission.

## Invalid operation key

**Problem: A call fails with an IllegalArgumentException stating the operation key is invalid**

Typical error messages:
- `Operation key should start with a slash ('/')`
- `Operation key cannot be a single slash ('/')`
- `Operation key cannot end with a slash ('/')`
- `Operation key cannot have wildcards ('*')`

Fix:
1. Use a leading slash and at least one path segment, for example `/app/orders/update`.
2. Do not include trailing slashes or wildcard characters.
3. When building operation keys dynamically, add unit tests for the formatting.

## Index key mismatch

**Problem: Index usage fails with key type or key value mismatch errors**

Typical error messages:
- `Argument 'key' class ... does not match the key class ... required by this index`
- `Argument 'newKey' value ... does not match key value ... returned by 'getKey' function`

Fix:
1. Ensure the index key type matches the key you pass to `get()`/`getObj()`.
2. Confirm your index `getKey` function returns the same key you use with `add()` and `rekey()`.
3. Prefer using `get(K)` with a strongly typed key when possible.

## Type key format

**Problem: TypeKey construction fails with an IllegalArgumentException about format or missing generic type**

Typical error messages:
- `You should call this method with an anonymous subclass of TypeKey, parameterized with a type. Example: ...`
- `Class name must be of proper format: 'Class<GenericQualifier>'`
- `Right bracket must not come before left bracket`

Fix:
1. For generic type capture, use an anonymous subclass: `new TypeKey<Document<JSON>>() {}`.
2. For string keys, use `Class<Qualifier>` (matching angle brackets) without extra whitespace.
3. For non-generic types, prefer `TypeKey.valueOf(MyClass.class)` or `TypeKey.valueOfFull(MyClass.class)`.

## Maven Build

**Problem: Maven build fails with
`Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.1.2:check`**

Error example:

```
[WARN] /taitl-existential/src/main/java/com/taitl/existential/constants/Constants.java:5:18: 'static' modifier out of order with the JLS suggestions. [ModifierOrder]
```

Cause: A coding style violation detected by the Checkstyle plugin.

Fix: Run Checkstyle in the IDE to view all violations and apply the suggested changes. In this case, change
`public final static` to `public static final` and re-run the build.

## Cache handlers not implemented

**Problem: Calling `CacheHandlers.cacheHandlersPerOp`, `cacheHandlersPerContext`, or `cacheHandlersPerTran`
throws `UnsupportedOperationException`**

Cause: `CacheHandlers` is a placeholder API and its caching strategies are not implemented yet.

Fix: Avoid using `CacheHandlers` until the caching implementation lands.

## Entity quantifier validation

Fluent `.all(...)` and `.exists(...)` rules validate each reported entity/access event during checkpoint or commit.
This includes deletion: a transition uses its after-value, while deletion uses its before-value. A missing Exists
source key or an empty source produces a `PredicateFailure` with the configured rule description.

If validation starts rejecting an operation after the quantifier dispatch repair, inspect its declared rules and
caller-maintained source collections/indexes: these expressions were previously skipped. Populate the matching source
before validation, correct the reported entity, or use an event-specific predicate if the rule should apply only to
creation or another selected event. Conditional All can exclude entities that do not require the constraint.
After a rejected commit or checkpoint, explicitly roll back the caller-held transaction handle for configured rollback
callbacks.

## Validation failure

`ValidationStageExceptions` identifies the operation and contains the individual rule failures through its cause,
suppressed exceptions, and `multiple()` accessor. Inspect those descriptions to correct the reported entity or its
caller-owned relationship source. An All/Exists source is read during validation; the library does not fill it or undo
caller changes. After a rejected commit or checkpoint, use the original transaction handle for explicit rollback, then
begin a fresh
transaction for another attempt. Retaining the closed handle does not retain its cleared runtime event indexes.

## Event endpoints

Transit requires both before and after values. Use `tr.transit(before, after)` for relocation or another change with
explicit snapshots. Use `tr.port(null, after)` for creation and `tr.port(before, null)` for deletion. Port rejects two
null endpoints. Endpoint errors occur before the event is indexed; correct the values and report the intended event
on the same transaction, or explicitly roll back. Keep snapshots and hash-source keys stable until validation ends.


## Managed index maintenance

A managed index token must be registered in the operation's context or transaction before begin. Use the same token
for Tr.index; a withPolicy call returns another token. Give distinct effective definitions unique names. Use Class for
non-generic subjects and TypeKey for generic subjects; do not infer missing arguments from an erased value.

If a key extractor fails or returns null, inspect the named index, operation, subject side, and original cause. Report
relevant changes, return stable non-null keys, and avoid mutating or querying the same index from its extractor.
Single-valued bookkeeping may detect errors during reporting; deferred multi-valued work may detect them at first query
or a boundary. Failed maintenance rejects further work on that handle: explicitly rollback or close, fix the input,
and begin a fresh transaction. The library does not undo application objects or effects.

An absent entry can reflect disabled automatic maintenance/read enrollment, an unreported mutation, retirement by a
before/after transition, or single-valued replacement. Removing a replacement does not resurrect an earlier snapshot.
Use a multi-valued index for legitimate duplicate keys. Seed already loaded data with bulk-add methods; seeds do not
emit access events. Join map/set query views are protected; use the facade's explicit maintenance methods. Retained
views reject use after disposal.

Index retirement does not erase earlier reported events from validation. If a managed Exists intentionally requires
current membership, an earlier Read of a retired snapshot can fail that rule. Seed the prior snapshot without emitting
a read event when only its replacement should participate, or express the rule against the intended source scope.

## Known resources during shutdown

On timeout or interruption, shutdown errors include active-transaction/in-flight counts and up to eight transaction
IDs and operation names. Operation details are truncated to keep diagnostics bounded. Finish or explicitly close those
handles, then retry library close. Counts describe library-known owners at observation time; detached failed handles
are deliberately absent. These diagnostics neither detect arbitrary application leaks nor rely on garbage collection.

### General On rules are not invoked

General On constraints, effects, and intents apply to matching reported entity types for every built-in event.
If a previously built artifact skips these declarations, rebuild/install a version containing the
[T10262601 correction](docs/dev/past/todo/T10262601.General-on-rules-skipped-by-event-dispatch.md). The missing common Event
route affected dispatch and authorization; direct On.handle calls were unaffected. Check the operation/context and
explicit TypeKey naming as well. An explicit Access report does not substitute for read/read-and-lock/write reports.

### Required rule descriptions

When BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS is enabled, fluent rule declarations require non-null, non-empty descriptions.
Add a description to the rejected check, intent, effect, All, or Exists declaration and retry it on the same builder.
Alternatively disable the flag before adding that rule, or use behavior.rules.requireDescriptions=false in the normal
configuration sources. Earlier declarations are not revalidated when the flag changes. Flags and declarations freeze
when the first begin successfully finalizes configuration. The option does not run another description check per event.

## Diagnostic RULE-REJECTED

A check or expression rejected the reported entity. Inspect `exception.diagnostic()` for the rule description,
original context, reported event/type, and evaluation stage/boundary. Correct the data or the declared predicate.
Descriptions are consumer text; `RULE-REJECTED` is the stable library category.

## Diagnostic INTENT-REJECTED

A matching intent denied this event. Inspect the rule and its declaration context, including narrower child or
transaction-local permissions. Every matching intent must pass. The original rejection remains in the cause chain.

## Diagnostic INTENT-MISSING

The event family is guarded but no intent matches the reported type. Define a matching intent for this event/type
or review intent gating. This failure has no rule or declaration context to report.

## Diagnostic CALLBACK-FAILED

An invoked rule or lifecycle callback threw unexpectedly. Inspect the cause for the original application failure.
Unexpected non-intent RuntimeException now uses EventHandlerException; intent callbacks use IntentViolation.
Caller-supplied exceptions remain causes of fresh stock exceptions and are never mutated with invocation metadata.
Error propagates unchanged. These failures stop evaluation immediately.

## Diagnostic EVALUATION-FAILED

An evaluation stage collected rule failures. Inspect `multiple()` or `diagnostic().children()` in evaluation order.
Checks-only validation collects ordinary violations. When effects participate, the first violation stops later work;
the diagnostic records that limitation. A failed checkpoint/commit makes the handle rollback-only and detaches
string-ID lookup. Explicit rollback runs rollback callbacks; close only releases resources. Correct application
state before beginning a fresh transaction. Diagnostic snapshots remain readable after cleanup.
