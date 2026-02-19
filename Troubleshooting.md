# Troubleshooting

## Maven Build

**Problem: Maven build fails with `Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.1.2:check`**

Error example:
```
[WARN] /taitl-existential/src/main/java/com/taitl/existential/constants/Constants.java:5:18: 'static' modifier out of order with the JLS suggestions. [ModifierOrder]
```

Cause: A coding style violation detected by the Checkstyle plugin.

Fix: Run Checkstyle in the IDE to view all violations and apply the suggested changes. In this case, change
`public final static` to `public static final` and re-run the build.
