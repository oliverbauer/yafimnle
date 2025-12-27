package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ImageTransformer {

    public String transformImage(File i, File o, String filterComplex, int seconds) {
        var framerate = Config.instance().ffmpeg().framerate();
        var configThreads = Config.instance().ffmpeg().threads();
        var loggingConfig = Config.instance().ffmpeg().loggingConfig();
        var codec = Config.instance().ffmpeg().codec();
        var formatOutput = " \\\n\t "; // results in a command better readable

        var profile = "";
        if (Config.instance().ffmpeg().profile() != null) {
            profile = "-profile:v " + Config.instance().ffmpeg().profile();
        }
        var preset = "";
        if (Config.instance().ffmpeg().preset() != null) {
            preset = "-preset " + Config.instance().ffmpeg().preset();
        }

        StringBuilder sb = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(loggingConfig).append(" ").append(configThreads)
                .append(formatOutput).append("-loop 1 -framerate ").append(framerate).append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(i))
                .append(" -f lavfi -i anullsrc -c:a aac ");
        if (filterComplex != null) {
            sb.append(formatOutput).append("-filter_complex \"")
                    .append(" \\\n\t\t ").append(filterComplex)
                    .append("\" ");
        }
        sb.append(formatOutput).append("-b:a 192k -ac 2 -ar 44100 -vcodec ").append(codec).append(" -map [v] -map [a] -t ").append(seconds)
                .append(formatOutput).append("-y ")
                .append(formatOutput).append(configThreads)
                .append(formatOutput).append("-r ").append(framerate)
                .append(" -pix_fmt yuv420p ")
                .append(profile)
                .append(" ")
                .append(preset)
                .append(" ")
                .append(Config.instance().ffmpeg().encoderOptions())
                .append(" ")
                .append(FileUtils.escapeWhitespaces(o));

        return sb.toString();
    }
}
