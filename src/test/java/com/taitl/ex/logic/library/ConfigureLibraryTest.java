package com.taitl.ex.logic.library;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class ConfigureLibraryTest extends SpecBase
{
    {
        autoConfigure = false;
    }

    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Test
    void rejectUnknownKey()
    {
        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE, "unknown=true")));

        String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
        assertThat(message, containsString("Invalid configuration key"));
        assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
    }

    @Test
    void rejectInvalidBoolean()
    {
        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                        "behavior.rules.requireDescriptions=YES")));

        String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
        assertThat(message, containsString("Invalid boolean value"));
        assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
    }

    @Test
    void rejectMissingEnvFile()
    {
        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> ConfigureLibrary.ENV_CONFIG_FILE.equals(name) ? "/tmp/no-such-file.properties" : null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                        "behavior.rules.requireDescriptions=false")));

        String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
        assertThat(message, containsString("does not exist"));
        assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
    }

    @Test
    void rejectOversizedClasspathConfig()
    {
        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE, oversizedConfig())));

        String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
        assertThat(message, containsString("exceeds max size"));
        assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
    }

    @Test
    void rejectOversizedEnvFile()
    {
        try
        {
            Path temp = Files.createTempFile("ex-config", ".properties");
            try
            {
                Files.write(temp, oversizedConfig().getBytes(StandardCharsets.UTF_8));

                ConfigureLibrary loader = new ConfigureLibrary(ex,
                        name -> ConfigureLibrary.ENV_CONFIG_FILE.equals(name) ? temp.toString() : null,
                        new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                                "behavior.rules.requireDescriptions=false")));

                String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
                assertThat(message, containsString("too large"));
                assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
            }
            finally
            {
                deleteTempFile(temp);
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void rejectSymlinkEnvFile()
    {
        try
        {
            Path tempDir = Files.createTempDirectory("ex-config");
            Path target = Files.createTempFile(tempDir, "ex-target", ".properties");
            Path link = tempDir.resolve("config-link.properties");
            try
            {
                Files.write(target, "behavior.rules.requireDescriptions=false".getBytes(StandardCharsets.UTF_8));
                try
                {
                    Files.createSymbolicLink(link, target);
                }
                catch (UnsupportedOperationException | IOException e)
                {
                    Assumptions.assumeTrue(false, "Symlinks not supported");
                }

                ConfigureLibrary loader = new ConfigureLibrary(ex,
                        name -> ConfigureLibrary.ENV_CONFIG_FILE.equals(name) ? link.toString() : null,
                        new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                                "behavior.rules.requireDescriptions=false")));

                String message = assertThrows(IllegalStateException.class, loader::configure).getMessage();
                assertThat(message, containsString("must not be a symlink"));
                assertThat(message, containsString(ConfigureLibrary.TROUBLESHOOTING_SECTION));
            }
            finally
            {
                deleteTempFile(link);
                deleteTempFile(target);
                deleteTempDirectory(tempDir);
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void startupDelegatesToConfigureLibrary()
    {
        StubConfigureLibrary loader = new StubConfigureLibrary(ex);
        ExistentialInit ei = new ExistentialInit(ex, loader);
        ei.startup();
        assertThat(loader.called, is(true));
    }

    private static class StubConfigureLibrary extends ConfigureLibrary
    {
        private boolean called;

        private StubConfigureLibrary(Existential ex)
        {
            super(ex, name -> null, new MemoryClassLoader(Map.of()));
        }

        public void configure()
        {
            called = true;
        }
    }

    private static class MemoryClassLoader extends ClassLoader
    {
        private final Map<String, String> resources;

        private MemoryClassLoader(Map<String, String> resources)
        {
            this.resources = resources;
        }

        public InputStream getResourceAsStream(String name)
        {
            String resource = resources.get(name);
            if (resource != null)
            {
                return new ByteArrayInputStream(resource.getBytes(StandardCharsets.UTF_8));
            }
            return super.getResourceAsStream(name);
        }
    }

    private static String oversizedConfig()
    {
        char[] data = new char[2_000_000];
        Arrays.fill(data, 'a');
        return new String(data);
    }

    private static void deleteTempFile(Path path)
    {
        if (path == null)
        {
            return;
        }
        try
        {
            Files.deleteIfExists(path);
        }
        catch (IOException ignored)
        {
        }
    }

    private static void deleteTempDirectory(Path path)
    {
        if (path == null)
        {
            return;
        }
        try
        {
            Files.deleteIfExists(path);
        }
        catch (IOException ignored)
        {
        }
    }
}
