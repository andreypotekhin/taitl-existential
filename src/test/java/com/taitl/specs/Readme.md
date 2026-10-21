# Library Specification Tests

These tests back claims in the [topic specification index](../../../../../../docs/dev/Specification.md).
Use requirement identifiers and user-facing descriptions for new acceptance cases. Preserve existing tests while
reconciling requirement coverage; a topic's completion mark must have direct passing evidence.

Night City models/configuration compile from the shared [example](../../../../../../examples/night_city/Readme.md)
as test sources. Run the suite with `mvn test`, or the full build with `mvn clean install` from the repository root.
