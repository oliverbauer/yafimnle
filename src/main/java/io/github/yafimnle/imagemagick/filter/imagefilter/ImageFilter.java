package io.github.yafimnle.imagemagick.filter.imagefilter;

import java.io.File;

@FunctionalInterface
public interface ImageFilter {
    File process(File input, String destinationDir);
}
