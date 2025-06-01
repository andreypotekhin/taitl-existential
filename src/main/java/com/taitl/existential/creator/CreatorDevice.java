package com.taitl.existential.creator;

import java.util.*;
import java.util.function.*;

import com.taitl.existential.helper.*;

import static com.taitl.existential.helper.Args.cool;
import static com.taitl.existential.helper.Args.require;
import static com.taitl.existential.helper.State.verify;

/**
 * Default implementation for Creator component. Extend this class to customize class instantiation,
 * e.g. to provide a custom class instead of the specified class, to initialize/augment the newly 
 * created instance, to provide singletons and other scopes, or to integrate with an existing 
 * dependency injection library.
 */
public class CreatorDevice
{
    /**
     * Registry for singleton objects, keyed by class canonical name.
     */
    protected Map<String, Object> singletons = new LinkedHashMap<>();

    /**
     * Registry for supplier objects, keyed by class canonical name.
     */
    protected Map<String, Supplier<?>> suppliers = new LinkedHashMap<>();

    /**
     * Remember created classes to avoid creating before injection.
     * See inject() method for details.
     */
    protected Set<String> created = new HashSet<>();

    /**
     * Provides a singleton instance of the specified class.
     *
     * @param <T> Type to create
     * @param cls Class object to create
     * @return New or existing instance
     */
    @SuppressWarnings("unchecked")
    public <T> T singleton(Class<T> cls)
    {
        cool(cls, "cls");
        String className = cls.getCanonicalName();
        T existing = (T) singletons.get(className);
        if (existing != null)
        {
            return existing;
        }

        T result;
        Supplier<? extends T> supplier = getSupplier(cls);

        if (supplier != null)
        {
            result = supplier.get();
            created.add(className);
        }
        else
        {
            try
            {
                result = cls.getDeclaredConstructor().newInstance();
                created.add(className);
            }
            catch (ReflectiveOperationException e)
            {
                throw new IllegalArgumentException(
                        "Could not create an instance of class " + className, e);
            }
        }

        synchronized (singletons)
        {
            return (T) singletons.computeIfAbsent(className, key -> result);
        }
    }

    /**
     * Creates a new instance of the specified class, optionally using constructor parameters.
     *
     * @param <T>        Type to provide
     * @param cls        Class instance to create
     * @param paramTypes Parameter types for the constructor
     * @param initargs   Arguments for the constructor
     * @return Newly created instance
     */
    public <T> T create(Class<T> cls, Class<?>[] paramTypes, Object... initargs)
    {
        cool(cls, "cls");
        if (paramTypes == null || initargs == null)
        {
            require(paramTypes == null && initargs == null,
                    "If paramTypes is specified, initargs must also be specified");
        }

        String className = cls.getCanonicalName();
        Supplier<? extends T> supplier = getSupplier(cls);

        if (supplier != null)
        {
            // Assuming the supplier can handle parameterized constructors
            if (supplier instanceof BiFunction<?, ?, ?>)
            {
                @SuppressWarnings("unchecked")
                BiFunction<Class<?>[], Object[], T> paramSupplier = (BiFunction<Class<?>[], Object[], T>) supplier;
                T result = paramSupplier.apply(paramTypes, initargs);
                created.add(className);
                return result;
            }
            else
            {
                T result = supplier.get();
                created.add(className);
                return result;
            }
        }

        try
        {
            T result;
            if (paramTypes == null || initargs == null)
            {
                result = cls.getDeclaredConstructor().newInstance();
            }
            else
            {
                result = cls.getDeclaredConstructor(paramTypes).newInstance(initargs);
            }
            created.add(className);
            return result;
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException(
                    "Could not create an instance of class " + className, e);
        }
    }

    /**
     * Creates new instance of the specified class.
     *
     * @param <T> Type to provide
     * @param cls Class instance to create
     * @return Newly created instance
     */
    public <T> T create(Class<T> cls)
    {
        cool(cls, "cls");
        String className = cls.getCanonicalName();
        Supplier<? extends T> supplier = getSupplier(cls);
        if (supplier != null)
        {
            T result = supplier.get();
            created.add(className);
            return result;
        }

        try
        {
            T result = cls.getDeclaredConstructor().newInstance();
            created.add(className);
            return result;
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException(
                    "Could not create an instance of class " + className, e);
        }
    }

    /**
     * Gets supplier function for the specified class.
     * Use inject() to set the supplier for a class.
     * Suppliers are keyed by their class canonical name.
     * Canonical name example: java.util.AbstractMap.SimpleEntry
     * 
     * @param <T> Type to supply
     * @param cls The class to get supplier for
     * @return Custom supplier, or null if no custom supplier was specified
     */
    @SuppressWarnings("unchecked")
    public <T> Supplier<? extends T> getSupplier(Class<T> cls)
    {
        cool(cls, "cls");
        return (Supplier<? extends T>) suppliers.get(cls.getCanonicalName());
    }

    /**
     * Returns true if supplier function for the specified class is set.
     * Use inject() to set the supplier for a class.
     * Suppliers are keyed by their class canonical name.
     * Canonical name example: java.util.AbstractMap.SimpleEntry
     *
     * @param cls The class to get supplier for
     * @return True if supplier for this class was injected
     */
    public boolean hasSupplier(Class<?> cls)
    {
        cool(cls, "cls");
        return suppliers.containsKey(cls.getCanonicalName());
    }

    /**
     * Sets (overwrites) supplier function for the specified class.
     * 
     * @param <T> Type to supply
     * @param supplier Custom supplier function
     * @param cls The class to get supplier for
     */
    public <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        cool(cls, "cls");
        cool(supplier, "supplier");
        String className = cls.getCanonicalName();
        verify(!created.contains(className),
                "Cannot make injection for class " + cls.getCanonicalName() +
                        " because create() or singleton() were already called for it");
        suppliers.put(cls.getCanonicalName(), supplier);
    }
}
