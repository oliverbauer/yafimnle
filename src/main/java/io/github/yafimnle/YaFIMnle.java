package io.github.yafimnle;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFMpegScriptAudio;
import io.github.yafimnle.ffmpeg.FFMpegScriptVideo;
import io.github.yafimnle.image.ImageBuilder;
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
        return new ImageBuilder(new File(sourcePath+"/"+name));
    }

    public static VideoBuilder vid(String name) {
        var sourcePath = Config.instance().sourceDir();
        return new VideoBuilder(new File(sourcePath+"/"+name));
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

    public File create() {
        String codec = Config.instance().ffmpeg().codec();

        if (log.isInfoEnabled()) {
            var pictures = builders
                    .stream()
                    .filter(ImageBuilder.class::isInstance)
                    .count();
            var videos = builders.size() - pictures;
            log.info("*********************************");
            log.info("* Creating video: {}, in directory {}", outputscript, destinationDir);
            log.info("* Output quality: {}, CRF: {}", config.resolution().apprev(), config.ffmpeg().encoderOptions());
            log.info("* Number of Inputs: {} ({} images, {} videos)", builders.size(), pictures, videos);
            log.info("*");
            log.info("* Config:");
            log.info("* - fade between composition[s]:           {}", config.ffmpeg().fadelength());
            log.info("* - img->vid seconds:                      {}", config.ffmpeg().imgToVidSeconds());
            log.info("* - *  ->vid codec:                        {}", config.ffmpeg().codec());
            log.info("* - *  ->vid framerate:                    {}", config.ffmpeg().framerate());
            log.info("* - vid->vid scale-flags:                  {}", config.ffmpeg().vid2vidscaleFlags());

            log.info("* - threads:                               {}", config.ffmpeg().threads());
            if (config.resolution() == Resolution.ULTRA_HD) {
                log.info("* - video h264 rc_lookahead: {}", config.ffmpeg().vidEncH264RCLookahreadFor2160p());
            }
            log.info("*********************************");
            // TODO log outline config
        }

        invokeBuilder(); // creates intermediate files


        // TODO Recheck if separation of audioonly/videoonly is still neccessary... new approach merges only videos with audio

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
        log.debug("Videolength: {}", ende);
        ffMpegScriptVideo.fade(builders);

        var mp4Output = destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".mp4";
        switch (config.resolution()) {
            case ULTRA_HD -> {
                /*
                 * rc_lookahead=0 reduces memory significantly!
                 */
                // TODO -profile:v high scheint bei hevc_nvenc nicht erlaubt zu sein
                var profile = "-profile:v high";
                if (codec.equals("hevc_nvenc")) {
                    profile = "";
                }

                var addVideoEncOptions = "-c:v "+codec+" "+profile+" -pix_fmt yuv420p "+config.ffmpeg().encoderOptions()+" -x264-params rc_lookahead=" + config.ffmpeg().vidEncH264RCLookahreadFor2160p() + ":threads=2:slices=0";
                // TODO r 25?
                // TODO crf
                videoonlyStringBuilder.append(" " + addVideoEncOptions + " -acodec aac -map \"[v]\" -y " + config.ffmpeg().threads() + " " + mp4Output).append("\n");
            }
            case FULL_HD -> {
                // TODO -profile:v high scheint bei hevc_nvenc nicht erlaubt zu sein
                var profile = "-profile:v high";
                if (codec.equals("hevc_nvenc")) {
                    profile = "";
                }

                var addVideoEncOptions = "-c:v "+codec+" "+profile+" -pix_fmt yuv420p " + config.ffmpeg().encoderOptions() + " -x264-params rc_lookahead="+ config.ffmpeg().vidEncH264RCLookahreadFor1080p();
                videoonlyStringBuilder.append(" " +addVideoEncOptions + " -acodec aac -map \"[v]\" -y " + config.ffmpeg().threads() + " " + mp4Output).append("\n");
            }
            case LOW_QUALITY -> {
                videoonlyStringBuilder.append(" -c:v "+codec+" -preset veryfast -pix_fmt yuv420p -acodec aac -map \"[v]\" -y " + config.ffmpeg().threads() + " " + mp4Output).append("\n");
            }
        }
        videoonlyStringBuilder.append("end=$(date)").append("\n");
        videoonlyStringBuilder.append("echo \"Encoding took time from $start to $end\"").append("\n");
        FileUtils.writeStringBuilderToFile(videoonlyStringBuilder, destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".sh");
        // VIDEO END

        return convert(ende);
    }

    private File convert(int ende) {
        if (new File(destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".aac").exists()) {
            log.warn(Logs.red("File {}/{}-audioonly-{}.aac already exists.. will not be overwritten"), destinationDir, outputscript, config.resolution().apprev());
        } else {
            CLI.exec("bash " + destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".sh", this);
        }

        if (overlayMp3 != null) {
            var name = FileUtils.escapeWhitespaces(overlayMp3);

            // Verzeichnis erstellen falls nicht vorhanden
            var path = destinationDir+"/"+FileUtils.escapeWhitespaces(overlayMp3.getParentFile());
            new File(path).mkdirs();

            CLI.exec("cp " + name + " " + destinationDir+"/"+FileUtils.escapeWhitespaces(overlayMp3.getParentFile()), this);
            log.info("Generate audio-overlay-File...");
            // TODO ? -filter:a loudnorm
            CLI.exec("ffmpeg -ss 0 -to " + ende + " -i " + destinationDir + "/" + name + " -af \"afade=t=in:st=0:duration=5,afade=t=out:st=" + (ende - 5) + ":duration=5,volume=0.5\" -b:a 192k -ar 44100 -ac 2 -y " + destinationDir + "/" + name + "-cutted.mp3", this);
            CLI.exec("ffmpeg -i " + destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".aac -i " + destinationDir + "/" + name + "-cutted.mp3 -filter_complex amix=inputs=2:duration=first:dropout_transition=3 -b:a 192k -ar 44100 -ac 2 -y " + destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + "-overlay.aac", this);
        }

        if (new File(destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".mp4").exists()) {
            log.warn(Logs.red("File {}/{}-videoonly-{}.mp4 already exists.. will not be overwritten"), destinationDir, outputscript, config.resolution().apprev());
        } else {
            var start = Instant.now();
            CLI.exec("bash " + destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".sh", this);
            log.info("Done after {}", Logs.time(start));
        }


        String audioInput;
        if (overlayMp3 == null) {
            audioInput = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + ".aac";
        } else {
            audioInput = destinationDir + "/" + outputscript + "-audioonly-" + config.resolution().apprev() + "-overlay.aac";
        }
        var videoInput = destinationDir + "/" + outputscript + "-videoonly-" + config.resolution().apprev() + ".mp4";
        var finalResult = destinationDir + "/" + outputscript + "-full-" + config.resolution().apprev() + ".mp4";

        if (new File(finalResult).exists()) {
            log.warn(Logs.red("File {} already exists.. will not be overwritten"), finalResult);
        } else {
            CLI.exec(Config.instance().ffmpeg().command()+" " +config.ffmpeg().loggingConfig() + " -i "+ videoInput + " -i " + audioInput + " -c copy -map 0:v -map 1:a -y " + finalResult, this);
        }
        return new File(finalResult);
    }
}
