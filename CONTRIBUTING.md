# Contributing

Thank you for contributing to Existential.

## Before you start

- Read /Readme.md for core concepts and usage.
- Check /Troubleshooting.md for common setup/runtime issues.
- Search existing issues and discussions (if enabled) before filing a new one.

## Development setup

Follow /docs/dev/Setup.md to configure your local environment.

## Coding standards

- Follow the style guide in /docs/dev/Style.md.
- The build uses an automatic formatter. If chained builders become hard to format,
  use the @formatter:off / @formatter:on pattern as described in the style guide.
- Keep classes focused and avoid duplication.

## Tests

- Run tests with `mvn test` before opening a PR.
- If you add or change behavior, add or update tests accordingly.

## Documentation

- Update /Readme.md and relevant files in /docs when public-facing behavior changes.
- Keep Javadocs concise and aligned with /docs/dev/Style.md.

## Pull requests

- Keep PRs small and focused.
- Describe the intent, scope, and risk of the change.
- Include reproduction steps for bug fixes and relevant benchmarks for performance work.
