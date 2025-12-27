package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Extend implements ImageFilter {
    @Override
    public File process(File input, String destinationDir) {
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = destinationDir+"/"+name+"-extend."+suffix;

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        var magick = Config.instance().magick().command();
        var dim = Config.instance().resolution().dimension();

        var command = String.format("%s %s -resize %s -background black -gravity center -extent %s -quality 100 %s", magick, i, dim, dim, o);
        CLI.exec(command, this);

        return new File(newName);
    }
}
