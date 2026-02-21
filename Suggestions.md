### S02212601 Public Lifecycle Status Dashboard
Expose a minimal, immutable lifecycle status object (for example, configuration state, active transaction count,
and last validation result) that can be queried from the public API and logged for diagnostics. This keeps runtime
observability explicit and makes it easier for users to detect mis-ordered calls while preserving strict phase
boundaries and fail-fast behavior.

### S02202603 Add Security Static Analysis
Introduce automated security static analysis (for example, SpotBugs + FindSecBugs with a curated exclude file)
into the build and CI pipeline, failing on high-confidence issues while documenting suppression policy for
accepted risks.

### S02202604 Prefer HashMap for TransactionIndexes
Transaction-scoped indexes are created and used within a single business transaction, so the current
ConcurrentHashMap in `TransactionIndexes` likely adds contention and allocation overhead without delivering
value. Consider switching to `HashMap` (or a small, pre-sized map) and explicitly documenting that transaction
indexes are thread-confined, keeping the hot path lean while preserving semantics.

### S02202601 Encapsulate Handler State
Convert public fields in `com.taitl.existential.handlers.On` into accessors or an immutable value object with a
builder-style construction so handler state cannot be mutated after registration. This avoids accidental nulling of
actions or conditions and keeps execute-time invariants crisp and predictable.

### S02182607 Event Queue Deque Backing
Replace the current EventQueue base type with a deque-backed structure (for example, an ArrayDeque of event
sets) and expose explicit enqueue/dequeue operations to avoid ArrayList growth churn and improve cache-friendly
traversal for transaction-close handling; keep the API surface minimal and documented for batching behavior.

### S02192604 Publish License and Source Availability
Add an explicit OSS license file and a short Readme section that states the license and any contribution
requirements. This reduces legal ambiguity for adopters and helps downstream users vet compatibility early.

### S02202602 Stable Rule Identity + Digest
Introduce a canonical rule identity and digest (for example, based on context, stage, event signature, and
predicate source metadata) that remains stable across runs. This enables deterministic caching, clearer
diagnostics, and safe incremental reload by matching rules without relying on object identity or registration
order.

### S02182610 Immutable Rule Snapshot + Hot Reload
Introduce an immutable, compiled rule snapshot that indexes rules by context, stage, and event signature,
allowing lock-free evaluation on the hot path while enabling safe hot reload by swapping snapshots. This
separates configuration from execution, eliminates write locks during runtime, and makes it feasible to add
validation, caching, and observability hooks without impacting transaction latency.

### S02182605 Transaction Membership Index
Track a parallel membership set for `Tr.transactions` (for example, an IdentityHashMap-backed Set) to avoid
O(n) scans in `addTransaction` while preserving order in the list, keeping transaction creation fast even with
many contexts.

### -S02182604 Fail-Fast Stubs for Unimplemented Actions
Replace TODO-marked no-op action classes (for example, event receive/ignore/execute actions) with fail-fast
implementations that throw a descriptive exception and deep-link to the relevant Troubleshooting entry. This
reduces silent misbehavior during partial implementations while keeping development progress visible and
actionable.

### -S02182603 Context Matching Strategy Registry
Introduce a pluggable context matching strategy registry that defines precedence (exact, parent, wildcard) and
detects ambiguous matches upfront. This keeps default wildcard matching behavior intact while enabling users
to inject stricter matching (regex, segment-aware) for large API surfaces and to surface configuration errors
early with clear diagnostics that link to the matching rules documentation.

### S02182602 Validation Report + Trace Surface
Add a structured validation report that records which contexts, transactions, stages, and rules
executed, plus violated constraints and implicated entities. Expose this via a report object (and optional
hooks) so applications can log or export diagnostics without relying on exceptions alone, enabling monitoring
and faster root-cause analysis for complex rule sets.

### S02182601 Log redaction
Introduce a configurable redaction policy for logging that can mask or drop values marked as sensitive (tokens, secrets,
PII) before formatting log output, with a default policy that keeps safe fields visible while preventing accidental
disclosure.

### S02202605 Dependency Vulnerability Scans
Add automated dependency vulnerability scanning (for example, OWASP Dependency-Check or equivalent) to the build
and CI pipeline, failing builds on high-severity findings while documenting suppression policy for accepted risks.
### S02212601 Enforce Secure Config File Permissions
Before loading configuration from the filesystem, check that the file is owned by the current user (when
available) and is not group/world writable. Fail fast with a clear error that links to the troubleshooting
entry and suggests how to fix permissions, preventing accidental use of tampered configuration in shared
environments.
