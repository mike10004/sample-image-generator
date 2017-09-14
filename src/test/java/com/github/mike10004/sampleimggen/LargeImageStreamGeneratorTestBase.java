package com.github.mike10004.sampleimggen;

import com.google.common.collect.Lists;
import org.devlib.schmidt.imageinfo.ImageInfo;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertTrue;

public abstract class LargeImageStreamGeneratorTestBase {

    protected abstract LargeImageStreamGenerator createGenerator(ImageFormat format);

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
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setInput(new ByteArrayInputStream(bytes));
        checkState(imageInfo.check());
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
            Dimension imageSize = readImageSize(fileBytes);
            long duration = System.currentTimeMillis() - startTime;
            System.out.format("%d x %d (%d >= %d) produced in %d ms%n", imageSize.width, imageSize.height, fileBytes.length, minimumSize, duration);
            assertTrue("image empty", imageSize.width * imageSize.height > 0);
            assertTrue("not enough bytes", fileBytes.length >= minimumSize);
        }
    }
}
