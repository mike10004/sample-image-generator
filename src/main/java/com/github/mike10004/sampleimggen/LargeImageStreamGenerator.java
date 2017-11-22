package com.github.mike10004.sampleimggen;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface LargeImageStreamGenerator {

    /**
     * Generates a stream of image bytes and writes it to an output stream.
     * @param minimumSizeInBytes the minimum number of bytes to write
     * @param output the output stream
     * @return the actual number of bytes written
     * @throws IOException if something goes awry
     */
    int generate(int minimumSizeInBytes, OutputStream output) throws IOException;

    /**
     * Generates a stream of image bytes and writes the bytes to a byte sink.
     * This is intended to be compatible with a Guava ByteSink by way of a method
     * reference lambda, e.g. {@code generate(1, byteSink::openStream)}.
     * @see #generate(int, OutputStream)
     */
    default int generate(int minimumSizeInBytes, StreamSupplier<? extends OutputStream> sink) throws IOException {
        try (OutputStream output = sink.openStream()) {
            return generate(minimumSizeInBytes, output);
        }
    }

    interface StreamSupplier<S extends Closeable> {
        S openStream() throws IOException;
    }
}
