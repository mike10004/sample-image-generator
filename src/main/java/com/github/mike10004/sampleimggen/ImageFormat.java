package com.github.mike10004.sampleimggen;

public enum ImageFormat {

    JPEG(0),

    GIF(1),

    PNG(2),

    BMP(3),

    PCX(4),

    IFF(5),

    RAS(6),

    PBM(7),

    PGM(8),

    PPM(9),

    PSD(10);

    public final int imageInfoCode;
    
    ImageFormat(int imageInfoCode) {
        this.imageInfoCode = imageInfoCode;
    }

    public String getImageIOWriteFormatCode() {
        switch (this) {
            case JPEG:
                return "jpg";
            default:
                return name().toLowerCase();
        }
    }
}
