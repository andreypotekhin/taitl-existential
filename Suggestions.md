### S02192602 Error Message Troubleshooting Links
Introduce a small, consistent error-id format for public exceptions and append a Troubleshooting.md deep-link to
configuration and validation failures. This gives end users a single hop to remediation steps while keeping
exception messages actionable and stable across releases.

### S02192603 Add Security Policy Document
Create a `SECURITY.md` that explains how to report vulnerabilities privately, expected response timelines, and
supported versions. Link to it from `Readme.md` so OSS users know how to disclose issues without opening
public tickets.

### S02192602 Publish License and Source Availability
Add an explicit OSS license file and a short Readme section that states the license and any contribution
requirements. This reduces legal ambiguity for adopters and helps downstream users vet compatibility early.

### S02192602 Stable Rule Identity + Digest
Introduce a canonical rule identity and digest (for example, based on context, stage, event signature, and predicate
source metadata) that remains stable across runs. This enables deterministic caching, clearer diagnostics, and
safe incremental reload by matching rules without relying on object identity or registration order.

### S02192601 Security Log Redaction Policy
Introduce a configurable redaction policy for logging that can mask or drop values marked as sensitive (tokens,
secrets, PII) before formatting log output, with a default policy that keeps safe fields visible while preventing
accidental disclosure.

### S02182610 Immutable Rule Snapshot + Hot Reload
Introduce an immutable, compiled rule snapshot that indexes rules by context, stage, and event signature, allowing lock-free evaluation on the hot path while enabling safe hot reload by swapping snapshots. This separates configuration from execution, eliminates write locks during runtime, and makes it feasible to add validation, caching, and observability hooks without impacting transaction latency.

### S02182604 Fail-Fast Stubs for Unimplemented Actions

Replace TODO-marked no-op action classes (e.g., event receive/ignore/execute actions) with fail-fast implementations
that throw a descriptive exception and deep-link to the relevant Troubleshooting entry. This reduces silent misbehavior
during partial implementations while keeping development progress visible and actionable.

### S02182604 Deterministic Execution Plan Compiler

Introduce an execution plan compiler that resolves the applicable contexts, transactions, stages, and rule ordering into
an immutable plan at begin() time, then caches it by op name and config version. This yields deterministic evaluation
order, a single place to detect conflicts or missing stages, and a concise plan summary for debugging, while keeping
runtime execution lean and predictable.

### S02182604 Event Queue Deque Backing

Replace the current EventQueue base type with a deque-backed structure (e.g. ArrayDeque of event sets) and expose
explicit enqueue/dequeue operations to avoid ArrayList growth churn and improve cache-friendly traversal for
transaction-close handling; keep API surface minimal and documented for batching behavior.


### S02182604 Immutable Rule Snapshot + Hot Reload

Introduce an immutable, compiled rule snapshot that indexes rules by context, stage, and event signature, allowing
lock-free evaluation on the hot path while enabling safe hot reload by swapping snapshots. This separates configuration
from execution, eliminates write locks during runtime, and makes it feasible to add validation, caching, and
observability hooks without impacting transaction latency.

### S02182605 Transaction Membership Index

Track a parallel membership set for `Tr.transactions` (for example, an `IdentityHashMap`-backed `Set`) to avoid O(n)
scans in `addTransaction` while preserving order in the list, keeping transaction creation fast even with many contexts.

### S02182603 Context Matching Strategy Registry

Introduce a pluggable context matching strategy registry that defines precedence (exact, parent, wildcard) and detects
ambiguous matches upfront. This keeps default wildcard matching behavior intact while enabling users to inject stricter
matching (regex, segment-aware) for large API surfaces and to surface configuration errors early with clear diagnostics
that link to the matching rules documentation.

### S02182602 Validation Report + Trace Surface

Add a structured validation report surface that records which contexts, transactions, stages, and rules executed, plus
violated constraints and implicated entities. Expose this via a report object (and optional hooks) so applications can
log or export diagnostics without relying on exceptions alone, enabling monitoring and faster root-cause analysis for
complex rule sets.

### S02182601 Config Source + Preflight Validator
<<<<<<< HEAD
Design a configuration source loader that supports file, classpath resource, and environment variable selection with
a single resolution path, then run a preflight validator on startup. The validator should confirm required options,
warn on conflicting flags, and emit actionable errors that deep-link to Troubleshooting entries for quick remediation.

### S02192601 Security Log Redaction Policy

Introduce a configurable redaction policy for logging that can mask or drop values marked as sensitive (tokens, secrets,
PII) before formatting log output, with a default policy that keeps safe fields visible while preventing accidental
disclosure.
