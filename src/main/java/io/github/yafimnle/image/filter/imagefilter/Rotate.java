package io.github.yafimnle.image.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Rotate implements ImageFilter {
    private final int degrees;

    public Rotate() {
        this(90);
    }

    public Rotate(int degrees) {
        this.degrees = degrees;
    }

    @Override
    public File process(File input, String destinationDir) {
        String path = input.getParent();
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = path+"/"+name+"-rotated."+suffix;

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        var magick = Config.instance().magick().command();
        var dim = Config.instance().resolution().dimension();

        var command = String.format("%s %s -rotate %s -resize %s -quality 100 %s", magick, i, degrees, dim, o);
        CLI.exec(command, this);

        return new File(newName);
    }
}
