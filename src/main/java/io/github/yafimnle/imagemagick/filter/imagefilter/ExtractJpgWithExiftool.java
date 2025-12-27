package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class ExtractJpgWithExiftool implements ImageFilter {
    @Override
    public File process(File input, String destinationDir) {
        File output = new File(input.getAbsolutePath()+"-jpg.jpg");
        CLI.exec("exiftool -b -JpgFromRaw "+ FileUtils.escapeWhitespaces(input)+" > "+FileUtils.escapeWhitespaces(output), this);
        return output;
    }
}
