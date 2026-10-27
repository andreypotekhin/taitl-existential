package com.taitl.ex.logic.evaluation.events.split_events;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.evaluation.events.split_events.maps.*;
import com.taitl.ex.logic.evaluation.events.split_events.rules.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Binds a reusable rule selection to the current entity's executable event payload.
 * A compact DispatchSignature reaches EventField before structural type or event-family
 * expansion. A miss resolves selection facts; a hit reuses immutable declaration order.
 *
 * Memo requirements are checked for every entity even when its selection is already cached.
 * Before/after payload conversion also occurs per call, so shared selections never cache
 * entity validity or another transaction's memo values.
 */
public class SplitEvent
{
    @Logic
    protected ResolveSelectionInput resolveSelectionInput = Creator.create(ResolveSelectionInput.class);

    @Logic
    protected ToMemo toMemo = Creator.create(ToMemo.class);

    public <T> SplitResult<T> call(
            RuntimeKey<T> runtimeKey,
            EventField eventField,
            boolean useFullEventNames,
            boolean splitElementaryToCompound,
            Tr tr)
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        boolean memo = tr != null && runtimeKey.entity() != null
                && tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey());
        DispatchSignature signature = new DispatchSignature(runtimeKey, useFullEventNames,
                splitElementaryToCompound, tr != null, memo);
        RuleSelection selection = eventField.select(signature,
                () -> resolveSelectionInput.call(runtimeKey, useFullEventNames, splitElementaryToCompound, tr));
        if (selection.memoRequired())
        {
            toMemo.requireMemo(runtimeKey, tr);
        }
        List<Ev<T>> evaluables = selection.handlers();
        Event<T> event = tr != null ? toMemo.forExecution(runtimeKey, selection.biHandler(), tr) : runtimeKey.event();
        SplitResult<T> result = new SplitResult<>(evaluables, event);
        result.selection = selection;
        return result;
    }

    public <T> boolean hasEffects(
            RuntimeKey<T> runtimeKey,
            EventField eventField,
            boolean useFullEventNames,
            boolean splitElementaryToCompound,
            Tr tr)
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        boolean memo = tr != null && runtimeKey.entity() != null
                && tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey());
        DispatchSignature signature = new DispatchSignature(runtimeKey, useFullEventNames,
                splitElementaryToCompound, tr != null, memo);
        RuleSelection selection = eventField.select(signature,
                () -> resolveSelectionInput.call(runtimeKey, useFullEventNames, splitElementaryToCompound, tr));
        return selection.hasEffects();
    }

}
