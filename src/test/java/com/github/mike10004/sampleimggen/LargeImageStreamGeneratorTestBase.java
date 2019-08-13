package com.github.mike10004.sampleimggen;

import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import org.devlib.schmidt.imageinfo.ImageInfo;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.github.mike10004.sampleimggen.Guava.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class LargeImageStreamGeneratorTestBase {

    private final Class<?> target;

    public LargeImageStreamGeneratorTestBase(Class<?> target) {
        this.target = requireNonNull(target);
    }

    protected abstract LargeImageStreamGenerator createGenerator(ImageFormat format);

    @Test
    public void createsInstanceOfCorrectType() {
        for (ImageFormat fmt : Arrays.asList(ImageFormat.JPEG, ImageFormat.PNG)) {
            LargeImageStreamGenerator instance = createGenerator(fmt);
            assertEquals("not correct instance type: " + instance, target, instance.getClass());
        }
    }

    @Test
    public void png_someSizes() throws Exception {
        System.out.println("\npng_someSizes");
        LargeImageStreamGenerator generator = createGenerator(ImageFormat.PNG);
        testSomeSizes(generator);
    }

    @Test
    public void jpeg_someSizes() throws Exception {
        System.out.println("\njpeg_someSizes");
        LargeImageStreamGenerator generator = createGenerator(ImageFormat.JPEG);
        testSomeSizes(generator);
    }

    protected boolean isVeryLargeSizesIncluded() {
        return false;
    }

    protected Dimension readImageSize(byte[] bytes) throws IOException {
        checkArgument(bytes.length >= 4, "image length is probably invalid: %s", bytes.length);
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(new ByteArrayInputStream(bytes));
        checkState(imageInfo.check(), "image info check failed on byte array of length %s with first 4 bytes %s", BaseEncoding.base16().encode(bytes, 0, 4));
        return new Dimension(imageInfo.getWidth(), imageInfo.getHeight());
    }

    private void testSomeSizes(LargeImageStreamGenerator generator) throws Exception {
        List<Integer> minimumSizes = Lists.newArrayList(0, 1, 1024, 10 * 1024, -1024 * 1024);
        if (isVeryLargeSizesIncluded()) {
            minimumSizes.add(100 * 1024);
            minimumSizes.add(1024 * 1024);
        }
        for (int minimumSize : minimumSizes) {
            long startTime = System.currentTimeMillis();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.max(256, minimumSize));
            generator.generate(minimumSize, baos);
            baos.flush();
            byte[] fileBytes = baos.toByteArray();
            assertTrue("expect valid file length: " + fileBytes.length, fileBytes.length > 4);
            Dimension imageSize = readImageSize(fileBytes);
            long duration = System.currentTimeMillis() - startTime;
            System.out.format("%d x %d (%d >= %d) produced in %d ms%n", imageSize.width, imageSize.height, fileBytes.length, minimumSize, duration);
            assertTrue("image empty", imageSize.width * imageSize.height > 0);
            assertTrue("not enough bytes", fileBytes.length >= minimumSize);
        }
    }
}
