package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

public class NoiseImageFileGenerator extends ImageFileDimensionFromSizeGenerator {

    private final Random random = new Random(NoiseImageFileGenerator.class.hashCode());

    private NoiseImageFileGenerator(Function<Integer, Dimension> fileSizeToImageSize, String imageIoWriteFormat) {
        super(fileSizeToImageSize, imageIoWriteFormat);
    }

    @Override
    public RenderedImage createImage(Dimension imageSize) throws IOException {
        BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = random.nextInt();
        }
        return image;
    }

    public static void main(String[] args) throws Exception {
        for (String format : new String[]{"png", "jpg"}) {
            NoiseImageFileGenerator generator = new NoiseImageFileGenerator(x -> new Dimension(), format);
            ImageFileDimensionFromSizeGenerator.generateSamples(generator, format, new File("webapp", "target"));
        }

    }

    public static LargeImageFileGenerator createGenerator(ImageInfo.Format format) {
        switch (format) {
            case PNG:
                return new NoiseImageFileGenerator(makeFunctionFromTrendline(new Trendline(18193.1755198333, -13183559.9417411)), "png");
            case JPEG:
                return new NoiseImageFileGenerator(makeFunctionFromTrendline(new Trendline(3639.2104356967, -2636879.32484107)), "jpg");
            default:
                throw new IllegalArgumentException("only png and jpeg supported, not " + format);
        }
    }

}
