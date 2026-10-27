package com.taitl.existential.diagnostics;

/** Stable categories for structured evaluation failures; descriptions identify consumer rules separately. */
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
    EVALUATION_FAILED;

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
