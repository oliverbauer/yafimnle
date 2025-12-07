package io.github.yafimnle.common;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.H264Exception;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
@Data
public abstract class Builder {
    private File originalInputFile;
    private File originalInputFileCopy; // in temp directory
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

    protected File prepareTemporaryDirectory(File sourceFile, String destinationDir) {
        // TODO re-check
        if (originalInputFileCopy != null && originalInputFileCopy.exists()) {
            log.trace("copy   done (already exists) {}", originalInputFileCopy);
            return originalInputFileCopy;
        } else {
            File targetDir = new File(destinationDir + "/" + sourceFile.getParent() + "/");
            targetDir.mkdirs();

            originalInputFileCopy = new File(targetDir + "/" + originalInputFile.getName());

            return originalInputFile;
        }
    }
}