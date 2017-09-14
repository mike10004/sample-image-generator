package com.github.mike10004.sampleimggen;

import com.google.common.io.ByteSink;

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
     * @see #generate(int, OutputStream)
     */
    default int generate(int minimumSizeInBytes, ByteSink sink) throws IOException {
        try (OutputStream output = sink.openStream()) {
            return generate(minimumSizeInBytes, output);
        }
    }
}
