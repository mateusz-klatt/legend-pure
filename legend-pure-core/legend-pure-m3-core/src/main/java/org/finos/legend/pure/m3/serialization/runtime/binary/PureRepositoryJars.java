// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.pure.m3.serialization.runtime.binary;

import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.block.function.Function;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.jar.JarInputStream;

public class PureRepositoryJars
{
    public static final Function<Path, PureRepositoryJar> PATH_TO_JAR = PureRepositoryJars::get;
    public static final Function<URL, PureRepositoryJar> URL_TO_JAR = PureRepositoryJars::get;
    public static final Function<byte[], PureRepositoryJar> BYTE_ARRAY_TO_JAR = PureRepositoryJars::get;
    public static final Function<ByteArrayOutputStream, PureRepositoryJar> BYTE_ARRAY_OUTPUT_STREAM_TO_JAR = PureRepositoryJars::get;
    public static final Function<ByteIterable, PureRepositoryJar> BYTE_ITERABLE_TO_JAR = PureRepositoryJars::get;

    private PureRepositoryJars()
    {
    }

    public static PureRepositoryJar get(Path path)
    {
        return get(path, false);
    }

    public static PureRepositoryJar get(Path path, boolean cacheBytes)
    {
        try
        {
            return cacheBytes ? new ByteArrayPureRepositoryJar(Files.readAllBytes(path)) : new PathPureRepositoryJar(path);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from path: " + path, e);
        }
    }

    public static PureRepositoryJar get(URL url)
    {
        return get(url, false);
    }

    public static PureRepositoryJar get(URL url, boolean cacheBytes)
    {
        try
        {
            return cacheBytes ?
                   new ByteArrayPureRepositoryJar(readBytes(url)) :
                   new URLPureRepositoryJar(url);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from URL: " + url, e);
        }
    }

    public static PureRepositoryJar get(byte[] bytes)
    {
        try
        {
            return new ByteArrayPureRepositoryJar(Arrays.copyOf(bytes, bytes.length));
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from byte array", e);
        }
    }

    public static PureRepositoryJar get(ByteArrayOutputStream byteStream)
    {
        try
        {
            return new ByteArrayPureRepositoryJar(byteStream.toByteArray());
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from ByteArrayOutputStream", e);
        }
    }

    public static PureRepositoryJar get(ByteIterable bytes)
    {
        try
        {
            return new ByteArrayPureRepositoryJar(bytes.toArray());
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from ByteIterable", e);
        }
    }

    public static PureRepositoryJar fromUnpackedJar(Path directory)
    {
        try
        {
            return new UnpackedPureRepositoryJar(directory);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("Error getting PureRepositoryJar from unpacked jar in " + directory, e);
        }
    }

    private static byte[] readBytes(URL url) throws IOException
    {
        if ("file".equalsIgnoreCase(url.getProtocol()))
        {
            // if it's a file URL, we have a potentially more efficient method of reading the content
            try
            {
                return Files.readAllBytes(Paths.get(url.toURI()));
            }
            catch (Exception ignore)
            {
                // fall back to default method
            }
        }

        int bufferSize = 8192;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(bufferSize);
        try (InputStream stream = url.openStream())
        {
            byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = stream.read(buffer, 0, bufferSize)) != -1)
            {
                byteStream.write(buffer, 0, read);
            }
        }
        return byteStream.toByteArray();
    }

    private static class PathPureRepositoryJar extends AbstractJarPureRepositoryJar
    {
        private final Path path;

        private PathPureRepositoryJar(Path path) throws IOException
        {
            super(PureRepositoryJarMetadata.getPureMetadata(path));
            this.path = path;
        }

        @Override
        protected JarInputStream getJarInputStream() throws IOException
        {
            return new JarInputStream(new BufferedInputStream(Files.newInputStream(this.path)));
        }
    }

    private static class URLPureRepositoryJar extends AbstractJarPureRepositoryJar
    {
        private final URL url;

        private URLPureRepositoryJar(URL url) throws IOException
        {
            super(PureRepositoryJarMetadata.getPureMetadata(url));
            this.url = url;
        }

        @Override
        protected JarInputStream getJarInputStream() throws IOException
        {
            return new JarInputStream(new BufferedInputStream(this.url.openStream()));
        }
    }

    private static class ByteArrayPureRepositoryJar extends AbstractJarPureRepositoryJar
    {
        private final byte[] bytes;

        private ByteArrayPureRepositoryJar(byte[] bytes) throws IOException
        {
            super(PureRepositoryJarMetadata.getPureMetadata(bytes));
            this.bytes = bytes;
        }

        @Override
        protected JarInputStream getJarInputStream() throws IOException
        {
            return new JarInputStream(new ByteArrayInputStream(this.bytes));
        }
    }
}
