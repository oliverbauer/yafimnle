package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Colorspace implements ImageFilter {
    @Override
    public File process(File input, String destinationDir) {
        String path = input.getParent();
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = path+"/"+name+"-intermediate-partialblur."+suffix;

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        CLI.exec(Config.instance().magick().command()+" "+i+" -colorspace GRAY "+o, this);

        return new File(newName);
    }
}
