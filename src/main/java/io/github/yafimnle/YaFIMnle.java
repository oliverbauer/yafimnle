package io.github.yafimnle;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFMpegJoiner;
import io.github.yafimnle.ffmpeg.FFMpegScriptAudio;
import io.github.yafimnle.ffmpeg.FFMpegScriptVideo;
import io.github.yafimnle.image.ImageBuilder;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import io.github.yafimnle.video.VideoBuilder;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class YaFIMnle {
    private final Config config = Config.instance();
    private final FFMpegScriptVideo ffMpegScriptVideo;
    private final FFMpegScriptAudio ffMpegScriptAudio;

    private final List<Builder> builders = new ArrayList<>();

    String destinationDir;

    File overlayMp3 = null;

    String outputscript;

    // destinationDir set in Config
    public YaFIMnle(String outputscript) {
        this.destinationDir = Config.instance().destinationDir();
        this.outputscript = outputscript;

        File file = new File(destinationDir);
        file.mkdirs();

        if (!file.exists()) {
            throw new IllegalStateException("Temp dir could not be created and is not yet existing");
        }
        ffMpegScriptVideo = new FFMpegScriptVideo();

        String aacName = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".aac";
        ffMpegScriptAudio = new FFMpegScriptAudio(aacName);
    }

    public static ImageBuilder img(String name) {
        var sourcePath = Config.instance().sourceDir();
        var overrideImageTransformation = Config.instance().transformConfig().imageTransformation();
        if (overrideImageTransformation == null) {
            return new ImageBuilder(new File(sourcePath + "/" + name));
        } else {
            return new ImageBuilder(new File(sourcePath + "/" + name)).transform(overrideImageTransformation);
        }
    }

    public static ImageBuilder img(String name, Transformation transformations) {
        var sourcePath = Config.instance().sourceDir();
        var overrideImageTransformation = Config.instance().transformConfig().imageTransformation();
        if (overrideImageTransformation == null) {
            return new ImageBuilder(new File(sourcePath + "/" + name)).transform(transformations);
        } else {
            return new ImageBuilder(new File(sourcePath + "/" + name)).transform(overrideImageTransformation);
        }
    }

    public static VideoBuilder vid(String name) {
        var sourcePath = Config.instance().sourceDir();
        var overrideVideoTransformation = Config.instance().transformConfig().videoTransformation();
        if (overrideVideoTransformation == null) {
            return new VideoBuilder(new File(sourcePath + "/" + name));
        } else {
            return new VideoBuilder(new File(sourcePath + "/" + name)).transform(overrideVideoTransformation);
        }
    }

    public static VideoBuilder vid(String name, Transformation transformations) {
        var sourcePath = Config.instance().sourceDir();
        var overrideVideoTransformation = Config.instance().transformConfig().videoTransformation();
        if (overrideVideoTransformation == null) {
            return new VideoBuilder(new File(sourcePath + "/" + name)).transform(transformations);
        } else {
            return new VideoBuilder(new File(sourcePath + "/" + name)).transform(overrideVideoTransformation);
        }
    }

    public YaFIMnle of(List<Builder> builder) {
        builders.addAll(builder);
        return this;
    }

    public YaFIMnle of(Builder... builder) {
        builders.addAll(Arrays.asList(builder));
        return this;
    }

    // TODO Needs a test!
    public YaFIMnle overlayMp3(File file) {
        this.overlayMp3 = file;
        // ffmpeg -i 01-prag-audioonly-540p.aac -i file -filter_complex amix=inputs=2:duration=first:dropout_transition=3 overlay.aac
        return this;
    }

    private void invokeBuilder() {
        for (Builder builder : builders) {
            builder.create();
        }
    }

    /**
     * This method only joins results of builders, there is not re-encoding no "Fade" or no change in quality settings
     * possible.
     * <p>
     * But this is amazingly fast. :-)
     *
     * @return
     */
    public File createJoin() {
        List<File> files = new ArrayList<>();
        for (Builder builder : builders) {
            File file = builder.create();
            files.add(file);
        }

        FFMpegJoiner joiner = new FFMpegJoiner();
        return joiner.join(outputscript, files);
    }

    /**
     * Creates a new video accordingly to all defined builders.
     * Note: After each builder has created its result, all videos will be together re-encoded.
     * This allows things like "Fade" of the builder-results or using the final result with an other crf (if h264 is your codec)
     * as using in builders, e.g. very high quality in builder (crf 20) and final video in crf 23.
     *
     * @return
     */
    public File create() {
        var finalResult = destinationDir + "/" + outputscript + "-full-" + config.resolution().apprev() + ".mp4";
        if (new File(finalResult).exists()) {
            log.warn("Result already exists, skipping further new encoding. Result: {}", finalResult);
            return new File(finalResult);
        }

        String codec = Config.instance().ffmpeg().codec();

        if (log.isInfoEnabled()) {
            var pictures = builders
                    .stream()
                    .filter(ImageBuilder.class::isInstance)
                    .count();
            var videos = builders.size() - pictures;
            log.info("*********************************");
            log.info("* Creating video: {}, in directory {}", outputscript, destinationDir);
            log.info("* Output quality: {}, encoder options: {}", config.resolution().apprev(), config.ffmpeg().encoderOptions());
            log.info("* Number of Inputs: {} ({} images, {} videos)", builders.size(), pictures, videos);
            log.info("*");
            log.info("* Config:");
            log.info("* - fade between composition[s]:           {}", config.ffmpeg().fadelength());
            log.info("* - img->vid seconds:                      {}", config.ffmpeg().imgToVidSeconds());
            log.info("* - *  ->vid codec:                        {}", config.ffmpeg().codec());
            log.info("* - *  ->vid frame rate:                   {}", config.ffmpeg().framerate());
            log.info("* - vid->vid scale-flags:                  {}", config.ffmpeg().vid2vidscaleFlags());

            log.info("* - threads:                               {}", config.ffmpeg().threads());
            if (config.resolution() == Resolution.ULTRA_HD) {
                log.info("* - video h264 rc_lookahead: {}", config.ffmpeg().vidEncH264RCLookahreadFor2160p());
            }
            log.info("*********************************");
            // TODO log outline config
        }

        invokeBuilder(); // creates intermediate files

        // AUDIO
        var audioonlyStringBuilder = ffMpegScriptAudio.stringBuilder();
        ffMpegScriptAudio.appendInputs(builders);
        ffMpegScriptAudio.fade(builders);
        FileUtils.writeStringBuilderToFile(audioonlyStringBuilder, destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".sh");
        // AUDIO END

        // VIDEO
        var videoonlyStringBuilder = ffMpegScriptVideo.stringBuilder();
        ffMpegScriptVideo.appendInputs(builders);
        var ende = ffMpegScriptVideo.fullLength();
        videoonlyStringBuilder.append("  -filter_complex \"\\").append("\n");
        log.debug("Video length: {}", ende);
        ffMpegScriptVideo.fade(builders);

        var mp4Output = destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".mp4";

        var profile = "";
        if (config.ffmpeg().profile() != null) {
            profile = "-profile:v " + config.ffmpeg().profile();
        }
        var preset = "";
        if (config.ffmpeg().preset() != null) {
            preset = "-preset " + config.ffmpeg().preset();
        }

        var addVideoEncOptions = "-c:v " + codec + " " + profile + " " + preset + " -pix_fmt yuv420p " + config.ffmpeg().encoderOptions() + " " + config.ffmpeg().vidEncH264RCLookahreadFor2160p();
        // TODO r 25?
        // TODO crf
        videoonlyStringBuilder.append(" " + addVideoEncOptions + " -acodec aac -b:a 192k -ac 2 -ar 44100 -map \"[v]\" -y " + config.ffmpeg().threads() + " " + mp4Output).append("\n");

        videoonlyStringBuilder.append("end=$(date)").append("\n");
        videoonlyStringBuilder.append("echo \"Encoding took time from $start to $end\"").append("\n");
        FileUtils.writeStringBuilderToFile(videoonlyStringBuilder, destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".sh");
        // VIDEO END

        return convert(ende);
    }

    private File convert(int ende) {
        var audioOnlySh = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".sh";
        var audioOnlyAAC = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".aac";
        var audioOnlyOverlayAAC = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + "-overlay.aac";

        var videoOnlySh = destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".sh";
        var videoOnlyMP4 = destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".mp4";
        var finalResult = destinationDir + "/" + outputscript + "-full-" + config.resolution().apprev() + ".mp4";


        if (new File(audioOnlyAAC).exists()) {
            log.warn(Logs.red("File {} already exists.. will not be overwritten"), audioOnlyAAC);
        } else {
            CLI.exec("bash " + audioOnlySh, this);
        }

        String audioInput;
        if (overlayMp3 != null) {
            var name = FileUtils.escapeWhitespaces(overlayMp3);

            // Verzeichnis erstellen falls nicht vorhanden
            var path = destinationDir + "/" + FileUtils.escapeWhitespaces(overlayMp3.getParentFile());
            new File(path).mkdirs();

            String i = FileUtils.escapeWhitespaces(overlayMp3);

            try {
                CLI.exec("mkdir "+destinationDir+"/"+FileUtils.escapeWhitespaces(overlayMp3.getParentFile()), this);
            } catch (Exception e) {
                // ignore directory exists
            }

            CLI.exec("cp " + i + " " + destinationDir + "/" + FileUtils.escapeWhitespaces(overlayMp3.getParentFile()), this);
            log.info("Generate audio-overlay-File...");
            // TODO ? -filter:a loudnorm
            // TODO fade-duration should be configurable
            var parent = FileUtils.escapeWhitespaces(overlayMp3.getParentFile());
            var targetMp3 = destinationDir + parent + "/"+overlayMp3.getName() + "-cutted.mp3";

            i = destinationDir+"/"+FileUtils.escapeWhitespaces(overlayMp3.getParentFile())+"/"+overlayMp3.getName();
            CLI.exec("ffmpeg -ss 0 -to " + ende + " -i " + i + " -af \"afade=t=in:st=0:duration=5,afade=t=out:st=" + (ende - 5) + ":duration=5,volume=0.5\" -b:a 192k -ar 44100 -ac 2 -y " + targetMp3, this);
            CLI.exec("ffmpeg -i " + audioOnlyAAC +" -i " + destinationDir + "/" + name + "-cutted.mp3 -filter_complex amix=inputs=2:duration=first:dropout_transition=3 -b:a 192k -ar 44100 -ac 2 -y " + audioOnlyOverlayAAC, this);

            audioInput = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + "-overlay.aac";
        } else {
            audioInput = audioOnlyAAC;
        }

        if (new File(videoOnlyMP4).exists()) {
            log.warn(Logs.red("File {} already exists.. will not be overwritten"), videoOnlyMP4);
        } else {
            var start = Instant.now();
            CLI.exec("bash " + videoOnlySh, this);
            log.info("Done after {}", Logs.time(start));
        }

        // put videoonly + audioonly together
        if (new File(finalResult).exists()) {
            log.warn(Logs.red("File {} already exists.. will not be overwritten"), finalResult);
        } else {
            CLI.exec(Config.instance().ffmpeg().command() + " " + config.ffmpeg().loggingConfig() + " -i " + videoOnlyMP4 + " -i " + audioInput + " -c copy -map 0:v -map 1:a -y " + finalResult, this);
        }
        return new File(finalResult);
    }
}
