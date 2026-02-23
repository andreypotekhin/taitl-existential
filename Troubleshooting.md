# Troubleshooting

## Library configuration load failure

**Problem: Startup fails with an IllegalStateException while loading library configuration**

Common causes:
- `EXISTENTIAL_CONFIG_FILE` points to a missing or unreadable file.
- `EXISTENTIAL_CONFIG_FILE` points to a symlink or an oversized file.
- `EXISTENTIAL_CONFIG_FILE` points to a file with insecure permissions or unexpected ownership.
- Properties file has an unknown key.
- Boolean value is not `true` or `false`.

Fix:
1. If `EXISTENTIAL_CONFIG_FILE` is set, verify the file exists and is readable.
2. Ensure the configuration file is a real file (not a symlink) and under 1 MB.
3. On POSIX systems, ensure the file is owned by the current user and not group/world writable (for example,
   `chmod 600 /path/to/config.properties`).
4. Keep only supported keys:
   - `behavior.rules.requireDescriptions`
5. Use only `true` or `false` for boolean values.
6. Unset `EXISTENTIAL_CONFIG_FILE` to use classpath fallback `existential.properties`.

## Condition Not Met

**Problem: Rule or handler fails with `ConditionNotMetException` or `EventHandlerExecutionException`**

Common causes:
- The condition predicate evaluates to `false` for the current entity state.
- A handler was configured as a constraint (no action), so unmet condition throws.

Fix:
1. Verify the condition logic against the entity values passed to the handler.
2. If the rule is informational only, provide an action handler instead of a constraint.
3. Add a description to the handler to make diagnostics clearer.

## Event Handler Execution Failure

**Problem: A handler throws `EventHandlerExecutionException` during validation or execution**

Common causes:
- The handler action threw an exception (NPE, validation failure, illegal state).
- A handler depends on external state that is not initialized.

Fix:
1. Inspect the exception cause for the original failure.
2. Guard against nulls and invalid state in the handler action.
3. Add a description to the handler and log key inputs for diagnostics.

## Transaction not found

**Problem: A call fails with `NotFoundException` stating `Transaction not found`**

Common causes:
- The transaction was already committed or rolled back, which invalidates its id.
- The transaction id was never created or was modified before reuse.
- The transaction id came from a different runtime or test scope.

Fix:
1. Call `begin()` and use the returned id exactly as-is.
2. Do not reuse transaction ids after `commit()` or `rollback()`.
3. Keep transaction ids scoped to the runtime that created them.

## Invalid operation key

**Problem: A call fails with an IllegalArgumentException stating the operation key is invalid**

Typical error messages:
- `Argument 'op' should start with a slash ('/')`
- `Argument 'op' cannot be a single slash ('/')`
- `Argument 'op' should not end with a slash ('/')`
- `Argument 'op' cannot have wildcards ('*')`

Fix:
1. Use a leading slash and at least one path segment, for example `/app/orders/update`.
2. Do not include trailing slashes or wildcard characters.
3. When building op strings dynamically, add unit tests for the formatting.

## Index key mismatch

**Problem: Index usage fails with key type or key value mismatch errors**

Typical error messages:
- `Argument 'key' class ... does not match the key class ... required by this index`
- `Argument 'k1' value ... does not match key value ... returned for object 'v' by 'getKey' function`

Fix:
1. Ensure the index key type matches the key you pass to `get()`/`getObj()`.
2. If you use `setGetKey()`, confirm it returns the same key you use with `add()` and `rekey()`.
3. Prefer using `get(K)` with a strongly typed key when possible.

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
