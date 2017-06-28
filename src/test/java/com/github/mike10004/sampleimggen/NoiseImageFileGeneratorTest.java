package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo.Format;

import static org.junit.Assert.*;

public class NoiseImageFileGeneratorTest extends LargeFileGeneratorTestBase {

    @Override
    protected LargeFileGenerator createGenerator(Format format) {
        return NoiseImageFileGenerator.createGenerator(format);
    }

    @Override
    protected boolean isVeryLargeSizesIncluded() {
        return true;
    }
}