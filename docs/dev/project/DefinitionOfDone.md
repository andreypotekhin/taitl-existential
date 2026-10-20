# Definition of Done

A story or task is done when all relevant criteria are satisfied.

## Functional Criteria

- The requested behavior is implemented.
- Source DSL examples compile.
- Generated PySpark code imports successfully.
- Runtime behavior is covered by tests where applicable.
- Error cases are covered by tests where applicable.

## Performance Criteria

- Benchmarks are created (taitl/existential/benchmarks/) as part of feature implementation. 
- Performance is measured as part of execution plans.  
- Performance reports are accumulated (per-topic and aggregate).
- Performance regressions are avoided.

## Diagnostic Criteria

- Error messages include specific location/context.
- Error messages suggest alternatives, helpers, and config workarounds when applicable.
- Troubleshooting documents are updated.

## Documentation Criteria

- Public behavior is documented in user docs.
- Internal behavior is documented in dev docs when design-relevant.
- Specification/user-story references are updated.
- Examples show focused code. Imports may be omitted.

## Testing Criteria

- Unit tests pass.
- Specification tests pass.
- Performance tests pass.
- Negative tests cover relevant broken paths, errors and warnings.

## Review Criteria

- Public API naming is reviewed.
- Introduced code shape is reviewed.
- Performance implications are reviewed.
- Backward compatibility concerns are noted.
