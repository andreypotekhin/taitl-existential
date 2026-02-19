package com.taitl.ex.logic.library;

import java.io.*;
import java.nio.charset.*;
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
    void startupDelegatesToConfigureLibrary()
    {
        SpyInit init = new SpyInit(ex);
        init.startup();
        assertThat(init.loader.called, is(true));
    }

    private static class SpyInit extends ExistentialInit
    {
        private StubConfigureLibrary loader;

        private SpyInit(Existential ex)
        {
            super(ex);
            loader = new StubConfigureLibrary(ex);
        }

        protected ConfigureLibrary configureLibrary()
        {
            return loader;
        }
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
}
