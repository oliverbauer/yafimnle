package io.github.yafimnle.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.transformation.common.OutlineEntry;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class OutlineV2 implements Transformation {

    List<OutlineEntry> outlineEntries;
    private boolean isVideo = false;

    public OutlineV2() {
        outlineEntries = new ArrayList<>();
    }

    public static OutlineV2 of(boolean isVideo, OutlineEntry... outlineEntry) {
        OutlineV2 outlineV2 = new OutlineV2();
        outlineV2.isVideo = isVideo;
        outlineV2.outlineEntries.addAll(Arrays.asList(outlineEntry));
        return outlineV2;
    }

    @Override
    public String toString() {
        if (outlineEntries.size() == 1) {
            StringBuilder s = new StringBuilder();
            s.append("  [0:v]").append(outlineEntries.get(0));
            s.append("\n");
            s.append("[v]\"\\");
            s.append("\n");
            return s.toString();
        } else {
            StringBuilder s = new StringBuilder();

            s.append("  [0:v]\\");
            s.append("\n");
            s.append("  ");
            s.append(outlineEntries.get(0));
            s.append("\\");
            s.append("\n");
            s.append("  [v1];\\");
            s.append("\n");
            s.append("  [v1]split=2[v1a][v1b];");
            s.append("\n");
            for (int i=1; i<=outlineEntries.size()-1; i++) {
                s.append("  [v"+ i+"a]\\");
                s.append("\n");

                s.append("  ");
                s.append(outlineEntries.get(i)).append("\\");

                if (i!=outlineEntries.size()-1) {
                    s.append("\n");
                    s.append("  [v" + (i) + "aDone];\\");
                    s.append("\n");


                    s.append("  [v"+ + (i)+"b][v"+ i+"aDone]overlay=0:0[v"+(i+1)+"];");
                    s.append("\\");
                    s.append("\n");
                    s.append("  [v"+(i+1)+"]split=2[v"+(i+1)+"a][v"+(i+1)+"b];");
                    s.append("\n");

                } else {

                    if (i == outlineEntries.size() -1) {
                        s.append("\n");
                        s.append("  [v" + (i) + "aDone];\\");
                        s.append("\n");
                        s.append("  [v"+i+"b][v"+i+"aDone]overlay=0:0");
                    } else {
                        s.append("NOT--LAST");
                    }
                }
            }

            s.append("\n");
            if (!isVideo) {
                s.append("[v]\"\\");
            } else {
                s.append("[v];");
                s.append("[0:a]loudnorm,afade=type=in:duration=1:start_time=0[a]\"\\"); // TODO normize?
            }

            s.append("\n");
            return s.toString();
        }
    }

    @Override
    public File fromImageToVideo(File input, File output, int seconds, String destinationDir) {
        int framerate = Config.instance().ffmpeg().framerate();
        String configThreads = Config.instance().ffmpeg().threads();
        String loggingConfig = Config.instance().ffmpeg().loggingConfig();
        String codec = Config.instance().ffmpeg().codec();

        String filterComplex = "\""+toString();

        if (!output.getName().endsWith("mp4")) {
            String current = output.toString();
            current = current.substring(0, current.length() - 4) + ".mp4";
            output = new File(current);
        }

        String formatOutput = " \\\n\t "; // results in a command better readable
        //formatOutput = " "; // results in a command with one line
        String command = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(loggingConfig).append(" ").append(configThreads)                                       // ffmpeg
                .append(formatOutput).append("-loop 1 -framerate ").append(framerate).append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input image

                // See: https://superuser.com/questions/1044988/merging-several-videos-with-audio-channel-and-without-audio
                .append(" -f lavfi -i anullsrc ")

                //.append(formatOutput).append(" ").append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input video
                .append(formatOutput).append("-filter_complex ").append(filterComplex)                                                             // see above: zoom-in
                //.append(formatOutput).append("-acodec aac -vcodec codec -map [v] -t ").append(seconds)                                  // audio and video definition
                .append(formatOutput).append("-acodec aac -vcodec ").append(codec).append(" -map [v] -map 0:a? -map 1:a -t ").append(seconds)                                  // audio and video definition
                .append(formatOutput).append("-y ")
                .append(formatOutput).append(configThreads)// do not override if "output" already exists
                .append(formatOutput).append("-r ").append(framerate)
                .append(" -pix_fmt yuv420p ")
                .append(Config.instance().ffmpeg().encoderOptions())                                                  // video definition
                .append(" ").append(FileUtils.escapeWhitespaces(output)).toString();                                                                        // result

        Instant start = Instant.now();
        CLI.exec(command, this);
        if (log.isInfoEnabled()) {
            log.info("{} {} (created) (length {}s, enc-time {},  directory {})",
                    Logs.yellow("finished builder"),
                    output.getName(),
                    seconds,
                    Logs.time(start),
                    output.getParent()
            );
        }

        return output;
    }

    @Override
    public File fromVideoToVideo(File input, File output, int seconds, String destinationDir) {
        var framerate = Config.instance().ffmpeg().framerate();
        var configThreads = Config.instance().ffmpeg().threads();
        var configQuiet = Config.instance().ffmpeg().loggingConfig();
        var codec = Config.instance().ffmpeg().codec();

        var filterComplex = "\""+toString();

        if (!output.getName().endsWith("mp4")) {
            String current = output.toString();
            current = current.substring(0, current.length() - 4) + ".mp4";
            output = new File(current);
        }

        var formatOutput = " \\\n\t "; // results in a command better readable
        //formatOutput = " "; // results in a command with one line
        var command = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(configQuiet).append(" ").append(configThreads)                                       // ffmpeg
                //.append(formatOutput).append("-loop 1 -framerate ").append(framerate).append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input image
                .append(formatOutput).append(" ").append(" -t ").append(seconds).append(" -i ").append(FileUtils.escapeWhitespaces(input)) // input video
                .append(formatOutput).append("-filter_complex ").append(filterComplex)                                                             // see above: zoom-in
                .append(formatOutput).append("-acodec aac -vcodec ").append(codec).append(" -map [v] -t ").append(seconds)                                  // audio and video definition
                .append(formatOutput).append("-y ")
                .append(formatOutput).append(configThreads)// do not override if "output" already exists
                .append(formatOutput).append("-r ").append(framerate)
                .append(" -pix_fmt yuv420p ")
                .append(Config.instance().ffmpeg().encoderOptions())                                                  // video definition
                .append(" ").append(FileUtils.escapeWhitespaces(output)).toString();                                                                        // result

        var start = Instant.now();
        CLI.exec(command, this);
        if (log.isInfoEnabled()) {
            log.info("{} {} (created) (length {}s, enc-time {},  directory {})",
                    Logs.yellow("finished builder"),
                    output.getName(),
                    seconds,
                    Logs.time(start),
                    output.getParent()
            );
        }

        return output;
    }
}
