package com.github.mike10004.sampleimggen;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;

import java.awt.Dimension;
import java.io.IOException;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public abstract class DimensionedImageByteArrayGenerator extends LargeImageByteArrayGenerator {

    private static final int MAX_NUM_TRIES = 10;

    private final Function<Integer, Dimension> fileSizeToImageSize;

    public DimensionedImageByteArrayGenerator(Function<Integer, Dimension> fileSizeToImageSize) {
        super();
        this.fileSizeToImageSize = fileSizeToImageSize;
    }

    public abstract byte[] generateImageBytesForSize(int minimumBytes, Dimension imageSize) throws IOException;

    @Override
    protected byte[] generateImageBytes(int minimumSize) throws IOException {
        byte[] bytes = null;
        Dimension imageSize = fileSizeToImageSize.apply(minimumSize);
        int width = Math.max(1, imageSize.width), height = Math.max(1, imageSize.height);
        int numTries = 0;
        while (bytes == null || bytes.length < minimumSize) {
            if (numTries >= getMaxNumTries()) {
                throw new IllegalStateException("tried too many times");
            }
            numTries++;
            checkState(LongMath.checkedMultiply(width, height) <= Integer.MAX_VALUE, "overflow on next width * height computation");
            bytes = generateImageBytesForSize(minimumSize, new Dimension(width, height));
            width = IntMath.checkedMultiply(width, 2);
            height = IntMath.checkedMultiply(height, 2);
        }
        return bytes;
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

    protected int getMaxNumTries() {
        return MAX_NUM_TRIES;
    }
}
