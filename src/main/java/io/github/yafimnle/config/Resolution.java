package io.github.yafimnle.config;

import lombok.Getter;

public enum Resolution {
    LOW_QUALITY("540p", "960x540", "16:9",960, 540),   // fast encoding for review
    FULL_HD("1080p", "1920x1080", "16:9", 1920, 1080), // 1920x1080, Full-HD, default
    ULTRA_HD("2160p", "3840x2160", "16:9", 3840, 2160), // 3840x2160, 4K, Ultra HD

    UNKNOWN("", "", "", -1, -1);

    /**
     * An appreviation like 540p, 1080p or 2160p.
     */
    @Getter
    private String apprev;
    /**
     * Format: "wxh" - For "width" x "height". This style is sometimes used in FFMpeg.
     */
    @Getter
    private String dimension;
    /**
     * "3:2", "4:3" or "16:9" or unknown.
     */
    @Getter
    private String ar;
    @Getter
    private int width;
    @Getter
    private int height;

    Resolution(String apprev, String dimension, String ar, int width, int height) {
        this.apprev = apprev;
        this.width = width;
        this.dimension = dimension;
        this.height = height;
        this.ar = ar;
    }

    public static Resolution from(int width, int height) {
        if (width == 960 && height == 540) {
            return LOW_QUALITY;
        } else if (width == 1920 && height == 1080) {
            return FULL_HD;
        } else if (width == 3840 && height == 2160) {
            return ULTRA_HD;
        } else {
            Resolution unknown = UNKNOWN;
            unknown.width = width;
            unknown.height = height;
            unknown.dimension = width+"x"+height;
            unknown.apprev = "unknown"; // obviously not 2160p, 1080p or 540p. Others will not be supported!
            unknown.ar = switch (unknown.dimension) {
                case "5184x3888", "4320x3240", "2592x1944", "2160x1620", "1920x1440" -> "4:3";
                case "6000x4000", "7008x4672" -> "3:2";
                case
                        "1920x1080",
                        "5184x2920",
                        "5184x2916",
                        "2880x1620", // extension from 2160x1620 (width = 1620*(16/9))
                        "6912x3888"  // extension from 5184x3888
                        -> "16:9"; // TODO recheck... 5184x2916?
                default -> "unknown";
            };

            return unknown;
        }
    }


}
