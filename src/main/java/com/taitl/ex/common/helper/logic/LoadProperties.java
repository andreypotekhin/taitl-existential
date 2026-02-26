package com.taitl.ex.common.helper.logic;

import java.io.*;
import java.nio.file.*;
import com.taitl.ex.common.helper.Properties;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.strings.Text.*;

/**
 * Loads properties from classpath resources and files.
 */
public class LoadProperties
{
    protected ClassLoader classLoader;
    protected long maxBytes;

    public LoadProperties(ClassLoader classLoader, long maxBytes)
    {
        sane(classLoader, "classLoader");
        check(maxBytes >= 0, "Argument 'maxBytes' must be >= 0");
        this.classLoader = classLoader;
        this.maxBytes = maxBytes;
    }

    public InputStream openResource(String resource) throws IOException
    {
        sane(resource, "resource");
        InputStream stream = classLoader.getResourceAsStream(resource);
        if (stream == null)
        {
            throw new FileNotFoundException(String.format("Classpath resource not found: %s", resource));
        }
        return stream;
    }

    public InputStream openFile(Path file) throws IOException
    {
        sane(file, "file");
        return Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS);
    }

    public java.util.Properties fromStream(InputStream stream) throws IOException
    {
        return Properties.load(stream, maxBytes);
    }

    public java.util.Properties fromResource(String resource) throws IOException
    {
        try (InputStream stream = openResource(resource))
        {
            return fromStream(stream);
        }
    }

    public java.util.Properties fromFile(Path file) throws IOException
    {
        try (InputStream stream = openFile(file))
        {
            return fromStream(stream);
        }
    }

    public java.util.Properties fromFile(String path) throws IOException
    {
        return fromFile(Paths.get(trimmed(path, "path")));
    }

    public java.util.Properties fromResourceAndOptionalFile(String resource, String path) throws IOException
    {
        java.util.Properties props = fromResource(resource);
        overwriteFromOptionalFile(props, path);
        return props;
    }

    public void overwriteFromOptionalFile(java.util.Properties props, String path) throws IOException
    {
        sane(props, "props");
        if (path == null || path.isBlank())
        {
            return;
        }
        props.putAll(fromFile(path));
    }
}
