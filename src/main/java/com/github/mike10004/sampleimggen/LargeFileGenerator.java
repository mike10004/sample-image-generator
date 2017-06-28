package com.github.mike10004.sampleimggen;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class LargeFileGenerator {

    public abstract byte[] createFile(int minimumSize) throws IOException;

}
