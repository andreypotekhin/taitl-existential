package com.taitl.ex.common.creator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Default implementation for Creator component. Extend this class to customize class instantiation,
 * e.g. to provide a custom class instead of the specified class, to initialize/augment the newly 
 * created instance, to provide singletons and other scopes, or to integrate with an existing 
 * dependency injection library.
 * Thread-safe for concurrent singleton creation and supplier lookup. For predictable injection
 * behavior, call inject() during configuration before concurrent create/singleton calls.
 */
public class CreatorDevice
{
    private static final class SupplierResult<T>
    {
        private final boolean used;
        private final T value;

        private SupplierResult(boolean used, T value)
        {
            this.used = used;
            this.value = value;
        }

        private static <T> SupplierResult<T> used(T value)
        {
            return new SupplierResult<>(true, value);
        }

        private static <T> SupplierResult<T> notUsed()
        {
            return new SupplierResult<>(false, null);
        }
    }

    /**
     * Registry for singleton objects, keyed by class binary name.
     */
    protected Map<String, Object> singletons = new ConcurrentHashMap<>();

    /**
     * Registry for supplier objects, keyed by class binary name.
     */
    protected Map<String, Supplier<?>> suppliers = new ConcurrentHashMap<>();

    /**
     * Remember created classes to avoid creating before injection.
     * See inject() method for details.
     */
    protected Set<String> created = ConcurrentHashMap.newKeySet();
    private final Object injectionLock = new Object();

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
        sane(cls, "cls");
        String className = classKey(cls);
        Supplier<? extends T> supplier = getSupplier(cls);
        synchronized (singletons)
        {
            return (T) singletons.computeIfAbsent(className,
                    key -> createWithSupplierOrReflect(cls, className, supplier, null, null));
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
        sane(cls, "cls");
        if (paramTypes == null || initargs == null)
        {
            check(paramTypes == null && initargs == null,
                    "If paramTypes is specified, initargs must also be specified");
        }

        String className = classKey(cls);
        Supplier<? extends T> supplier = getSupplier(cls);
        return createWithSupplierOrReflect(cls, className, supplier, paramTypes, initargs);
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
        sane(cls, "cls");
        String className = classKey(cls);
        Supplier<? extends T> supplier = getSupplier(cls);
        return createWithSupplierOrReflect(cls, className, supplier, null, null);
    }

    /**
     * Gets supplier function for the specified class.
     * Use inject() to set the supplier for a class.
     * Suppliers are keyed by their class binary name.
     * Binary name example: java.util.AbstractMap$SimpleEntry
     *
     * IMPORTANT:
     * This method will only return a non-null if a custom supplier
     * has been set for this class (e.g. by calling inject())
     * A 'safer' way to get a guaranteed non-null Supplier is to use lambda
     * instead of calling this method, as follows:
     * Supplier<? extends MyType> myTypeFactory = () -> Creator.create(MyType.class);
     * 
     * @param <T> Type to supply
     * @param cls The class to get supplier for
     * @return Custom supplier, or null if no custom supplier was specified
     */
    @SuppressWarnings("unchecked")
    public <T> Supplier<? extends T> getSupplier(Class<T> cls)
    {
        sane(cls, "cls");
        return (Supplier<? extends T>) suppliers.get(classKey(cls));
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
        sane(cls, "cls");
        return suppliers.containsKey(classKey(cls));
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
        sane(cls, "cls");
        sane(supplier, "supplier");
        String className = classKey(cls);
        synchronized (injectionLock)
        {
            verify(!created.contains(className),
                    "Cannot make injection for class " + cls.getName() +
                            " because create() or singleton() were already called for it");
            suppliers.put(className, supplier);
        }
    }

    protected String classKey(Class<?> cls)
    {
        return cls.getName();
    }

    private static boolean isParameterized(Class<?>[] paramTypes, Object[] initargs)
    {
        return paramTypes != null && initargs != null;
    }

    private <T> SupplierResult<T> trySupplier(Supplier<? extends T> supplier,
            Class<?>[] paramTypes,
            Object[] initargs)
    {
        if (supplier == null)
        {
            return SupplierResult.notUsed();
        }
        boolean parameterized = isParameterized(paramTypes, initargs);
        if (parameterized)
        {
            if (supplier instanceof BiFunction<?, ?, ?>)
            {
                @SuppressWarnings("unchecked")
                BiFunction<Class<?>[], Object[], T> paramSupplier =
                        (BiFunction<Class<?>[], Object[], T>) supplier;
                return SupplierResult.used(paramSupplier.apply(paramTypes, initargs));
            }
            return SupplierResult.notUsed();
        }
        return SupplierResult.used(supplier.get());
    }

    /**
     * Keeps supplier/reflective creation paths consistent and easy to follow.
     */
    private <T> T createWithSupplierOrReflect(Class<T> cls,
            String className,
            Supplier<? extends T> supplier,
            Class<?>[] paramTypes,
            Object[] initargs)
    {
        SupplierResult<T> supplied = trySupplier(supplier, paramTypes, initargs);
        if (supplied.used)
        {
            return markCreated(className, supplied.value);
        }

        boolean parameterized = isParameterized(paramTypes, initargs);
        T result = createReflective(className, cls, parameterized, paramTypes, initargs);
        return markCreated(className, result);
    }

    private <T> T createReflective(String className,
            Class<T> cls,
            boolean parameterized,
            Class<?>[] paramTypes,
            Object[] initargs)
    {
        try
        {
            if (parameterized)
            {
                return cls.getDeclaredConstructor(paramTypes).newInstance(initargs);
            }
            return cls.getDeclaredConstructor().newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException(
                    "Could not create an instance of class " + className, e);
        }
    }

    private <T> T markCreated(String className, T result)
    {
        synchronized (injectionLock)
        {
            created.add(className);
        }
        return result;
    }
}
