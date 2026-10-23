package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;

import java.util.*;

/**
 * Owns a bounded cache of complete operation-specific stage indexes.
 * All stages are compiled before an immutable map is published; a compilation failure closes
 * its temporary indexes and leaves no partial cache entry. Concurrent cold requests may build
 * equivalent inventories, while cache hits avoid the insertion lock.
 *
 * Eviction releases cache ownership without closing a generation still used by an evaluation.
 * Library shutdown drains transactions first, then closes resident indexes and clears the cache.
 * The cache is scoped to one configuration, so operations from separate instances cannot mix.
 */
public class OperationIndexes
{
    public static final int CAPACITY = 128;
    protected BoundedCache<String, Map<StageName, ConfigurationIndexes>> entries = new BoundedCache<>(CAPACITY);

    public Map<StageName, ConfigurationIndexes> resolve(String op, Config config, boolean fullNames)
    {
        return entries.resolve(op, () -> compile(op, config, fullNames));
    }

    protected Map<StageName, ConfigurationIndexes> compile(String op, Config config, boolean fullNames)
    {
        Map<StageName, ConfigurationIndexes> result = new EnumMap<>(StageName.class);
        try
        {
            for (StageName stage : StageName.values())
            {
                ConfigurationIndexes indexes = Creator.create(ConfigurationIndexes.class);
                result.put(stage, indexes);
                indexes.useFullClassNames(fullNames);
                indexes.indexConfig(op, config, stage);
            }
            return Collections.unmodifiableMap(result);
        }
        catch (RuntimeException | Error failure)
        {
            for (ConfigurationIndexes indexes : result.values())
            {
                indexes.close();
            }
            throw failure;
        }
    }

    public Collection<Map<StageName, ConfigurationIndexes>> values()
    {
        return entries.values();
    }

    public int size()
    {
        return entries.size();
    }

    public void close()
    {
        for (Map<StageName, ConfigurationIndexes> stages : entries.values())
        {
            for (ConfigurationIndexes indexes : stages.values())
            {
                indexes.close();
            }
        }
        entries.clear();
    }
}
