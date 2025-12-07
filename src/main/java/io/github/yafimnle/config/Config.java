package io.github.yafimnle.config;

public class Config {
    private static Config instance = Config.instance();

    private FFMpegConfig ffMpegConfig = null;
    private MagickConfig magickConfig = null;
    /**
     * Defines weather the final output is Ultra-HD (4k, 2160p, 16:9, 3840x2160) or Full-HD (1080p, 16:9, 1920x1080).
     * <pre>
     *     Default: RESULT_1080P (FullHD 1920x1080)
     * </pre>
     */
    private Resolution resolution = null;

    /**
     * Do not set this here to some other value, use it in your concret scenario. Changing this value here
     * will result in a lot of trash in this folder since this value is assumed by tests!
     */
    private String destinationDir = "/tmp/yafimnle";
    private String sourceDir = null;

    private Config() {
        // Singleton instance. Use instance() or freshInstance() (mainly used in unit tests)
    }

    public static Config instance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public static Config freshInstance() {
        instance = new Config();
        return instance;
    }


    public Config destinationDir(String destinationDir) {
        instance.destinationDir = destinationDir;
        return instance;
    }

    public String destinationDir() {
        return instance.destinationDir;
    }

    public Config resolution(Resolution resolution) {
        instance.resolution = resolution;
        return instance;
    }
    public Resolution resolution() {
        if (instance.resolution == null) {
            instance.resolution = Resolution.FULL_HD;
        }
        return instance.resolution;
    }



    public FFMpegConfig ffmpeg() {
        if (instance.ffMpegConfig == null) {
            instance.ffMpegConfig = FFMpegConfig.ffmpeg();
        }
        return instance.ffMpegConfig;
    }
    public Config ffmpeg(FFMpegConfig config) {
        instance.ffMpegConfig = config;
        return instance;
    }




    public MagickConfig magick() {
        if (magickConfig == null) {
            instance.magickConfig = MagickConfig.magick();
        }
        return instance.magickConfig;
    }
    public Config magick(MagickConfig config) {
        instance.magickConfig = config;
        return instance;
    }

    public Config sourceDir(String sourceDir) {
        instance.sourceDir = sourceDir;
        return instance;
    }
    public String sourceDir() {
        return instance.sourceDir;
    }
}
