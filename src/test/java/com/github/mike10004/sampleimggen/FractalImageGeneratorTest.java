package com.github.mike10004.sampleimggen;

public class FractalImageGeneratorTest extends LargeImageStreamGeneratorTestBase {

    public FractalImageGeneratorTest() {
        super(FractalImageGenerator.class);
    }

    @Override
    protected LargeImageStreamGenerator createGenerator(ImageFormat format) {
        return FractalImageGenerator.createGenerator(format);
    }

}