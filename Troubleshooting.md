# Troubleshooting

## Maven Build

**Problem: Maven build fails with ‘Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.1.2:check’**

Error: [WARN] \taitl-existential\src\main\java\existential\constants\Constants.java:5:18: 'static' modifier out of order with the JLS suggestions. [ModifierOrder]

Cause: Coding style violation detected by Checkstyle plugin.

Fix: Run Checkstyle in IDE to see Checkstyle errors and suggestions for fixing. For instance, to fix above violation, ‘public final static’ should be changed to ‘public static final’

