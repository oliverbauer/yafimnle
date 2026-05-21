package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class PartialBlur implements ImageFilter {
    int x;
    int y;
    int width;
    int height;
    int blur;

    public PartialBlur(int x, int y, int width, int height, int blur) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height= height;
        this.blur = blur;
    }

    @Override
    public File process(File input, String destinationDir) {
        var path = input.getParent();
        var name = input.getName();
        var suffix = name.substring(name.length()-3);

        var newName = path+"/"+name+"-intermediate-partialblur."+suffix;

        var i = FileUtils.escapeWhitespaces(input);
        var o = FileUtils.escapeWhitespaces(new File(newName));

        CLI.exec(Config.instance().magick().command()+" "+i+" -region "+width+"x"+height+"+"+x+"+"+y+" -blur 0x"+blur+" +region "+o, this);

        return new File(newName);
    }
}

