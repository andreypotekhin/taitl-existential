# Troubleshooting

## Maven Build

**Problem: Maven build fails with ‘Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:3.1.2:check’**

Error: [WARN] C:\Dev\Code\Spaces\OpenSource\taitl-existential\src\main\java\existential\constants\Constants.java:5:18: 'static' modifier out of order with the JLS suggestions. [ModifierOrder]

Cause: Coding style violation detected by Checkstyle plugin.

Fix: Run Checkstyle in IDE to see Checkstyle errors and suggestions for fixing. For instance, to fix above violation, ‘public final static’ should be changed to ‘public static final’

## Eclipse

**Problem: Plugin execution not covered by lifecycle configuraiton**

Error: 

Cause: Eclipse cannot sync its plugin configuration (e.g. Checkstyle plugin configuration) from Maven build file plugin (e.g. maven-checkstyle-plugin config) found in pom.xml, because the required M2E connector plugin is missing.

Fix: Use Alt-Enter on Problem’s window line to get a suggestion. Follow suggestion, e.g. by installing the required M2E connector plugin (e.g. Checkstyle configuration plugin for M2Eclipse).