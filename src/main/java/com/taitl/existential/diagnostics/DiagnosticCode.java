package com.taitl.existential.diagnostics;

/** Stable categories for structured runtime failures; descriptions identify consumer rules separately. */
public enum DiagnosticCode
{
    /** Ordinary failed check or expression. */
    RULE_REJECTED,
    /** A matching intent rejected the event. */
    INTENT_REJECTED,
    /** A guarded event has no matching intent. */
    INTENT_MISSING,
    /** A rule or lifecycle callback failed unexpectedly. */
    CALLBACK_FAILED,
    /** An evaluation boundary collected failures. */
    EVALUATION_FAILED,
    /** A completed handle rejected another operation. */
    TRANSACTION_CLOSED,
    /** A failed handle permits only rollback or close. */
    TRANSACTION_ROLLBACK_ONLY,
    /** An ID has no active transaction registry entry. */
    TRANSACTION_NOT_FOUND,
    /** Configuration sources, declarations or required settings are invalid. */
    CONFIGURATION_INVALID,
    /** Finalized configuration rejected a change. */
    CONFIGURATION_FROZEN,
    /** Shutdown has stopped admission of new work. */
    LIBRARY_CLOSED,
    /** Active work did not drain before the configured deadline. */
    SHUTDOWN_TIMEOUT,
    /** Waiting for active work was interrupted. */
    SHUTDOWN_INTERRUPTED,
    /** A callback attempted a lifecycle action that would wait on itself. */
    REENTRANT_LIFECYCLE,
    /** A before-state snapshot is invalid or already registered. */
    MEMO_INVALID,
    /** Evaluation requires absent before-state. */
    MEMO_MISSING,
    /** A managed index could not maintain its contents. */
    INDEX_MAINTENANCE_FAILED,
    /** Resource cleanup or explicit rollback failed. */
    CLEANUP_FAILED;

    /**
     * Returns the stable, unprefixed display identifier.
     *
     * @return identifier such as RULE-REJECTED
     */
    public String id()
    {
        return name().replace('_', '-');
    }
}
