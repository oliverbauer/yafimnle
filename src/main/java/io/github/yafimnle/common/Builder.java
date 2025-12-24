package io.github.yafimnle.common;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.H264Exception;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
@Data
public abstract class Builder {
    private File originalInputFile;
    private File encodingResult;

    // TODO create a default in FFMPegConfig such as fadeLength below
    private FadeType fade = FadeType.FADE; // default
    /**
     * fadeLength can be set in Config, but between each to builders seperately too.
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
        new File(Config.instance().destinationDir() + "/" + originalInputFile.getParent() + "/").mkdirs();
    }

    public Builder as(String targetFilename) {
        this.encodingResult = new File(Config.instance().destinationDir() + "/" + originalInputFile.getParent() + "/" + targetFilename);
        return this;
    }


    public Builder fade(FadeType fadeType) {
        this.fade = fadeType;
        return this;
    }

    public Builder fadeLength(int length) {
        this.fadeLength = length;
        return this;
    }

    // DestinationDir is set on Config
    public abstract File create();
}