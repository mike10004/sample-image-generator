package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo;
import com.github.mike10004.common.image.ImageInfos;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class LargeFileGeneratorTestBase {

    protected abstract LargeFileGenerator createGenerator(ImageInfo.Format format);

    @Test
    public void png_someSizes() throws Exception {
        System.out.println("\npng_someSizes");
        LargeFileGenerator generator = createGenerator(ImageInfo.Format.PNG);
        testSomeSizes(generator);
    }

    @Test
    public void jpeg_someSizes() throws Exception {
        System.out.println("\njpeg_someSizes");
        LargeFileGenerator generator = createGenerator(ImageInfo.Format.JPEG);
        testSomeSizes(generator);
    }

    protected boolean isVeryLargeSizesIncluded() {
        return false;
    }

    private void testSomeSizes(LargeFileGenerator generator) throws Exception {
        List<Integer> minimumSizes = Lists.newArrayList(0, 1, 1024, 10 * 1024, -1024 * 1024);
        if (isVeryLargeSizesIncluded()) {
            minimumSizes.add(100 * 1024);
            minimumSizes.add(1024 * 1024);
        }
        for (int minimumSize : minimumSizes) {
            long startTime = System.currentTimeMillis();
            byte[] fileBytes = generator.createFile(minimumSize);
            Dimension imageSize = ImageInfos.readImageSize(fileBytes);
            long duration = System.currentTimeMillis() - startTime;
            System.out.format("%d x %d (%d >= %d) produced in %d ms%n", imageSize.width, imageSize.height, fileBytes.length, minimumSize, duration);
            assertTrue("image empty", imageSize.width * imageSize.height > 0);
            assertTrue("not enough bytes", fileBytes.length >= minimumSize);
        }
    }
}
