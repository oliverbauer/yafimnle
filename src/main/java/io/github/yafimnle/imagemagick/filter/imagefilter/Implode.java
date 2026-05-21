package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Implode implements ImageFilter {
    int x;
    int y;
    int width;
    int height;
    double factor;

    public Implode(int x, int y, int width, int height, double factor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height= height;
        this.factor = factor;
    }

    @Override
    public File process(File input, String destinationDir) {
        var path = input.getParent();
        var name = input.getName();
        var suffix = name.substring(name.length()-3);

        var newName = path+"/"+name+"-intermediate-implode."+suffix;

        var i = FileUtils.escapeWhitespaces(input);
        var o = FileUtils.escapeWhitespaces(new File(newName));

        CLI.exec(Config.instance().magick().command()+" "+i+" -region "+width+"x"+height+"+"+x+"+"+y+" -implode "+factor+" +region "+o, this);

        return new File(newName);
    }
}

