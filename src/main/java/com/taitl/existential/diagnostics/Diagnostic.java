package com.taitl.existential.diagnostics;

import com.taitl.existential.constants.*;
import java.util.*;
import java.io.*;

/**
 * Read-only descriptive snapshot of an evaluation failure. Optional scalar values may be null;
 * lists are non-null. Library snapshots are immutable, serializable, and retain no live evaluation state.
 */
public interface Diagnostic extends Serializable
{
    /**
     * Stable library failure category.
     *
     * @return stable library failure category.
     */
    DiagnosticCode code();

    /**
     * Concise primary message, without rendered fields.
     *
     * @return concise primary message, without rendered fields.
     */
    String message();

    /**
     * Reported operation, or null when unknown.
     *
     * @return reported operation, or null when unknown.
     */
    String operation();

    /**
     * Existing rule description, or null when absent.
     *
     * @return existing rule description, or null when absent.
     */
    String rule();

    /**
     * Original declaration context or pattern, or null when absent.
     *
     * @return original declaration context or pattern, or null when absent.
     */
    String context();

    /**
     * Whether the rule was declared in the transaction.
     *
     * @return whether the rule was declared in the transaction.
     */
    boolean transactionLocal();

    /**
     * Reported event name, or null when absent.
     *
     * @return reported event name, or null when absent.
     */
    String event();

    /**
     * Reported type evidence, or null when absent.
     *
     * @return reported type evidence, or null when absent.
     */
    String type();

    /**
     * Selected declaration event, or null when absent.
     *
     * @return selected declaration event, or null when absent.
     */
    String ruleEvent();

    /**
     * Selected declaration type, or null when absent.
     *
     * @return selected declaration type, or null when absent.
     */
    String ruleType();

    /**
     * Configured evaluation stage, or null when unknown.
     *
     * @return configured evaluation stage, or null when unknown.
     */
    StageName stage();

    /**
     * Triggering report/begin/checkpoint/commit/rollback boundary, or null.
     *
     * @return triggering report/begin/checkpoint/commit/rollback boundary, or null.
     */
    String boundary();

    /**
     * Whether failure is known to have stopped this evaluation.
     *
     * @return whether failure is known to have stopped this evaluation.
     */
    boolean evaluationStopped();

    /**
     * Immutable ordered notes about actual consequences.
     *
     * @return immutable ordered notes about actual consequences.
     */
    List<String> notes();

    /**
     * Immutable ordered corrective actions, possibly empty.
     *
     * @return immutable ordered corrective actions, possibly empty.
     */
    List<String> actions();

    /**
     * Relevant documentation path, or null when absent.
     *
     * @return relevant documentation path, or null when absent.
     */
    String troubleshooting();

    /**
     * Immutable ordered sibling diagnostics, possibly empty.
     *
     * @return immutable ordered sibling diagnostics, possibly empty.
     */
    List<Diagnostic> children();

}
