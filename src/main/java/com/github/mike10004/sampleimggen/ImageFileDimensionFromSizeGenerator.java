package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfos;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public abstract class ImageFileDimensionFromSizeGenerator extends LargeImageFileGenerator {

    private static final int MAX_NUM_TRIES = 10;

    private final Function<Integer, Dimension> fileSizeToImageSize;

    public ImageFileDimensionFromSizeGenerator(Function<Integer, Dimension> fileSizeToImageSize, String imageIoWriteFormat) {
        super(imageIoWriteFormat);
        this.fileSizeToImageSize = fileSizeToImageSize;
    }

    public abstract RenderedImage createImage(Dimension imageSize) throws IOException;

    @Override
    protected ImageCreation createImage(int minimumSize) throws IOException {
        byte[] bytes = null;
        Dimension imageSize = fileSizeToImageSize.apply(minimumSize);
        int width = Math.max(1, imageSize.width), height = Math.max(1, imageSize.height);
        int numTries = 0;
        RenderedImage image = null;
        while (bytes == null || bytes.length < minimumSize) {
            if (numTries >= MAX_NUM_TRIES) {
                throw new IllegalStateException("tried too many times");
            }
            numTries++;
            checkState(LongMath.checkedMultiply(width, height) <= Integer.MAX_VALUE, "overflow on next width * height computation");
            image = createImage(new Dimension(width, height));
            bytes = toImageDataByteArray(image, minimumSize);
            width = IntMath.checkedMultiply(width, 2);
            height = IntMath.checkedMultiply(height, 2);
        }
        return new ImageCreation(image, bytes);
    }

    protected static class Trendline {
        /**
         * Slope.
         */
        public final double m;
        /**
         * X-intercept
         */
        public final double b;

        public Trendline(double m, double b) {
            this.m = m;
            this.b = b;
        }
    }

    protected static Function<Integer, Dimension> makeFunctionFromTrendline(Trendline t) {
        // x is image width, image height is 3/4 of width
        // mx + b = y
        //     mx = y - b
        //      x = (y - b) / m
        checkArgument(t.m != 0, "slope must be nonzero");
        return y -> {
            int width = Ints.checkedCast(Math.round((y - t.b) / t.m));
            int height = Ints.checkedCast(Math.round(Math.ceil(width * 3d / 4d)));
            long imageBufferSize = LongMath.checkedMultiply(LongMath.checkedMultiply(width, height), 3); // 3-channel rgb
            if (imageBufferSize > Integer.MAX_VALUE) {
                throw new ArithmeticException("overflow; can't create image large enough for file size " + y);
            }
            return new Dimension(width, height);
        };
    }

    private static final AtomicLong sampleCounter = new AtomicLong();

    public static void generateSamples(ImageFileDimensionFromSizeGenerator generator, String tag, File outputDir) throws Exception {
        List<Integer> widths = Lists.newArrayList(16, 256, 512, 1024, 2048, 4096, 8192);
        System.out.println();
        System.out.println(tag);
        for (int width : widths) {
            long startTime = System.currentTimeMillis();
            Dimension dim = new Dimension(width, width * 3 / 4);
            RenderedImage image = generator.createImage(dim);
            byte[] fileBytes = generator.toImageDataByteArray(image, width * 256);
            ImageInfos.readImageSize(fileBytes); // check that it's a readable image
            long duration = System.currentTimeMillis() - startTime;
            String filename = "generated-sample-" + sampleCounter.incrementAndGet() + "." + tag;
            File file = new File(outputDir, filename);
            Files.createParentDirs(file);
            Files.write(fileBytes, file);
            System.out.format("%d\t%d\t%d\t%d\t%s%n", width, fileBytes.length, dim.height, duration, file.getAbsolutePath());
        }

    }
}
