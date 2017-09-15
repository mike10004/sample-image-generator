package com.github.mike10004.sampleimggen;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.devlib.schmidt.imageinfo.ImageInfo;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * Program used to generate the raw data mapping image pixel sizes to file sizes.
 * That raw data is analyzed to produce a trendline that implementations of
 * {@link DimensionedImageByteArrayGenerator} use to estimate, for a given
 * minimum file size, how big the dimensions of the image to be generated
 * must be.
 */
public class GenerateSamples {

    private static final AtomicLong sampleCounter = new AtomicLong();

    private Fraction aspectRatio = new Fraction(4, 3);
    int minWidth = 16;
    int numSamplesPerGenerator = 10;
    private OutputFormat outputFormat = OutputFormat.JAVA_SYNTAX;

    private enum OutputFormat {
        HUMAN, JAVA_SYNTAX;

        public String render(int numBytes, int width, int height, long durationMs, File file) {
            switch (this) {
                case HUMAN:
                    return String.format("%d\t%d\t%d\t%d\t%s", width, numBytes, height, durationMs, file);
                case JAVA_SYNTAX:
                    return String.format("{%d, %d},", numBytes, width);
                default:
                    throw new IllegalStateException(this.name());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<RenderingImageGenerator> generators = Arrays.asList(
                FractalImageGenerator.createGenerator(ImageFormat.JPEG),
                FractalImageGenerator.createGenerator(ImageFormat.PNG),
                NoiseImageGenerator.createGenerator(ImageFormat.PNG),
                NoiseImageGenerator.createGenerator(ImageFormat.JPEG)
        );
        File outputDir = java.nio.file.Files.createTempDirectory("generated-samples").toFile();
        try {
            GenerateSamples s = new GenerateSamples();
            for (RenderingImageGenerator generator : generators) {
                s.generateSamples(generator, outputDir);
            }
        } finally {
            FileUtils.deleteDirectory(outputDir);
        }
    }

    public void generateSamples(RenderingImageGenerator generator, File outputDir) throws IOException {
        List<Integer> widths = IntStream.iterate(minWidth, x -> x * 2).limit(numSamplesPerGenerator).boxed().collect(ImmutableList.toImmutableList());
        SimpleRegression regression = new SimpleRegression(true);
        int aspectW = aspectRatio.getNumerator();
        int aspectH = aspectRatio.getDenominator();
        String tag = generator.getClass().getSimpleName();
        for (Integer width : widths) {
            long startTime = System.currentTimeMillis();
            Dimension dim = new Dimension(width, width * aspectH / aspectW);
            byte[] fileBytes = generator.generateImageBytesForSize(0, dim);
            assertImageIsReadable(ByteSource.wrap(fileBytes)); // check that it's a readable image
            long duration = System.currentTimeMillis() - startTime;
            String filenameExtension = generator.getOutputFormat().name().toLowerCase();
            String filename = String.format("sample-%d-%s.%s", sampleCounter.incrementAndGet(), tag, filenameExtension);
            File file = new File(outputDir, filename);
            Files.createParentDirs(file);
            Files.write(fileBytes, file);
            System.out.println(outputFormat.render(fileBytes.length, width, dim.height, duration, file));
            //noinspection SuspiciousNameCombination
            regression.addData(fileBytes.length, width);
        }
        double slope = regression.getSlope();
        double intercept = regression.getIntercept();
        System.out.format("%s trendline (%d:%d aspect): width = %f * x + %f%n", tag, aspectW, aspectH, slope, intercept);
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
