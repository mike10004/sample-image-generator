# sample-image-generator

Java library to generate sample images of arbitrary size.

Need a 10MB jpeg to load test your image processing application? Try

   NoiseImageGenerator generator = NoiseImageGenerator.createGenerator(ImageFormat.JPEG);
   File imageFile = new File("largeimage.jpg");
   try (OutputStream out = new FileOutputStream(imageFile)) {
       generator.generate(10 * 1024 * 1024, out);
   }

Only PNG and JPEG output formats are currently supported.
