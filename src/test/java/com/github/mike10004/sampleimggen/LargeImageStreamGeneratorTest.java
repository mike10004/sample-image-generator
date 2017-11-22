package com.github.mike10004.sampleimggen;

import com.google.common.io.ByteSink;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.*;

public class LargeImageStreamGeneratorTest {

    @Test
    public void generate_toSink() throws Exception {
        AtomicReference<ByteArrayOutputStream> streamRef = new AtomicReference<>();
        AtomicInteger openings = new AtomicInteger(0);
        int size = 256;
        ByteSink sink = new ByteSink() {
            @Override
            public OutputStream openStream() throws IOException {
                ByteArrayOutputStream stream = new ByteArrayOutputStream(size);
                streamRef.set(stream);
                openings.incrementAndGet();
                return stream;
            }
        };
        byte[] expected = new byte[size];
        new Random(getClass().getName().hashCode()).nextBytes(expected);
        LargeImageStreamGenerator generator = new LargeImageStreamGenerator() {
            @Override
            public int generate(int minimumSizeInBytes, OutputStream output) throws IOException {
                checkArgument(size >= minimumSizeInBytes);
                output.write(expected);
                return size;
            }
        };
        int len = generator.generate(1, sink::openStream);
        assertEquals(size, len);
        assertEquals(1, openings.get());
        byte[] actual = streamRef.get().toByteArray();
        assertArrayEquals(expected, actual);
    }

}