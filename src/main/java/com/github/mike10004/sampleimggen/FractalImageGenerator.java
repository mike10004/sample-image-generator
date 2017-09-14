package com.github.mike10004.sampleimggen;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.function.Function;

/**
 * Generator whose output is a fractal image.
 * https://gist.github.com/j-mcc1993/ef75a9227eeac139ee94
 */
public class FractalImageGenerator extends RenderingImageGenerator {

    protected FractalImageGenerator(ImageFormat outputFormat) {
        this(makeFunctionFromTrendline(makeTrendline(outputFormat)), outputFormat);
    }

    protected FractalImageGenerator(Function<Integer, Dimension> fileSizeToImageSize, ImageFormat outputFormat) {
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

    public static FractalImageGenerator createGenerator(ImageFormat format) {
        return new FractalImageGenerator(format);
    }

    protected static Trendline makeTrendline(ImageFormat format) {
        switch (format) {
            case PNG:
                return new Trendline(5516.093, -4124366.521);
            case JPEG:
                return new Trendline(1135.6819, -858246.477);
            default:
                throw new IllegalArgumentException("only png and jpeg supported, not " + format);
        }
    }

}
