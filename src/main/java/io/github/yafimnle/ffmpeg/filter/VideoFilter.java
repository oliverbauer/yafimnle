package io.github.yafimnle.ffmpeg.filter;

import java.io.File;

@FunctionalInterface
public interface VideoFilter {
    File process(File input, String destinationDir);
}
