# Changelog

All notable changes to this project will be documented in this file.

This project follows Semantic Versioning. See [Readme.md](Readme.md) for the release policy summary.

## Unreleased

- Added transaction-local managed indexes/joins, scoped maintenance policies, access-event coverage and explicit
  cleanup.
- Added immutable structured diagnostics, lazy multiline messages and semantic transaction/memo/maintenance error codes.
- Added code-bearing configuration/shutdown errors while retaining their Java catch types and recovery behavior.
- Prevented supplied-intent type bindings from rewriting reused definitions; validated supplied description policy.
- Replaced the global inferred-type Class maps with ClassValue associations and verified cache/global ownership.
- Verified additional application values through standard typed events. V2 release hardening remains separate.

## 0.0.1 - 2026-10-22
- Completed the V1 representative workflow with relationship/evolution rules and retryable recovery after transaction or configuration failure.
- Released the library as a locally installed Maven artifact and verified Night City as an independent consumer and reactor module.
- Recorded V1 hardening, artifact hygiene, shutdown/resource checks, and three-sample consumer workflow measurements.
- Moved Night City to `examples/night_city/` as shared test sources; example classes no longer ship in library JARs.
- Established topic designs/specifications, an agent documentation-first process, and V1/V2 epics and sprint plans.
- Added reproducible performance run records and topic history using the existing benchmark profile.
- Added CONTRIBUTING.md, CODE_OF_CONDUCT.md, and SUPPORT.md for contributor and user guidance.
- Added SECURITY.md and Apache-2.0 LICENSE for security reporting and licensing clarity.
- Added GitHub issue templates and a pull request template.
- Expanded [Readme.md](Readme.md) community links.
- Standardized operation key terminology across docs and error messages.
- Added transaction quickstart examples to [Readme.md](Readme.md) and [Usage.md](docs/Usage.md).
