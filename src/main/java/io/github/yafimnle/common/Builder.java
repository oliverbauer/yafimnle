package io.github.yafimnle.common;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.H264Exception;
import io.github.yafimnle.utils.FileUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
@Data
public abstract class Builder {
    private File originalInputFile;
    private File encodingResult;

    /**
     * fadeType can be set in Config, but between each to builders separately too.
     */
    private FadeType fadeType = Config.instance().ffmpeg().fadeType();

    /**
     * fadeLength can be set in Config, but between each to builders separately too.
     */
    private Integer fadeLength = Config.instance().ffmpeg().fadelength();

    // result of vid2vid or img2vid
    private int encodedLength;

    protected Builder(File file) {
        if (!file.exists()) {
            throw new H264Exception("File does not exist: " + file);
        }
        this.originalInputFile = file;
        // If input file is something like
        //      "/home/user/input.jpg"
        // and destinationDir something like
        //      "/home/user/mynewvideo"
        // temporary files will be put into
        //      "/home/user/mynewvideo/home/user/"
        // This allows to inspect temporary files and to have same "names" from different source directories.
        new File(Config.instance().destinationDir() + File.separator + originalInputFile.getParent() + File.separator).mkdirs();
    }

    public Builder as(String targetFilename) {
        this.encodingResult = FileUtils.file(Config.instance().destinationDir(), originalInputFile.getParent(), targetFilename);
        return this;
    }

    /**
     * Override default from FFMpegConfig.
     * @param fadeType
     * @return
     */
    public Builder fadeType(FadeType fadeType) {
        this.fadeType = fadeType;
        return this;
    }

    /**
     * Override default from FFMpegConfig.
     * @param length
     * @return
     */
    public Builder fadeLength(int length) {
        this.fadeLength = length;
        return this;
    }

    // destinationDir is set on Config
    public abstract File create();
}