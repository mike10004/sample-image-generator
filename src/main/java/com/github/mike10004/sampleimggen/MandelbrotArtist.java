package com.github.mike10004.sampleimggen;

import com.google.common.io.Files;
import com.google.common.math.LongMath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Superclass of a mandelbrot set drawer.
 *
 */
public abstract class MandelbrotArtist {

    private final static int DEFAULT_MAX_ITERATIONS = 570;

    protected final int maxIterations;

    public MandelbrotArtist() {
        this(DEFAULT_MAX_ITERATIONS);
    }

    public MandelbrotArtist(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public BufferedImage draw() {
        return draw(800, 600);
    }

    protected int selectColor(int iterations) {
        return iterations | (iterations << 16);
    }

    public abstract BufferedImage draw(int width, int height);

    public static void main(String[] args) throws IOException {
        File outputDir = new File("/tmp/mandelbrots");
        System.out.format("output directory: %s%n", outputDir.getAbsolutePath());
        MandelbrotArtist artist = new RosettaMandelbrotArtist();
        int imageNum = 0;
        for (int width : new int[]{512, 1024, 2048, 4096, 8192}) {
            int height = width * 3 / 4;
            BufferedImage image = artist.draw(width, height);
            String stem = String.format("mandelbrot-%d", ++imageNum);
            File outputJpgFile = new File(outputDir, stem + ".jpg");
            File outputPngFile = new File(outputDir, stem + ".png");
            writeImage(image, "jpg", outputJpgFile);
            writeImage(image, "png", outputPngFile);
        }
    }

    private static void writeImage(RenderedImage image, String format, File outputFile) throws IOException {
        Files.createParentDirs(outputFile);
        ImageIO.write(image, format, outputFile);
        System.out.format("%5d x %5d image: %6.1f kB in %s%n", image.getWidth(), image.getHeight(), outputFile.length() / 1024f, outputFile);
    }

    public static MandelbrotArtist createDefault() {
        return new RosettaMandelbrotArtist();
    }

    public static MandelbrotArtist createDefault(int maxIterations) {
        return new RosettaMandelbrotArtist(maxIterations);
    }

    /*
     * License: GNU FDL (Free Documentation License)
     *
     * See https://rosettacode.org/wiki/Mandelbrot_set#Java.
     *
     * This code is used to generate large image files for load testing purposes.
     * VIDET as an application is not dependent on or derived from this code.
     */
    private static class RosettaMandelbrotArtist extends MandelbrotArtist {

        public RosettaMandelbrotArtist() {
        }

        public RosettaMandelbrotArtist(int maxIterations) {
            super(maxIterations);
        }

        @Override
        public BufferedImage draw(int width, int height) {
            checkArgument(width >= 0 && height >= 0, "width and height must both be nonnegative: %s x %s requested", width, height);
            checkArgument(LongMath.checkedMultiply(width, height) <= Integer.MAX_VALUE, "overflow at width %s height %s", width, height);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            double zx, zy, cX, cY, tmp;
            double zoom = chooseZoom(width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    zx = zy = 0;
                    cX = (x - width / 2) / zoom;
                    cY = (y - height / 2) / zoom;
                    int iter = maxIterations;
                    while (zx * zx + zy * zy < 4 && iter > 0) {
                        tmp = zx * zx - zy * zy + cX;
                        zy = 2.0 * zx * zy + cY;
                        zx = tmp;
                        iter--;
                    }
                    int color = selectColor(iter);
                    image.setRGB(x, y, color);
                }
            }
            return image;
        }

        protected double chooseZoom(int width, int height) {
            return height / 4;
        }
    }

}

