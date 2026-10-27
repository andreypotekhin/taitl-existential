package com.taitl.existential;

/**
 * Immutable defaults or overrides for transaction-local relationship maintenance.
 * Unspecified properties inherit independently from the enclosing index, transaction, context,
 * and library scopes. Resolved policies belong to one active transaction and never change
 * maintenance already performed. Automatic maintenance and read enrollment default to enabled.
 */
public final class IndexingPolicy
{
    private final Boolean automatic;
    private final Boolean enrollReads;

    private IndexingPolicy(Boolean automatic, Boolean enrollReads)
    {
        this.automatic = automatic;
        this.enrollReads = enrollReads;
    }

    /**
     * Returns a policy that inherits both properties.
     *
     * @return immutable empty overrides
     */
    public static IndexingPolicy inherit()
    {
        return new IndexingPolicy(null, null);
    }

    /**
     * Returns the library defaults with both properties enabled.
     *
     * @return fully resolved default policy
     */
    public static IndexingPolicy defaults()
    {
        return new IndexingPolicy(true, true);
    }

    /**
     * Returns a copy with explicit automatic-maintenance behavior.
     *
     * @param enabled whether events automatically maintain this index
     * @return new policy retaining the read override
     */
    public IndexingPolicy automatic(boolean enabled)
    {
        return new IndexingPolicy(enabled, enrollReads);
    }

    /**
     * Returns a copy with explicit read-event enrollment behavior.
     *
     * @param enabled whether reads enroll when automatic maintenance is enabled
     * @return new policy retaining the automatic override
     */
    public IndexingPolicy enrollReads(boolean enabled)
    {
        return new IndexingPolicy(automatic, enabled);
    }

    /**
     * Returns the automatic-maintenance override, or null to inherit.
     *
     * @return nullable automatic override
     */
    public Boolean automatic()
    {
        return automatic;
    }

    /**
     * Returns the read-enrollment override, or null to inherit.
     *
     * @return nullable read override
     */
    public Boolean enrollReads()
    {
        return enrollReads;
    }

    /**
     * Fills unspecified properties from broader defaults without changing either policy.
     *
     * @param broader non-null enclosing policy
     * @return merged policy; properties absent from both remain inherited
     * @throws IllegalArgumentException if broader is null
     */
    public IndexingPolicy over(IndexingPolicy broader)
    {
        if (broader == null)
        {
            throw new IllegalArgumentException("Indexing defaults must not be null");
        }
        return new IndexingPolicy(automatic != null ? automatic : broader.automatic,
                enrollReads != null ? enrollReads : broader.enrollReads);
    }
}
