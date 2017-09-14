package com.github.mike10004.sampleimggen;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.devlib.schmidt.imageinfo.ImageInfo;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Program used to generate the raw data mapping image pixel sizes to file sizes.
 * That raw data is analyzed to produce a trendline that implementations of
 * {@link DimensionedImageByteArrayGenerator} use to estimate, for a given
 * minimum file size, how big the dimensions of the image to be generated
 * must be.
 */
public class GenerateSamples {

    private static final AtomicLong sampleCounter = new AtomicLong();

    public static void main(String[] args) throws Exception {
        List<RenderingImageGenerator> generators = Arrays.asList(
                new FractalImageGenerator(ImageFormat.JPEG),
                new FractalImageGenerator(ImageFormat.PNG),
                new NoiseImageGenerator(ImageFormat.PNG),
                new NoiseImageGenerator(ImageFormat.JPEG)
        );
        File outputDir = new File(System.getProperty("user.dir"));
        for (RenderingImageGenerator generator : generators) {
            generateSamples(generator, outputDir);
        }
    }

    public static void generateSamples(RenderingImageGenerator generator, File outputDir) throws IOException {
        List<Integer> widths = Lists.newArrayList(16, 256, 512, 1024, 2048, 4096, 8192);
        for (int width : widths) {
            long startTime = System.currentTimeMillis();
            Dimension dim = new Dimension(width, width * 3 / 4);
            byte[] fileBytes = generator.generateImageBytesForSize(width * 100, dim);
            assertImageIsReadable(ByteSource.wrap(fileBytes)); // check that it's a readable image
            long duration = System.currentTimeMillis() - startTime;
            String filenameExtension = generator.getOutputFormat().name().toLowerCase();
            String filename = String.format("sample-%d-%s.%s", sampleCounter.incrementAndGet(), generator.getClass().getSimpleName(), filenameExtension);
            File file = new File(outputDir, filename);
            Files.createParentDirs(file);
            Files.write(fileBytes, file);
            System.out.format("%d\t%d\t%d\t%d\t%s%n", width, fileBytes.length, dim.height, duration, file.getAbsolutePath());
        }

    }

    public static void assertImageIsReadable(ByteSource byteSource) throws IOException {
        try (InputStream in = byteSource.openStream()) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setInput(in);
            if (!imageInfo.check()) {
                throw new IOException("not readable: " + byteSource);
            }
        }
    }

}
