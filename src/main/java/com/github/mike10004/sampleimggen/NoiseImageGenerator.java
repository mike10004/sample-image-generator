package com.github.mike10004.sampleimggen;

import org.apache.commons.math3.fraction.Fraction;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Generator whose output contains random noise.
 */
public class NoiseImageGenerator extends RenderingImageGenerator {

    private final Random random;

    protected NoiseImageGenerator(DimensionEstimator fileSizeToImageSize, ImageFormat outputFormat) {
        this(fileSizeToImageSize, outputFormat, new Random());
    }

    protected NoiseImageGenerator(DimensionEstimator fileSizeToImageSize, ImageFormat outputFormat, Random random) {
        super(fileSizeToImageSize, outputFormat);
        this.random = Objects.requireNonNull(random);
    }

    @Override
    protected RenderedImage render(int minimumSize, Dimension imageSize) throws IOException {
        BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, getOutputFormat().getCompatibleBufferedImageType());
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = random.nextInt();
        }
        return image;
    }

    /*
     * Linear regression on the required dimensions for noise images, with respect
     * to byte size, is (in theory) correct for PNG, because the randomness should
     * make PNG compression extremely inefficient. For JPEG, another type of model
     * may be more appropriate.
     */
    private static class EstimatorHolder {

        public static DimensionEstimator getEstimator(ImageFormat outputFormat) {
            switch (outputFormat) {
                case JPEG:
                    return JPEG_ESTIMATOR;
                case PNG:
                    return PNG_ESTIMATOR;
                default:
                    throw new IllegalArgumentException("output format not supported: " + outputFormat + "; must be jpeg or png");
            }
        }

        private static final DimensionEstimator PNG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {656, 16},
                {2396, 32},
                {9332, 64},
                {37050, 128},
                {147809, 256},
                {590672, 512},
                {2361711, 1024},
                {9445119, 2048},
                {37777215, 4096},
                {151102524, 8192},
        }, new Fraction(4, 3));

        public static final DimensionEstimator JPEG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {773, 16},
                {1112, 32},
                {2464, 64},
                {8039, 128},
                {30189, 256},
                {118664, 512},
                {472904, 1024},
                {1889710, 2048},
                {7555922, 4096},
                {30228174, 8192},
        }, new Fraction(4, 3));
    }

    public static NoiseImageGenerator createGenerator(ImageFormat format) {
        return new NoiseImageGenerator(EstimatorHolder.getEstimator(format), format); // TODO cache commonly-used generators
    }

}
