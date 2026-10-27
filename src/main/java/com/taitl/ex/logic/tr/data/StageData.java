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
    protected EventField intentOverlay;

    public void preparedIndexes(ConfigurationIndexes preparedIndexes)
    {
        sane(preparedIndexes, "preparedIndexes");
        if (this.preparedIndexes != null)
        {
            this.preparedIndexes.close();
        }
        this.preparedIndexes = preparedIndexes;
        if (overlay != null)
        {
            overlay.close();
        }
        overlay = null;
        if (intentOverlay != null)
        {
            intentOverlay.close();
        }
        intentOverlay = null;
    }

    public boolean hasPreparedIndexes()
    {
        return preparedIndexes != null;
    }

    public boolean hasPreparedHandlers()
    {
        return preparedIndexes != null && preparedIndexes.hasEventHandlers();
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

    public EventField intentField(EventField base)
    {
        if (preparedIndexes == null)
        {
            return base;
        }
        if (intentOverlay == null)
        {
            intentOverlay = Creator.create(EventFieldOverlay.class,
                    new Class[] { EventField.class, EventField.class }, base, preparedIndexes.intentField());
        }
        return intentOverlay;
    }

    public void close()
    {
        if (preparedIndexes != null)
        {
            preparedIndexes.close();
            preparedIndexes = null;
        }
        if (overlay != null)
        {
            overlay.close();
        }
        overlay = null;
        if (intentOverlay != null)
        {
            intentOverlay.close();
        }
        intentOverlay = null;
        intentEventTypes.clear();
        biIntentKeys.clear();
        intentHandlers.clear();
    }
}
