package com.taitl.exlogic.creator;

import java.util.function.*;
import com.taitl.existential.helper.*;

/**
 * Creates instances of the specified class, or returns a singleton.
 * Allows for dependency injection, by customizing instance creation.
 *
 * Use cases:
 * 1. Dependency inject using singleton() and create() methods.
 * 2. Customize class instantiation by calling inject() method.
 * 3. For even greater effect, subclass CreatorDevice. 
 *   Comes handy when mocking components for unit tests.
 * 4. Integrate a third party dependency injection library
 * behind Creator facade, again by providing custom CreatorDevice.
 * 
 * Usage (per use case):
 * 1. Creator.singleton(AppDevice.class); Creator.create(LogDevice.class);
 * 2. Creator.inject(com.taitl.helper.devices.LogDevice.class, () -> new com.my.LogDevice());
 * 3. Creator.setDevice(myCreatorDevice)
 * 4. Creator.setDevice(myThirdPartyAdapter)
 *
 * Why:
 * Q: Is this class a kind of 'God' antipattern?
 * A: No - it does not deal with concrete classes, therefore does not
 * refer/encompass  concrete business logic.
 * Q: Is injection by reflection not performant?
 * A: We reserve this to less performance-critical code. For more
 * performance-critical code, you are welcome to use other approaches
 * to injection, or directly instantiate the classes.
 */
public class Creator
{
    /**
     * Instance holder for lazy initialization.
     */
    private static class InstanceHolder
    {
        private static CreatorDevice device = new CreatorDevice();
    }

    /**
     * Protected constructor for utility class.
     */
    protected Creator()
    {
    }

    /**
     * Returns creator device
     */
    private static CreatorDevice device()
    {
        return InstanceHolder.device;
    }

    /**
     * Sets custom creator device
     */
    public static void setDevice(CreatorDevice device)
    {
        Args.cool(device, "device");
        InstanceHolder.device = device;
    }

    /**
     * Provides a singleton instance of the specified class.
     *
     * @param <T> Type to create
     * @param cls Class object to create
     * @return A new or existing  instance of the specified class
     */
    public static <T> T singleton(Class<T> cls)
    {
        return device().singleton(cls);
    }

    /**
     * Creates new instance of the specified class.
     *
     * @param <T> Type to provide
     * @param cls The class to create and instance of
     * @return Newly created instance of the specified class
     */
    public static <T> T create(Class<T> cls)
    {
        return device().create(cls);
    }

    /**
     * Gets supplier function for the specified class.
     * Use setSupplier() to set supplier function per-class.
     *
     * @param <T> Type to supply
     * @param cls The class to get supplier for
     * @return Custom supplier, or null if no custom supplier was specified
     */
    public static <T> Supplier<? extends T> getSupplier(Class<T> cls)
    {
        return device().getSupplier(cls);
    }

    /**
     * Sets supplier function for the specified class.
     *
     * @param <T>      Type to supply
     * @param supplier Custom supplier function
     * @param cls      The class to get supplier for
     */
    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        device().inject(cls, supplier);
    }

    /**
     * Removes supplier function for the specified class.
     * Throws IllegalStateException if supplier function was set for this class.
     *
     * @param <T>      Type to supply
     * @param cls      The class to get supplier for
     */
    public static <T> void uninject(Class<T> cls)
    {
        device().uninject(cls);
    }
}
