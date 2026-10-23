package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;

import java.util.*;

/**
 * Transient structural facts needed to resolve a cold handler selection.
 * Reported type patterns and available event families select handlers; separate memo-sensitive
 * families and subject types preserve missing-memo admission checks for the declared subject.
 *
 * ResolveSelectionInput creates this value only after a compact-signature cache miss.
 * It carries type metadata and event names without keeping entity, event, or transaction objects,
 * and the completed RuleSelection does not retain the input.
 */
public class SelectionInput
{
    public Set<TypePattern> types;
    public Set<String> events;
    public Set<String> memoEvents;
    public Set<TypePattern> memoTypes;

    public SelectionInput(Set<TypePattern> types, Set<String> events, Set<String> memoEvents)
    {
        this.types = types;
        this.events = events;
        this.memoEvents = memoEvents;
        this.memoTypes = types;
    }
}
