package com.github.mike10004.sampleimggen;

import org.apache.commons.math3.fraction.Fraction;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScalingImageGenerator extends RenderingImageGenerator {

    private static final BufferedImage ONE_BY_ONE_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private final BufferedImage seedImage;

    public ScalingImageGenerator(BufferedImage seedImage, DimensionEstimator dimensionEstimator, ImageFormat outputFormat) {
        super(dimensionEstimator, outputFormat);
        this.seedImage = seedImage;
    }

    private static double divide(int numerator, int denominator) {
        return (double) numerator / (double) denominator;
    }

    @Override
    protected RenderedImage render(int minimumSize, Dimension imageSize) throws IOException {
        if (imageSize.width == 1 && imageSize.height == 1) {
            return ONE_BY_ONE_IMAGE;
        }
        double sx = divide(imageSize.width, seedImage.getWidth());
        double sy = divide(imageSize.height, seedImage.getHeight());
        AffineTransform xform = AffineTransform.getScaleInstance(sx, sy);
        BufferedImageOp op = new AffineTransformOp(xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(seedImage, null);
    }

    /*
     * Linear regression is probably not the way to go for these, as the amount of detail or
     * entropy is constant when we're just scaling an image. I imagine a log estimate would
     * be more appropriate for both the JPEG and PNG esimators.
     */
    private static final class DefaultHolder {
        private static final BufferedImage DEFAULT_SEED_IMAGE;
        static {
            BufferedImage tmp;
            try {
                tmp = ImageIO.read(DefaultHolder.class.getResource("/default-seed-image.jpg"));
            } catch (IOException e) {
                Logger.getLogger(ScalingImageGenerator.class.getName()).log(Level.WARNING, "failed to load seed image", e);
                tmp = null;
            }
            DEFAULT_SEED_IMAGE = tmp;
        }

        private static final DimensionEstimator JPEG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {741, 16},
                {972, 32},
                {1569, 64},
                {3336, 128},
                {9048, 256},
                {26312, 512},
                {82242, 1024},
                {256055, 2048},
                {827096, 4096},
                {2584584, 8192},
        }, Fraction.getReducedFraction(1920, 1280));

        private static final DimensionEstimator PNG_ESTIMATOR = LinearDimensionEstimator.fromSamples(new double[][]{
                {455, 16},
                {1419, 32},
                {4814, 64},
                {17607, 128},
                {65016, 256},
                {243390, 512},
                {895697, 1024},
                {2659543, 2048},
                {3292950, 4096},
                {4276093, 8192},
        }, Fraction.getReducedFraction(1920, 1280));

        @SuppressWarnings("Duplicates")
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

    public static ScalingImageGenerator createGenerator(ImageFormat imageFormat) {
        if (DefaultHolder.DEFAULT_SEED_IMAGE == null) {
            throw new IllegalStateException("seed image failed to load");
        }
        return new ScalingImageGenerator(DefaultHolder.DEFAULT_SEED_IMAGE, DefaultHolder.getEstimator(imageFormat), imageFormat);
    }
}
