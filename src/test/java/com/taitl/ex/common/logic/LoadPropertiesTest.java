package com.taitl.ex.common.logic;

import com.taitl.ex.common.helper.logic.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoadPropertiesTest
{
    LoadProperties load(Map<String, String> resources)
    {
        return new LoadProperties(new MemoryClassLoader(resources), 1024);
    }

    @Nested
    class FromResource
    {
        @Test
        @DisplayName("Loads from resource")
        void loads() throws Exception
        {
            java.util.Properties props =
                    load(Map.of("a.properties", "flag=true\nname=base")).fromResource("a.properties");

            assertEquals("true", props.getProperty("flag"));
            assertEquals("base", props.getProperty("name"));
        }

        @Test
        @DisplayName("Missing resource fails")
        void missing()
        {
            assertThrows(FileNotFoundException.class, () -> load(Map.of()).fromResource("missing.properties"));
        }
    }

    @Nested
    class FromResourceAndOptionalFile
    {
        @Test
        @DisplayName("Resource and optional file applies overrides")
        void appliesOverrides() throws Exception
        {
            Path file = Files.createTempFile("load-properties-", ".properties");
            try
            {
                Files.writeString(file, "flag=true\nname=file", StandardCharsets.UTF_8);

                java.util.Properties props =
                        load(Map.of("a.properties", "flag=false\nbase=ok")).fromResourceAndOptionalFile(
                                "a.properties", file.toString());

                assertEquals("true", props.getProperty("flag"));
                assertEquals("ok", props.getProperty("base"));
                assertEquals("file", props.getProperty("name"));
            }
            finally
            {
                Files.deleteIfExists(file);
            }
        }

        @Test
        @DisplayName("Optional file blank is ignored")
        void optionalFileBlank()
                throws Exception
        {
            java.util.Properties props =
                    load(Map.of("a.properties", "flag=false")).fromResourceAndOptionalFile("a.properties", "   ");

            assertEquals("false", props.getProperty("flag"));
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
