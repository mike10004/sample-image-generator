package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * https://gist.github.com/j-mcc1993/ef75a9227eeac139ee94
 */
public class FractalImageFileGenerator extends ImageFileDimensionFromSizeGenerator {

    private FractalImageFileGenerator(Function<Integer, Dimension> fileSizeToImageSize, String imageIoWriteFormat) {
        super(fileSizeToImageSize, imageIoWriteFormat);
    }

    public BufferedImage createImage(Dimension imageSize) throws IOException {
        int width = imageSize.width, height= imageSize.height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, (x-y)*(x+y)&(x|y));
            }
        }
        return image;
    }

    public static LargeImageFileGenerator createGenerator(ImageInfo.Format format) {
        switch (format) {
            case PNG:
                return new FractalImageFileGenerator(makeFunctionFromTrendline(new Trendline(5516.093, -4124366.521)), "png");
            case JPEG:
                return new FractalImageFileGenerator(makeFunctionFromTrendline(new Trendline(1135.6819, -858246.477)), "jpg");
            default:
                throw new IllegalArgumentException("only png and jpeg supported, not " + format);
        }
    }

    public static void main(String[] args) throws Exception {
        for (String format : new String[]{"png", "jpg"}) {
            FractalImageFileGenerator generator = new FractalImageFileGenerator(x -> new Dimension(), format);
            ImageFileDimensionFromSizeGenerator.generateSamples(generator, format, new File("webapp", "target"));
        }

    }
}
