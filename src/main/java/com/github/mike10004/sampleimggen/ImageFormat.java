package com.github.mike10004.sampleimggen;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

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
        //noinspection SwitchStatementWithTooFewBranches
        switch (this) {
            case JPEG:
                return "jpg";
            default:
                return name().toLowerCase();
        }
    }

    public int getCompatibleBufferedImageType() {
        //noinspection SwitchStatementWithTooFewBranches
        switch (this) {
            case PNG:
                return BufferedImage.TYPE_INT_ARGB;
            default:
                return BufferedImage.TYPE_INT_RGB;
        }
    }

    public static String getBufferedImageTypeName(RenderedImage image) {
        if (image instanceof BufferedImage) {
            return getBufferedImageTypeName(((BufferedImage)image).getType());
        }
        return "NOT_BUFFERED";
    }

    public static String getBufferedImageTypeName(int type) {
        switch (type) {
            case 0:
                return "TYPE_CUSTOM";
            case 1:
                return "TYPE_INT_RGB";
            case 2:
                return "TYPE_INT_ARGB";
            case 3:
                return "TYPE_INT_ARGB_PRE";
            case 4:
                return "TYPE_INT_BGR";
            case 5:
                return "TYPE_3BYTE_BGR";
            case 6:
                return "TYPE_4BYTE_ABGR";
            case 7:
                return "TYPE_4BYTE_ABGR_PRE";
            case 8:
                return "TYPE_USHORT_565_RGB";
            case 9:
                return "TYPE_USHORT_555_RGB";
            case 10:
                return "TYPE_BYTE_GRAY";
            case 11:
                return "TYPE_USHORT_GRAY";
            case 12:
                return "TYPE_BYTE_BINARY";
            case 13:
                return "TYPE_BYTE_INDEXED";
            default:
                return "UNKNOWN";
        }
    }
}
