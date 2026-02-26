package com.taitl.ex.common.logic;

import org.junit.jupiter.api.Test;

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
    @Test
    void loadsFromResource()
            throws Exception
    {
        LoadProperties load = new LoadProperties(
                new MemoryClassLoader(Map.of("a.properties", "flag=true\nname=base")),
                1024);

        java.util.Properties props = load.fromResource("a.properties");

        assertEquals("true", props.getProperty("flag"));
        assertEquals("base", props.getProperty("name"));
    }

    @Test
    void resourceAndOptionalFileAppliesOverrides()
            throws Exception
    {
        Path file = Files.createTempFile("load-properties-", ".properties");
        try
        {
            Files.writeString(file, "flag=true\nname=file", StandardCharsets.UTF_8);

            LoadProperties load = new LoadProperties(
                    new MemoryClassLoader(Map.of("a.properties", "flag=false\nbase=ok")),
                    1024);

            java.util.Properties props = load.fromResourceAndOptionalFile("a.properties", file.toString());

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
    void optionalFileBlankIsIgnored()
            throws Exception
    {
        LoadProperties load = new LoadProperties(
                new MemoryClassLoader(Map.of("a.properties", "flag=false")),
                1024);

        java.util.Properties props = load.fromResourceAndOptionalFile("a.properties", "   ");

        assertEquals("false", props.getProperty("flag"));
    }

    @Test
    void missingResourceFails()
    {
        LoadProperties load = new LoadProperties(new MemoryClassLoader(Map.of()), 1024);

        assertThrows(FileNotFoundException.class, () -> load.fromResource("missing.properties"));
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
