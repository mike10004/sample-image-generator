package com.github.mike10004.sampleimggen;

public class FractalImageGeneratorTest extends LargeImageStreamGeneratorTestBase {

    @Override
    protected LargeImageStreamGenerator createGenerator(ImageFormat format) {
        return FractalImageGenerator.createGenerator(format);
    }

}