package io.github.yafimnle.image.filter.imagefilter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;

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
        String path = input.getParent();
        String name = input.getName();
        String suffix = name.substring(name.length()-3);

        String newName = path+"/"+name+"-intermediate-implode."+suffix;

        CLI.exec(Config.instance().magick().command()+" "+input+" -region "+width+"x"+height+"+"+x+"+"+y+" -implode "+factor+" +region "+newName, this);

        return new File(newName);
    }
}

