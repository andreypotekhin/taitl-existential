package com.taitl.ex.logic.library;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

public class ConfigureLibrary
{
    public static final String ENV_CONFIG_FILE = "EXISTENTIAL_CONFIG_FILE";
    public static final String CLASSPATH_CONFIG_FILE = "existential.properties";
    public static final String TROUBLESHOOTING_SECTION =
            "/Troubleshooting.md#library-configuration-load-failure";

    private static final String OPT_REQUIRE_DESCRIPTIONS = "behavior.rules.requireDescriptions";
    private static final Set<String> BOOL_TRUES = Set.of("true", "TRUE", "True");
    private static final Set<String> BOOL_FALSES = Set.of("false", "FALSE", "False");

    protected Existential ex;
    protected Function<String, String> env;
    protected ClassLoader classLoader;

    public ConfigureLibrary(Existential ex)
    {
        this(ex, System::getenv, ConfigureLibrary.class.getClassLoader());
    }

    public ConfigureLibrary(Existential ex, Function<String, String> env, ClassLoader classLoader)
    {
        sane(ex, "ex", env, "env", classLoader, "classLoader");
        this.ex = ex;
        this.env = env;
        this.classLoader = classLoader;
    }

    public void configure()
    {
        fromClasspath(CLASSPATH_CONFIG_FILE);

        String envFile = env.apply(ENV_CONFIG_FILE);
        if (envFile != null && !envFile.isBlank())
        {
            fromFile(envFile.trim());
        }
    }

    public void fromFile(String path)
    {
        sane(path, "path");
        Path file = Paths.get(path.trim());
        verify(Files.exists(file),
                String.format("Configuration file does not exist: %s. See %s", file, TROUBLESHOOTING_SECTION));
        verify(Files.isRegularFile(file),
                String.format("Configuration path is not a file: %s. See %s", file, TROUBLESHOOTING_SECTION));
        verify(Files.isReadable(file),
                String.format("Configuration file is not readable: %s. See %s", file, TROUBLESHOOTING_SECTION));
        try (InputStream stream = Files.newInputStream(file))
        {
            load(stream, file.toString());
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read configuration file '%s'. See %s",
                            file, TROUBLESHOOTING_SECTION),
                    e);
        }
    }

    public void fromClasspath(String resource)
    {
        sane(resource, "resource");
        InputStream stream = classLoader.getResourceAsStream(resource);
        verify(stream != null,
                String.format("Classpath configuration resource '%s' not found. See %s",
                        resource, TROUBLESHOOTING_SECTION));

        try (InputStream opened = stream)
        {
            load(opened, "classpath:" + resource);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read classpath configuration '%s'. See %s",
                            resource, TROUBLESHOOTING_SECTION),
                    e);
        }
    }

    protected void load(InputStream stream, String source)
    {
        Properties props = new Properties();
        try
        {
            props.load(stream);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not parse configuration from '%s'. See %s",
                            source, TROUBLESHOOTING_SECTION),
                    e);
        }

        for (String key : props.stringPropertyNames())
        {
            String value = props.getProperty(key);
            if (OPT_REQUIRE_DESCRIPTIONS.equals(key))
            {
                boolean b = parseBoolean(key, value, source);
                if (b)
                {
                    ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
                }
                else
                {
                    ex.off(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
                }
            }
            else
            {
                throw new IllegalStateException(
                        String.format("Invalid configuration key '%s' in '%s'. See %s",
                                key, source, TROUBLESHOOTING_SECTION));
            }
        }
    }

    protected boolean parseBoolean(String key, String value, String source)
    {
        verify(value != null,
                String.format("Configuration key '%s' has no value in '%s'. See %s",
                        key, source, TROUBLESHOOTING_SECTION));
        String trimmed = value.trim();
        if (BOOL_TRUES.contains(trimmed))
        {
            return true;
        }
        if (BOOL_FALSES.contains(trimmed))
        {
            return false;
        }
        throw new IllegalStateException(
                String.format(
                        "Invalid boolean value '%s' for key '%s' in '%s'. Use true/false. See %s",
                        value, key, source, TROUBLESHOOTING_SECTION));
    }
}
