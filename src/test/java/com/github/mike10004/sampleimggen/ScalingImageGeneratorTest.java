package com.github.mike10004.sampleimggen;

public class ScalingImageGeneratorTest extends LargeImageStreamGeneratorTestBase {

    @Override
    protected LargeImageStreamGenerator createGenerator(ImageFormat format) {
        return ScalingImageGenerator.createGenerator(format);
    }

    @Override
    protected boolean isVeryLargeSizesIncluded() {
        return true;
    }
}