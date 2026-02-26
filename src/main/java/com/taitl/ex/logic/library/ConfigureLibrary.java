package com.taitl.ex.logic.library;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.function.*;
import com.taitl.ex.common.helper.FileSecurity;
import com.taitl.ex.common.helper.LimitedInputStream;
import com.taitl.ex.common.helper.Properties;
import com.taitl.ex.common.logic.LoadProperties;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;
import static com.taitl.ex.common.helper.Text.*;

public class ConfigureLibrary
{
    public static final String ENV_CONFIG_FILE = "EXISTENTIAL_CONFIG_FILE";
    public static final String CLASSPATH_CONFIG_FILE = "existential.properties";
    public static final String TROUBLESHOOTING_SECTION =
            "/Troubleshooting.md#library-configuration-load-failure";

    private static final String OPT_REQUIRE_DESCRIPTIONS = "behavior.rules.requireDescriptions";
    private static final long MAX_CONFIG_BYTES = 1024 * 1024;

    protected Existential ex;
    protected Function<String, String> env;
    protected ClassLoader classLoader;
    protected LoadProperties loadProperties;

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
        this.loadProperties = new LoadProperties(classLoader, MAX_CONFIG_BYTES);
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
        String trimmed = trimmed(path, "path");
        Path file = Paths.get(trimmed);
        verify(Files.exists(file),
                String.format("Configuration file does not exist: %s. See %s", file, TROUBLESHOOTING_SECTION));
        verify(!Files.isSymbolicLink(file),
                String.format("Configuration file must not be a symlink: %s. See %s", file, TROUBLESHOOTING_SECTION));
        verify(Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS),
                String.format("Configuration path is not a file: %s. See %s", file, TROUBLESHOOTING_SECTION));
        verify(Files.isReadable(file),
                String.format("Configuration file is not readable: %s. See %s", file, TROUBLESHOOTING_SECTION));
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null)
        {
            FileSecurity.verifySecurePosixDirectory(parent, "Configuration directory", TROUBLESHOOTING_SECTION);
        }
        FileSecurity.verifySecurePosixFile(file, "Configuration file", TROUBLESHOOTING_SECTION);
        long size = fileSize(file);
        verify(size <= MAX_CONFIG_BYTES,
                String.format("Configuration file is too large (%d bytes). Max allowed is %d bytes. See %s",
                        size, MAX_CONFIG_BYTES, TROUBLESHOOTING_SECTION));
        try (InputStream stream = loadProperties.openFile(file))
        {
            apply(read(stream, file.toString()), file.toString());
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
        try (InputStream opened = loadProperties.openResource(resource))
        {
            apply(read(opened, "classpath:" + resource), "classpath:" + resource);
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalStateException(
                    String.format("Classpath configuration resource '%s' not found. See %s",
                            resource, TROUBLESHOOTING_SECTION),
                    e);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read classpath configuration '%s'. See %s",
                            resource, TROUBLESHOOTING_SECTION),
                    e);
        }
    }

    protected java.util.Properties read(InputStream stream, String source)
    {
        try
        {
            return loadProperties.fromStream(stream);
        }
        catch (LimitedInputStream.MaxSizeExceededException e)
        {
            throw new IllegalStateException(
                    String.format("Configuration data in '%s' exceeds max size of %d bytes. See %s",
                            source, MAX_CONFIG_BYTES, TROUBLESHOOTING_SECTION),
                    e);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not parse configuration from '%s'. See %s",
                            source, TROUBLESHOOTING_SECTION),
                    e);
        }
    }

    protected void apply(java.util.Properties props, String source)
    {
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
        try
        {
            return Properties.parseBoolean(value);
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalStateException(
                    String.format(
                            "Invalid boolean value '%s' for key '%s' in '%s'. Use true/false. See %s",
                            value, key, source, TROUBLESHOOTING_SECTION),
                    e);
        }
    }

    protected long fileSize(Path file)
    {
        try
        {
            BasicFileAttributes attrs =
                    Files.readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            return attrs.size();
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    String.format("Could not read configuration file size '%s'. See %s",
                            file, TROUBLESHOOTING_SECTION),
                    e);
        }
    }
}
