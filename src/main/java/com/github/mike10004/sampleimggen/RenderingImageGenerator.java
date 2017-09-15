package com.github.mike10004.sampleimggen;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class RenderingImageGenerator extends DimensionedImageByteArrayGenerator {

    private final ImageFormat outputFormat;

    public RenderingImageGenerator(DimensionEstimator dimensionEstimator, ImageFormat outputFormat) {
        super(dimensionEstimator);
        this.outputFormat = outputFormat;
    }

    @Override
    public byte[] generateImageBytesForSize(int minimumBytes, Dimension imageSize) throws IOException {
        return toImageDataByteArray(render(minimumBytes, imageSize), minimumBytes, outputFormat);
    }

    protected abstract RenderedImage render(int minimumSize, Dimension imageSize) throws IOException;

    protected static byte[] toImageDataByteArray(RenderedImage image, int expectedSize, ImageFormat outputFormat) throws IOException {
        expectedSize = Math.max(1024, expectedSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
        ImageIO.write(image, outputFormat.getImageIOWriteFormatCode(), baos);
        return baos.toByteArray();
    }

    public ImageFormat getOutputFormat() {
        return outputFormat;
    }
}
