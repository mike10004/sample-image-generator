/*
 *
 */
package com.github.mike10004.sampleimggen;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LargeImageFileGenerator extends LargeFileGenerator {

    private final String imageIoWriteFormat;

    protected LargeImageFileGenerator(String imageIoWriteFormat) {
        this.imageIoWriteFormat = checkNotNull(imageIoWriteFormat);
    }

    @Override
    public byte[] createFile(int minimumSize) throws IOException {
        ImageCreation creation = createImage(minimumSize);
        byte[] bytes = creation.imageBytes;
        if (bytes == null) {
            bytes = toImageDataByteArray(creation.image, minimumSize);
        }
        return bytes;
    }

    protected byte[] toImageDataByteArray(RenderedImage image, int expectedSize) throws IOException {
        expectedSize = Math.max(1024, expectedSize);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(expectedSize);
        ImageIO.write(image, imageIoWriteFormat, baos);
        return baos.toByteArray();
    }

    protected static class ImageCreation {
        public final RenderedImage image;
        @Nullable
        public final byte[] imageBytes;

        public ImageCreation(RenderedImage image, @Nullable byte[] imageBytes) {
            this.image = checkNotNull(image);
            this.imageBytes = imageBytes;
        }
    }

    protected abstract ImageCreation createImage(int minimumSize) throws IOException;
}
