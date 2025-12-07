package io.github.yafimnle.transformation;

import java.io.File;

public interface Transformation {
    File fromImageToVideo(File input, File output, int seconds, String destinationDir);
    File fromVideoToVideo(File input, File output, int seconds, String destinationDir);
}
