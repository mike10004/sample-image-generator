package com.github.mike10004.sampleimggen;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generator whose output contains random noise.
 */
public class NoiseImageGenerator extends RenderingImageGenerator {

    private final Random random;

    protected NoiseImageGenerator(ImageFormat outputFormat) {
        this(makeFunctionFromTrendline(makeTrendline(outputFormat)), outputFormat);
    }

    protected NoiseImageGenerator(Function<Integer, Dimension> fileSizeToImageSize, ImageFormat outputFormat) {
        this(fileSizeToImageSize, outputFormat, new Random());
    }

    protected NoiseImageGenerator(Function<Integer, Dimension> fileSizeToImageSize, ImageFormat outputFormat, Random random) {
        super(fileSizeToImageSize, outputFormat);
        this.random = checkNotNull(random);
    }

    @Override
    protected RenderedImage render(int minimumSize, Dimension imageSize) throws IOException {
        BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = random.nextInt();
        }
        return image;
    }

    public static NoiseImageGenerator createGenerator(ImageFormat format) {
        return new NoiseImageGenerator(format);
    }

    protected static Trendline makeTrendline(ImageFormat format) {
        switch (format) {
            case PNG:
                return new Trendline(18193.1755198333, -13183559.9417411);
            case JPEG:
                return new Trendline(3639.2104356967, -2636879.32484107);
            default:
                throw new IllegalArgumentException("only png and jpeg supported, not " + format);
        }
    }

}
