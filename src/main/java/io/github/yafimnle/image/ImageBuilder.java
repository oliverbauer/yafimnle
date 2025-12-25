package io.github.yafimnle.image;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.AllowedFiles;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.image.ar.AbstractAROptions;
import io.github.yafimnle.image.filter.ImageFilters;
import io.github.yafimnle.image.filter.Transformations;
import io.github.yafimnle.image.filter.imagefilter.ImageFilter;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.image.transformation.ZoomIn;
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
    Transformation transformation = new ZoomIn();
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
        File intermediateImage = new File(destinationDir + "/" + originalInputFile().getParent() + "/" + targetFilename + ".jpg");
        encodingResult(new File(destinationDir + "/" + originalInputFile().getParent() + "/" + targetFilename + ".mp4"));


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

        encodingResult(transformation.fromImageToVideo(intermediateImage, encodingResult(), seconds, destinationDir));
        return encodingResult();
    }

    public ImageBuilder seconds(int seconds) {
        this.seconds = seconds;
        return this;
    }

    public ImageBuilder ar(AbstractAROptions arOptions) {
        this.arOptions = arOptions;
        return this;
    }

    /**
     * <p>
     * A Tranformation uses FFMpeg to transform an image to a video. This ranges from Ken Burns effect to Overlay of Text/Boxes/Fade-in/Fade-out...
     * </p>
     * <br/>
     * <p>
     * Utilizes <b>filter_complex</b> of FFMpeg.
     * </p>
     * @param transformation
     * @return ImageBuilder
     * @see Transformations Predefined transformations.
     */
    public ImageBuilder transform(Transformation transformation) {
        this.transformation = transformation;
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