package io.github.yafimnle.image.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;

import java.io.File;

public class Colorspace implements ImageFilter {
    @Override
    public File process(File input, String destinationDir) {
        String path = input.getParent();
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = path+"/"+name+"-intermediate-partialblur."+suffix;

        CLI.exec(Config.instance().magick().command()+" "+input+" -colorspace GRAY "+newName, this);

        return new File(newName);
    }
}
