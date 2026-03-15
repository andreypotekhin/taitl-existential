package com.taitl.ex.logic.tr.data;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.tr.keys.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class StageData
{
    public final Set<EventType> intentEventTypes = new LinkedHashSet<>();
    public final Set<IntentHandlerKey> biIntentKeys = new LinkedHashSet<>();
    public final ListMap<IntentHandlerKey, EventHandler<?>> intentHandlers = new ListMap<>();
    protected ConfigurationIndexes preparedIndexes;
    protected EventField overlay;

    public void preparedIndexes(ConfigurationIndexes preparedIndexes)
    {
        sane(preparedIndexes, "preparedIndexes");
        if (this.preparedIndexes != null)
        {
            this.preparedIndexes.close();
        }
        this.preparedIndexes = preparedIndexes;
        overlay = null;
    }

    public boolean hasPreparedIndexes()
    {
        return preparedIndexes != null;
    }

    public EventField eventField(EventField base)
    {
        sane(base, "base");
        if (preparedIndexes == null)
        {
            return base;
        }
        if (overlay != null)
        {
            return overlay;
        }

        overlay = Creator.create(EventFieldOverlay.class,
                new Class[] { EventField.class, EventField.class },
                base,
                preparedIndexes.eventField());
        return overlay;
    }

    public void close()
    {
        if (preparedIndexes != null)
        {
            preparedIndexes.close();
            preparedIndexes = null;
        }
        overlay = null;
    }
}
