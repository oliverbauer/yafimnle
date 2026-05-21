package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Upscayl implements ImageFilter {
    public static final String INSTALLATION_PATH = "/home/oliver/Downloads/upscayl-2.25.0-linux/";

    @Override
    public File process(File input, String destinationDir) {
        var newName = destinationDir + "/" + input.getParent() + "/" + input.getName() + "-upscayl.jpg";

        var i = FileUtils.escapeWhitespaces(input);
        var o = FileUtils.escapeWhitespaces(new File(newName));

        /*
         * remacri-4x
         * ultrasharp-4x
         */
        var command = INSTALLATION_PATH +"resources/bin/upscayl-bin -i "+i+" -s 2 -m "+ INSTALLATION_PATH +"/resources/models -n remacri-4x -o "+o;

        CLI.exec(command, this);

        return new File(newName);
    }
}
