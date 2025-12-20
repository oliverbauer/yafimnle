package io.github.yafimnle.video;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.AllowedFiles;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;

@Log4j2
public class VideoBuilder extends Builder {
    String from;
    String to;

    Transformation transformation = null;

    public VideoBuilder(File file) {
        super(file);

        AllowedFiles.checkIfAllowedVideo(file);
    }

    @Override
    public File create() {
        var config = Config.instance();

        // TODO consider a "Preprocessing chain" like in ImageBuilder. This enables the possibility to use vidstabdetect + vidstabtransform
        // ffmpeg -i input.mp4 -vf vidstabdetect -f null -
        // ffmpeg -i input.mp4 -vf vidstabtransform output.mp4

        // Configuration
        var configThreads = config.ffmpeg().threads();
        var loggingConfig = config.ffmpeg().loggingConfig();
        var framerate = config.ffmpeg().framerate();
        var filterAudio = config.ffmpeg().vid2vidaudioFilter();
        var targetResolution = config.resolution();
        var destinationDir = config.destinationDir();
        var codec = config.ffmpeg().codec();

        if (config.ffmpeg().forceSkipReencoding()) {
            log.info("Force skip reencoding for original input: {}", originalInputFile());
            encodingResult(originalInputFile());
            encodedLength(FFProbe.instance().seconds(encodingResult()));
            return originalInputFile();
        }

        var targetFilename = originalInputFile().getName() + "-" + targetResolution.apprev() + ".mp4";
        if (encodingResult() == null) {
            encodingResult(new File(destinationDir + "/" + originalInputFile().getParent() + "/" + targetFilename));
        } else {
            log.debug("name of encoded result defined as {}", encodingResult());
        }

        int targetLength;
        if (from != null && to != null) {
            String[] toTime = to.split(":");
            String[] fromTime = from.split(":");
            var toAsSec = Integer.parseInt(toTime[0]) * 60 * 60 + Integer.parseInt(toTime[1]) * 60 + Integer.parseInt(toTime[2]);
            var fromAsSec = Integer.parseInt(fromTime[0]) * 60 * 60 + Integer.parseInt(fromTime[1]) * 60 + Integer.parseInt(fromTime[2]);

            targetLength = Integer.parseInt(String.valueOf(toAsSec - fromAsSec));
            encodedLength(toAsSec - fromAsSec);
        } else {
            targetLength = FFProbe.instance().seconds(originalInputFile());
        }

        if (encodingResult().exists()) {
            log.info("{} {} (existing) (length {}s, directory {})",
                    Logs.yellow("finished builder"),
                    encodingResult().getName(),
                    targetLength,
                    encodingResult().getParent()
            );
            encodedLength(FFProbe.instance().seconds(encodingResult()));
            return encodingResult();
        } else {
            log.info("{} {} (length {}s, directory {})",
                    Logs.yellow("creating builder"),
                    encodingResult().getName(),
                    targetLength,
                    encodingResult().getParent()
            );
        }

        var copy = prepareTemporaryDirectory(originalInputFile(), destinationDir);

        var sb = new StringBuilder();
        sb.append(Config.instance().ffmpeg().command())
                .append(" ")
                .append(loggingConfig)
                .append(" ")
                .append(configThreads);
        if (from != null && to != null) {
            sb.append(" -ss ");
            sb.append(from);
            sb.append(" -to ");
            sb.append(to);
        } else {
            encodedLength(targetLength);
        }

        sb.append(" -i ");
        sb.append(FileUtils.escapeWhitespaces(copy));
        sb.append(" ");
        if (transformation != null) {
            // filter not allowed: -vf/-af/-filter and -filter_complex cannot be used together for the same stream.
        } else {
            sb.append(filterAudio);
        }

        var sourceResolution = FFProbe.instance().resolution(originalInputFile());
        if (!targetResolution.equals(sourceResolution)) {
            if (transformation == null) {
                sb.append(" -vf scale=");
                sb.append(targetResolution.dimension());
                sb.append(config.ffmpeg().vid2vidscaleFlags()); //  TODO scale denoise video->video
                log.debug("Source dimension {} does not equals requested dimension {}, adding option '-vf scale=wxh' on next command (scale flags defined: '{}')", sourceResolution, targetResolution, config.ffmpeg().vid2vidscaleFlags());
            }
        } else {
            log.debug("Source dimension {} already equals requested dimension, skipping option '-vf scale=wxh' on next command", sourceResolution);
        }

        if (transformation != null) {
            String filterComplex = "\"" + transformation.toString();
            sb.append(" -filter_complex ").append(filterComplex);

            // TODO [0:a]afade=type=in:duration=1:start_time=0[a]
        }


        sb.append(" -r ");
        sb.append(framerate);
        sb.append(" -acodec aac -b:a 192k -ac 2 -ar 44100 ");
        sb.append(" -t ");
        sb.append(targetLength);
        sb.append(" -y ");
        sb.append(configThreads);
        sb.append(" ");
        sb.append(" -c:v ");
        sb.append(codec);
        sb.append(" ");

        sb.append(config.ffmpeg().encoderOptions());
        if (transformation != null) {
            sb.append(" -map [v] -map [a]"); // TODO Map a
        }
        sb.append(" ");
        sb.append(FileUtils.escapeWhitespaces(encodingResult()));

        Instant start = Instant.now();
        CLI.exec(sb.toString(), this);

        log.info("{} {} (created) (length {}s, enc-time {},  directory {})",
                Logs.yellow("finished builder"),
                encodingResult().getName(),
                encodedLength(),
                Logs.time(start),
                encodingResult().getParent()
        );

        return encodingResult();
    }

    public VideoBuilder seconds(String from, String to) {
        this.from = from;
        this.to = to;
        return this;
    }

    public VideoBuilder transform(Transformation transformation) {
        this.transformation = transformation;
        return this;
    }

    // TODO speed - create a speed factor 1 is default, 2 is speedup, 0.5 is half speed

    // TODO allow to silence part of video
    // ffmpeg -i ourfavmovies.mp4 -vcodec copy -af "volume=enable='between(t,30,90)':volume=0" outourfavmovies.mp4
    // https://www.vladimircicovic.com/2020/05/ffmpeg-some-useful-tricks
}