[![Maven Central](https://img.shields.io/maven-central/v/com.github.mike10004/sample-image-generator.svg)](https://repo1.maven.org/maven2/com/github/mike10004/sample-image-generator/)
[![Travis build status](https://img.shields.io/travis/mike10004/sample-image-generator.svg)](https://travis-ci.org/mike10004/sample-image-generator)
[![AppVeyor build status](https://img.shields.io/appveyor/ci/mike10004/sample-image-generator.svg)](https://ci.appveyor.com/project/mike10004/sample-image-generator)

# sample-image-generator

Java library to generate sample images of arbitrary size.

Need a 10MB jpeg to load test your image processing application? Try

    NoiseImageGenerator generator = NoiseImageGenerator.createGenerator(ImageFormat.JPEG);
    File imageFile = new File("largeimage.jpg");
    try (OutputStream out = new FileOutputStream(imageFile)) {
       generator.generate(10 * 1024 * 1024, out);
    }

Only PNG and JPEG output formats are currently supported.

## Maven

    <dependency>
        <groupId>com.github.mike10004</groupId>
        <artifactId>sample-image-generator</artifactId>
        <version>[CHECK MAVEN CENTRAL BADGE ABOVE]</version>
    </dependency>

## Credits

Seed image for the default scaling image generator is from 
[publicdomainpictures.net][pdn-image-url]. The author is Jean Beaufort and 
it is distributed under the CC0 license.

[pdn-image-url]: http://www.publicdomainpictures.net/view-image.php?image=210256&picture=bison 
