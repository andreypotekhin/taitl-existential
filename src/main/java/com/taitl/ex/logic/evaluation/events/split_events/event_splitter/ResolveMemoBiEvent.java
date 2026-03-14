package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class ResolveMemoBiEvent
{
    public static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#memo-state-missing";

    public <T> Event<T> forSplit(Event<T> splitEvent, RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(splitEvent, "splitEvent", runtimeKey, "runtimeKey");
        if (!(splitEvent instanceof BiEvent<?>))
        {
            return splitEvent;
        }
        if (splitEvent instanceof Transit<?>)
        {
            if (!canBuildTransit(runtimeKey, tr))
            {
                return null;
            }
            return transit(runtimeKey, tr);
        }
        if (splitEvent instanceof Port<?>)
        {
            if (!canBuildPort(runtimeKey, tr))
            {
                return null;
            }
            return port(runtimeKey, tr);
        }
        return splitEvent;
    }

    public <T> Event<T> forExecution(RuntimeKey<T> runtimeKey, List<Ev<T>> evs, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", evs, "evs");
        Event<T> event = runtimeKey.event();
        if (event instanceof BiEvent<?> || !containsBiHandler(evs))
        {
            return event;
        }
        if (event instanceof Create<?> || event instanceof Delete<?>)
        {
            return port(runtimeKey, tr);
        }
        if (event instanceof Update<?> || event instanceof CU<?> || event instanceof UD<?> || event instanceof CUD<?>)
        {
            return transit(runtimeKey, tr);
        }
        return event;
    }

    public <T> boolean memoSensitive(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        return event instanceof Update<?>
                || event instanceof Delete<?>
                || event instanceof CU<?>
                || event instanceof UD<?>
                || event instanceof CUD<?>;
    }

    protected <T> boolean containsBiHandler(List<Ev<T>> evs)
    {
        sane(evs, "evs");
        for (Ev<T> ev : evs)
        {
            if (ev instanceof BiEventHandler<?>)
            {
                return true;
            }
        }
        return false;
    }

    protected <T> Event<T> transit(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        if (event instanceof Transit<?>)
        {
            return event;
        }
        if (event instanceof Port<?>)
        {
            BiEvent<T> biEvent = biEvent(event);
            if (biEvent.t0 != null && biEvent.t1 != null)
            {
                return new Transit<>(biEvent.t0, biEvent.t1);
            }
            return event;
        }
        return new Transit<>(memo(runtimeKey, tr), runtimeKey.entity());
    }

    protected <T> Event<T> port(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey");
        Event<T> event = runtimeKey.event();
        if (event instanceof Port<?>)
        {
            return event;
        }
        if (event instanceof Transit<?>)
        {
            BiEvent<T> biEvent = biEvent(event);
            return new Port<>(biEvent.t0, biEvent.t1);
        }
        if (event instanceof Create<?>)
        {
            return new Port<>(null, runtimeKey.entity());
        }
        if (event instanceof Delete<?>)
        {
            return new Port<>(memo(runtimeKey, tr), null);
        }
        return new Port<>(memo(runtimeKey, tr), runtimeKey.entity());
    }

    protected <T> T memo(RuntimeKey<T> runtimeKey, Tr tr) throws MemoException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        T entity = runtimeKey.entity();
        TypeKey<T> typeKey = runtimeKey.typeKey();
        T memo = tr.beforeState(entity, typeKey);
        if (memo != null)
        {
            return memo;
        }
        throw new MemoException("Memo state not present for event '" + runtimeKey.key()
                + "'. Use memo() to register the before-state for this entity before mutating it. See "
                + TROUBLESHOOTING_SECTION);
    }

    @SuppressWarnings("unchecked")
    protected <T> BiEvent<T> biEvent(Event<T> event)
    {
        sane(event, "event");
        return (BiEvent<T>) event;
    }

    protected <T> boolean canBuildTransit(RuntimeKey<T> runtimeKey, Tr tr)
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        Event<T> event = runtimeKey.event();
        if (event instanceof Transit<?>)
        {
            return true;
        }
        if (event instanceof Port<?>)
        {
            BiEvent<T> biEvent = biEvent(event);
            return biEvent.t0 != null && biEvent.t1 != null;
        }
        return (event instanceof Update<?> || event instanceof CU<?> || event instanceof UD<?>
                || event instanceof CUD<?>)
                && tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey());
    }

    protected <T> boolean canBuildPort(RuntimeKey<T> runtimeKey, Tr tr)
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        Event<T> event = runtimeKey.event();
        if (event instanceof Port<?> || event instanceof Transit<?> || event instanceof Create<?>)
        {
            return true;
        }
        return memoSensitive(runtimeKey) && tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey());
    }
}
