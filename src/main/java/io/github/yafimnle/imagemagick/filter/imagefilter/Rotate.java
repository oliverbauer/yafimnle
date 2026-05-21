package io.github.yafimnle.imagemagick.filter.imagefilter;

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
        var path = input.getParent();
        var name = input.getName();
        var suffix = name.substring(name.length()-3);

        var newName = path+"/"+name+"-rotated."+suffix;

        var i = FileUtils.escapeWhitespaces(input);
        var o = FileUtils.escapeWhitespaces(new File(newName));

        var magick = Config.instance().magick().command();
        var dim = Config.instance().resolution().dimension();

        var command = String.format("%s %s -rotate %s -resize %s -quality 100 %s", magick, i, degrees, dim, o);
        CLI.exec(command, this);

        return new File(newName);
    }
}
