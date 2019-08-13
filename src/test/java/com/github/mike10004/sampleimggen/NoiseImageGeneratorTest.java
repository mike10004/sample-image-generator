package com.github.mike10004.sampleimggen;

public class NoiseImageGeneratorTest extends LargeImageStreamGeneratorTestBase {

    public NoiseImageGeneratorTest() {
        super(NoiseImageGenerator.class);
    }

    @Override
    protected LargeImageStreamGenerator createGenerator(ImageFormat format) {
        return NoiseImageGenerator.createGenerator(format);
    }

    @Override
    protected boolean isVeryLargeSizesIncluded() {
        return true;
    }
}