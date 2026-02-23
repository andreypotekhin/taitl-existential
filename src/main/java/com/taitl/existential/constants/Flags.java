package com.taitl.existential.constants;

/**
 * Central registry of bit flags used by the existential configuration system.
 * Flags are designed to be OR-ed together to compose behavior switches.
 */
public class Flags
{
    public static final int FLAG_1 = 1;
    public static final int FLAG_2 = 2;
    public static final int FLAG_3 = 4;
    public static final int FLAG_4 = 8;

    /**
     * Enforces non-empty descriptions for behavior rules to improve diagnostics.
     */
    public static final int BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS = 16;

    /**
     * Uses fully-qualified class names for library-inferred type keys (event/read/write overloads without TypeKey).
     */
    public static final int BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES = 32;

    /**
     * @deprecated Use BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES.
     */
    @Deprecated
    public static final int FLAG_6 = BEHAVIOR_TYPE_KEYS_USE_FULL_CLASS_NAMES;
    public static final int FLAG_7 = 64;
    public static final int FLAG_8 = 128;
    public static final int FLAG_9 = 256;
    public static final int FLAG_10 = 512;
    public static final int FLAG_11 = 1024;
    public static final int FLAG_12 = 2048;
    public static final int FLAG_13 = 4096;

    /**
     * Highest supported flag value in this registry.
     */
    public static final int MAX_FLAG = FLAG_13;
}
