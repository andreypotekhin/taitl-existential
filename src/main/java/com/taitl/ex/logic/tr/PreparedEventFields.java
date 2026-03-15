package com.taitl.ex.logic.tr;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.constants.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Holds transaction-local handler overlays keyed by execution stage.
 */
public class PreparedEventFields
{
    protected Map<StageName, ConfigurationIndexes> indexes = new EnumMap<>(StageName.class);
    protected Map<StageName, EventField> overlays = new EnumMap<>(StageName.class);

    public void put(StageName stageName, ConfigurationIndexes configurationIndexes)
    {
        sane(stageName, "stageName", configurationIndexes, "configurationIndexes");
        indexes.put(stageName, configurationIndexes);
        overlays.remove(stageName);
    }

    public boolean has(StageName stageName)
    {
        sane(stageName, "stageName");
        return indexes.containsKey(stageName);
    }

    public EventField eventField(StageName stageName, EventField base)
    {
        sane(stageName, "stageName", base, "base");
        ConfigurationIndexes prepared = indexes.get(stageName);
        if (prepared == null)
        {
            return base;
        }

        EventField overlay = overlays.get(stageName);
        if (overlay != null)
        {
            return overlay;
        }

        overlay = Creator.create(OverlayEventField.class,
                new Class[] { EventField.class, EventField.class },
                base,
                prepared.eventField());
        overlays.put(stageName, overlay);
        return overlay;
    }

    public void close()
    {
        for (ConfigurationIndexes configurationIndexes : indexes.values())
        {
            configurationIndexes.close();
        }
        indexes.clear();
        overlays.clear();
    }
}
