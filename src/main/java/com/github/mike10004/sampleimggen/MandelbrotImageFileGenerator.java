package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.function.Function;

public class MandelbrotImageFileGenerator extends ImageFileDimensionFromSizeGenerator {
    /*
     * JPEG trend actually appears quadratic (as opposed to linear PNG trend); we should recompute
     */
    private static final Trendline JPEG_SIZES_TREND = new Trendline(135.9697896, -148762.5);
    private static final Trendline PNG_SIZES_TREND = new Trendline(196.9658308, -190008.3333);

    private final MandelbrotArtist artist = MandelbrotArtist.createDefault();

    private MandelbrotImageFileGenerator(Function<Integer, Dimension> fileSizeToImageSize, String imageIoWriteFormat) {
        super(fileSizeToImageSize, imageIoWriteFormat);
    }

    public static LargeFileGenerator getGenerator(ImageInfo.Format format) {
        switch (format) {
            case JPEG:
                return new MandelbrotImageFileGenerator(makeFunctionFromTrendline(JPEG_SIZES_TREND), "jpg");
            case PNG:
                return new MandelbrotImageFileGenerator(makeFunctionFromTrendline(PNG_SIZES_TREND), "png");
            default:
                throw new IllegalArgumentException("only jpeg and png supported, not " + format);
        }
    }

    @Override
    public RenderedImage createImage(Dimension imageSize) throws IOException {
        return artist.draw(imageSize.width, imageSize.height);
    }
}