package com.taitl.ex.cross.caching;

public class CacheHandlers
{
    public void cacheHandlersPerOp()
    {
        throw notImplemented("cacheHandlersPerOp");
    }

    public void cacheHandlersPerContext()
    {
        throw notImplemented("cacheHandlersPerContext");
    }

    public void cacheHandlersPerTran()
    {
        throw notImplemented("cacheHandlersPerTran");
    }

    private UnsupportedOperationException notImplemented(String methodName)
    {
        return new UnsupportedOperationException(
                "CacheHandlers." + methodName + "() is not implemented yet. " +
                        "See /Troubleshooting.md#cache-handlers-not-implemented");
    }
}
