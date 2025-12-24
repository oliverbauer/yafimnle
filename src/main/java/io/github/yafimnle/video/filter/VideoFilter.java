package io.github.yafimnle.video.filter;

import java.io.File;

@FunctionalInterface
public interface VideoFilter {
    File process(File input, String destinationDir);
}
