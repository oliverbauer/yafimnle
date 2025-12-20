package io.github.yafimnle.image;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.AllowedFiles;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.image.ar.AbstractAROptions;
import io.github.yafimnle.image.filter.ImageFilters;
import io.github.yafimnle.image.filter.Transformations;
import io.github.yafimnle.image.filter.imagefilter.ImageFilter;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.transformation.image.ZoomIn;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import io.github.yafimnle.utils.SanityCheck;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ImageBuilder extends Builder {
    int seconds = -1;
    int rotate = -1;
    boolean fromPortrait = false;
    AbstractAROptions arOptions;
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
        var destinationDir = Config.instance().destinationDir();
        if (seconds == -1) {
            seconds = config.ffmpeg().imgToVidSeconds();
        }
        var copy = prepareTemporaryDirectory(originalInputFile(), destinationDir);

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

        if (rotate != -1) {
            CLI.exec("convert -rotate 90 -resize "+config.resolution().dimension()+" "+ FileUtils.escapeWhitespaces(originalInputFile())+" -quality 100 "+FileUtils.escapeWhitespaces(intermediateImage), this);
            originalInputFile(intermediateImage);
        }
        if (fromPortrait) {
            //convert -rotate 90 -resize 1920x1080 -background black -gravity center -extent 1920x1080 DSCI0042.jpg -quality 100 DSCI0042-rotated.jpg
            CLI.exec("convert -resize "+config.resolution().dimension()+" -background black -gravity center -extent "+config.resolution().dimension()+" "+FileUtils.escapeWhitespaces(originalInputFile())+" -quality 100 "+FileUtils.escapeWhitespaces(intermediateImage), this);
            copy = intermediateImage;
        }

        // Preprocessing chain
        var preprocessed = copy;
        imageFilterProcessBeforeCrop.addAll(config.magick().preprocessFilters()); // defaults to empty list
        for (ImageFilter preprocessingFilter : imageFilterProcessBeforeCrop) {
            preprocessed = preprocessingFilter.process(preprocessed, destinationDir);
        }

        new ImageCropper().crop(preprocessed, intermediateImage, arOptions);

        // required by e.g. AR.extractResolution(7008, 2160, 0, 1500)
        for (ImageFilter imageFilter : imageFilterProcessAfterCrop) {
            intermediateImage = imageFilter.process(intermediateImage, destinationDir);
            SanityCheck.checkDimension(intermediateImage);
        }

        encodingResult(transformation.fromImageToVideo(intermediateImage, encodingResult(), seconds, destinationDir));
        return encodingResult();
    }

    public ImageBuilder rotate(int degrees) {
        this.rotate = degrees;
        return this;
    }

    /*
     * If input source does not need a rotation use only this method, if input source needs a rotation, and then it is portrait, use rotate(90).fromPortrait().
     */
    public ImageBuilder fromPortrait() {
        this.fromPortrait = true;
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