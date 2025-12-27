package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

/**
 * TODO Create additional config with path to waifu, make n configurable
 */
public class Waifu2xUpscale implements ImageFilter {
    @Override
    public File process(File input, String destinationDir) {
        String newName = destinationDir + "/" + input.getParent() + "/" + input.getName() + "-waifu2x-upscale.jpg";

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));

        CLI.exec("/home/oliver/Downloads/waifu2x-ncnn-vulkan-20250915-linux/waifu2x-ncnn-vulkan -i "+i+" -o "+o+" -n 3 -s 2", this);

        return new File(newName);
    }
}
