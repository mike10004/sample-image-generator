package com.github.mike10004.sampleimggen;

import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.awt.Dimension;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class DimensionedImageByteArrayGenerator extends LargeImageByteArrayGenerator {

    private static final int MAX_NUM_TRIES = 10;

    private final DimensionEstimator dimensionEstimator;

    public DimensionedImageByteArrayGenerator(DimensionEstimator dimensionEstimator) {
        super();
        this.dimensionEstimator = checkNotNull(dimensionEstimator);
    }

    public abstract byte[] generateImageBytesForSize(int minimumBytes, Dimension imageSize) throws IOException;

    @Override
    protected byte[] generateImageBytes(int minimumSize) throws IOException {
        byte[] bytes = null;
        Dimension imageSize = dimensionEstimator.estimate(minimumSize);
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

    protected interface DimensionEstimator  {
        void estimate(int numBytes, Dimension target);
        default Dimension estimate(int numBytes) {
            Dimension d = new Dimension();
            estimate(numBytes, d);
            return d;
        }

        static DimensionEstimator constant(int width, int height) {
            return (numBytes, target) -> {
                target.width = width;
                target.height = height;
            };
        }
    }

    protected static class LinearDimensionEstimator implements DimensionEstimator {

        public final double slope, intercept;
        public final Fraction aspectRatio;

        public LinearDimensionEstimator(double slope, double intercept, Fraction aspectRatio) {
            this.slope = slope;
            this.intercept = intercept;
            checkArgument(!Double.isNaN(slope) && Double.isFinite(slope), "slope must be well-defined: %s", slope);
            checkArgument(!Double.isNaN(intercept) && Double.isFinite(intercept), "intercept must be well-defined: %s", intercept);
            this.aspectRatio = aspectRatio;
        }

        public static LinearDimensionEstimator fromSamples(double[][] xyPairs, Fraction aspectRatio) {
            SimpleRegression regression = new SimpleRegression(true);
            regression.addData(xyPairs);
            return new LinearDimensionEstimator(regression.getSlope(), regression.getIntercept(), aspectRatio);
        }

        @Override
        public void estimate(int numBytes, Dimension target) {
            double width = Math.ceil(Math.max(1, slope * numBytes + intercept));
            target.width = Ints.saturatedCast(Math.round(width));
            target.height = Math.max(1, IntMath.checkedMultiply(target.width, aspectRatio.getDenominator()) / aspectRatio.getNumerator());
        }
    }

    /**
     * Gets the maximum number of attempts to produce an image of adequate size.
     * The dimension estimator estimates the width and height of an image to be produced,
     * but if the resulting byte size is not above the minimum requested, then
     * subsequent attempts will be made until the image is of adequate size, until this
     * limit on attempts is reached.
     * @return the maximum number of attempts
     */
    protected int getMaxNumTries() {
        return MAX_NUM_TRIES;
    }
}
