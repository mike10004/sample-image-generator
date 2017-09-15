package com.github.mike10004.sampleimggen;

import org.apache.commons.math3.fraction.Fraction;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * Generator whose output is a fractal image.
 * https://gist.github.com/j-mcc1993/ef75a9227eeac139ee94
 */
public class FractalImageGenerator extends RenderingImageGenerator {

    protected FractalImageGenerator(DimensionEstimator fileSizeToImageSize, ImageFormat outputFormat) {
        super(fileSizeToImageSize, outputFormat);
    }

    @Override
    protected RenderedImage render(int minimumSize, Dimension imageSize) throws IOException {
        int width = imageSize.width, height= imageSize.height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, (x-y)*(x+y)&(x|y));
            }
        }
        return image;
    }

    public static NoiseImageGenerator createGenerator(ImageFormat format) {
        return new NoiseImageGenerator(EstimatorHolder.getEstimator(format), format); // TODO cache commonly-used generators
    }

    private static class EstimatorHolder {
        private final static DimensionEstimator JPEG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {632, 16},
                {647, 32},
                {732, 64},
                {1536, 128},
                {8071, 256},
                {31022, 512},
                {129742, 1024},
                {521425, 2048},
                {2148530, 4096},
                {9487302, 8192},
        }, new Fraction(4, 3));
        private final static DimensionEstimator PNG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {174, 16},
                {414, 32},
                {1264, 64},
                {4584, 128},
                {29169, 256},
                {142101, 512},
                {617291, 1024},
                {2586440, 2048},
                {10863254, 4096},
                {45942822, 8192},
        }, new Fraction(4, 3));

        public static DimensionEstimator getEstimator(ImageFormat outputFormat) {
            switch (outputFormat) {
                case PNG:
                    return PNG_ESTIMATOR;
                case JPEG:
                    return JPEG_ESTIMATOR;
                default:
                    throw new IllegalArgumentException("only jpeg and png are supported, not format " + outputFormat);
            }
        }
    }
}
