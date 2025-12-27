package io.github.yafimnle.imagemagick;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.AllowedFiles;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.ImageTransformer;
import io.github.yafimnle.ffmpeg.filtercomplex.FilterComplex;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.FilterChainEntry;
import io.github.yafimnle.imagemagick.ar.AbstractAROptions;
import io.github.yafimnle.imagemagick.filter.ImageFilters;
import io.github.yafimnle.imagemagick.filter.imagefilter.ImageFilter;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ImageBuilder extends Builder {
    int seconds = -1;
    AbstractAROptions arOptions = Config.instance().magick().defaultImageAspectRatio();
    FilterComplex filterComplex = null;
    List<ImageFilter> imageFilterProcessBeforeCrop = new ArrayList<>();
    List<ImageFilter> imageFilterProcessAfterCrop = new ArrayList<>();

    public ImageBuilder(File file) {
        super(file);

        AllowedFiles.checkIfAllowedImage(file);

        if (file.getName().endsWith("nef") || file.getName().endsWith("NEF")) {
            appendImageFilterBeforeCrop(ImageFilters.fromNEF());
        }
    }

    @Override
    public File create() {
        var config = Config.instance();
        var destinationDir = config.destinationDir();
        if (seconds == -1) {
            seconds = config.ffmpeg().imgToVidSeconds();
        }

        String targetFilename;
        if (encodingResult() == null) {
            // default
            targetFilename = originalInputFile().getName() + "-" + config.resolution().apprev();
        } else {
            log.info("name of encoded result defined as {}", encodingResult());

            targetFilename = encodingResult().getName();
        }
        File intermediateImage = new File(destinationDir + File.separator + originalInputFile().getParent() + File.separator + targetFilename + ".jpg");
        encodingResult(new File(destinationDir + File.separator + originalInputFile().getParent() + File.separator + targetFilename + ".mp4"));


        encodedLength(seconds);
        if (encodingResult().exists()) {
            log.info("{} {} (existing) (length {}s, directory {})", Logs.yellow("finished builder"), encodingResult().getName(), seconds, encodingResult().getParent());
            return encodingResult();
        } else {
            log.info("{} {} {} (length {}s, directory {})", Logs.yellow("creating builder"), intermediateImage.getName(), encodingResult().getName(), seconds, encodingResult().getParent());
        }

        // Preprocessing chain
        var preprocessed = originalInputFile();
        imageFilterProcessBeforeCrop.addAll(config.magick().preprocessFilters()); // defaults to empty list
        for (ImageFilter preprocessingFilter : imageFilterProcessBeforeCrop) {
            preprocessed = preprocessingFilter.process(preprocessed, destinationDir);
        }

        CLI.exec(arOptions.command(preprocessed, intermediateImage), this);

        // required by e.g. AR.extractResolution(7008, 2160, 0, 1500)
        for (ImageFilter imageFilter : imageFilterProcessAfterCrop) {
            intermediateImage = imageFilter.process(intermediateImage, destinationDir);
        }

        // is an override defined?
        if (Config.instance().transformConfig().imageTransformation() != null) {
            filterComplex = Config.instance().transformConfig().imageTransformation();
        }

        var command = new ImageTransformer().transformImage(intermediateImage, encodingResult(), filterComplex.getFilterComplex(), seconds);
        CLI.exec(command, this);

        return encodingResult();
    }

    public ImageBuilder filterCompex(FilterComplex filterComplex) {
        this.filterComplex = filterComplex;
        return this;
    }

    public ImageBuilder filterCompex(FilterChainEntry... filterChainEntry) {
        this.filterComplex = FilterComplex.of(false, filterChainEntry);
        return this;
    }

    public ImageBuilder seconds(int seconds) {
        this.seconds = seconds;
        return this;
    }

    public ImageBuilder ar(AbstractAROptions arOptions) {
        this.arOptions = arOptions;
        return this;
    }

    public ImageBuilder appendImageFilterBeforeCrop(ImageFilter imageFilter) {
        this.imageFilterProcessBeforeCrop.add(imageFilter);
        return this;
    }

    public ImageBuilder appendImageFilterAfterCrop(ImageFilter imageFilter) {
        this.imageFilterProcessAfterCrop.add(imageFilter);
        return this;
    }
}