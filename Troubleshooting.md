# Troubleshooting

## Library configuration load failure

**Problem: Startup fails with an IllegalStateException while loading library configuration**

Common causes:
- `EXISTENTIAL_CONFIG_FILE` points to a missing or unreadable file.
- `EXISTENTIAL_CONFIG_FILE` points to a symlink or an oversized file.
- Properties file has an unknown key.
- Boolean value is not `true` or `false`.

Fix:
1. If `EXISTENTIAL_CONFIG_FILE` is set, verify the file exists and is readable.
1. Ensure the configuration file is a real file (not a symlink) and under 1 MB.
2. Keep only supported keys:
   - `behavior.rules.requireDescriptions`
3. Use only `true` or `false` for boolean values.
4. Unset `EXISTENTIAL_CONFIG_FILE` to use classpath fallback `existential.properties`.

<<<<<<< ours
## Condition Not Met
=======
>>>>>>> theirs
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

Fix: Avoid using `CacheHandlers` until the caching implementation lands. Track progress in `Todo.md` for the
implementation or replacement plan.
