package io.github.yafimnle.image.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Paint implements ImageFilter {
    int x;
    int y;
    int width;
    int height;
    int factor;

    public Paint(int x, int y, int width, int height, int factor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height= height;
        this.factor = factor;
    }

    @Override
    public File process(File input, String destinationDir) {
        String path = input.getParent();
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = path+"/"+name+"-intermediate-paint."+suffix;

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        CLI.exec(Config.instance().magick().command()+" "+i+" -region "+width+"x"+height+"+"+x+"+"+y+" -paint "+factor+" +region "+o, this);

        return new File(newName);
    }
}

