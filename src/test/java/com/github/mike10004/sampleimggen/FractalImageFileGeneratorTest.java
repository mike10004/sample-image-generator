package com.github.mike10004.sampleimggen;

import com.github.mike10004.common.image.ImageInfo.Format;

public class FractalImageFileGeneratorTest extends LargeFileGeneratorTestBase {

    @Override
    protected LargeFileGenerator createGenerator(Format format) {
        return FractalImageFileGenerator.createGenerator(format);
    }
}