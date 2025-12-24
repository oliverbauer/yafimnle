package io.github.yafimnle.image.filter.imagefilter;

import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class Upscayl implements ImageFilter {
    public static final String installationPath = "/home/oliver/Downloads/upscayl-2.25.0-linux/";

    @Override
    public File process(File input, String destinationDir) {
        String newName = destinationDir + "/" + input.getParent() + "/" + input.getName() + "-upscayl.jpg";

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        /*
         * remacri-4x
         * ultrasharp-4x
         */
        var command = installationPath+"resources/bin/upscayl-bin -i "+i+" -s 2 -m "+installationPath+"/resources/models -n remacri-4x -o "+o;

        CLI.exec(command, this);

        return new File(newName);
    }
}
