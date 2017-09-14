/*
 *
 */
package com.github.mike10004.sampleimggen;

import java.io.IOException;
import java.io.OutputStream;

public abstract class LargeImageByteArrayGenerator implements LargeImageStreamGenerator {

    protected abstract byte[] generateImageBytes(int minimumSize) throws IOException;

    @Override
    public int generate(int minimumSizeInBytes, OutputStream output) throws IOException {
        byte[] bytes = generateImageBytes(minimumSizeInBytes);
        output.write(bytes);
        return bytes.length;
    }
}
