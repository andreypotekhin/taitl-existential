package com.taitl.existential.specs.library_configuration;

import com.taitl.ex.logic.library.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.specs.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class UserCanConfigureLibrary extends SpecBase
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
    @DisplayName("User can change library configuration options programmatically")
    void changeLibraryOptions()
    {
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
        ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(true));
        ex.toggle(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));

        assertThat(ex.get(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND), is(false));
        ex.on(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
        assertThat(ex.get(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND), is(true));
        ex.off(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
        assertThat(ex.get(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND), is(false));
    }

    @Test
    @DisplayName("User must configure the library before use")
    void sendEventsToUnconfiguredLibrary()
    {
        assertThat(assertThrows(IllegalStateException.class, () -> {
            String tran = ex.begin(op).id();
            ex.update(cat, tran);
        }).getMessage(), containsString("You need to configure at least one context"));
    }

    @Test
    @DisplayName("User can configure library options using a config file")
    void configureWithFile() throws Exception
    {
        Path file = Files.createTempFile("existential-", ".properties");
        Files.writeString(file, "behavior.rules.requireDescriptions=true", StandardCharsets.UTF_8);

        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> ConfigureLibrary.ENV_CONFIG_FILE.equals(name) ? file.toString() : null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                        "behavior.rules.requireDescriptions=false")));
        loader.configure();

        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(true));
        Files.deleteIfExists(file);
    }

    @Test
    @DisplayName("User can configure library options using a classpath resource")
    void configureWithClasspathResource()
    {
        ex.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);
        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                        "behavior.rules.requireDescriptions=false")));

        loader.configure();

        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(false));
    }

    @Test
    @DisplayName("User can specify the config file with an environment variable")
    void specifyConfigFileUsingEnvironmentVariable() throws Exception
    {
        Path file = Files.createTempFile("existential-", ".properties");
        Files.writeString(file, "behavior.rules.requireDescriptions=true", StandardCharsets.UTF_8);

        ConfigureLibrary loader = new ConfigureLibrary(ex,
                name -> ConfigureLibrary.ENV_CONFIG_FILE.equals(name) ? file.toString() : null,
                new MemoryClassLoader(Map.of(ConfigureLibrary.CLASSPATH_CONFIG_FILE,
                        "behavior.rules.requireDescriptions=false")));

        loader.configure();

        assertThat(ex.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS), is(true));
        Files.deleteIfExists(file);
    }

    @Test
    @DisplayName("Initial version of config is available as classpath resource")
    void defaultConfigAvailableOnClasspath() throws Exception
    {
        try (InputStream stream =
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(
                                ConfigureLibrary.CLASSPATH_CONFIG_FILE))
        {
            assertNotNull(stream);
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
