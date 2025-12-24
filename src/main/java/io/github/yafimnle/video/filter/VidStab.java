package io.github.yafimnle.video.filter;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

/**
 * Usage of https://github.com/georgmartius/vid.stab
 */
public class VidStab implements VideoFilter {
    /*
        ffmpeg -i input.mp4 -vf vidstabdetect -f null -
        ffmpeg -i input.mp4 -vf vidstabtransform output.mp4
     */

    // TODO allow options for vidstabdetect like stepsize=32:shakiness=10:accuracy=10...
    // TODO allow options for vidstabtransform like smoothing=7
    // TODO "Make sure that you use unsharp filter provided by ffmpeg for best results (only in second pass)." (https://github.com/georgmartius/vid.stab)

    @Override
    public File process(File input, String destinationDir) {
        String newName = destinationDir + "/" + input.getParent() + "/" + input.getName() + "-stabilized.mp4";
        String transformtrf = destinationDir + "/" + input.getParent() + "/" + input.getName() + "-transforms.trf";

        if (!new File(destinationDir+"/"+input.getParent()).exists()) {
            new File(destinationDir+"/"+input.getParent()).mkdirs();
        }

        String i = FileUtils.escapeWhitespaces(input);
        String o = FileUtils.escapeWhitespaces(new File(newName));
        String t = FileUtils.escapeWhitespaces(new File(transformtrf));


        String command = Config.instance().ffmpeg().command();
        String threads = Config.instance().ffmpeg().threads();

        // first pass: vidstabdetect
        CLI.exec(command+" "+threads+" -i "+i+" -vf vidstabdetect=result="+t+" -f null -", this);

        // second pass: vidstabtransform
        var framerate = Config.instance().ffmpeg().framerate();
        var configThreads = Config.instance().ffmpeg().threads();
        var configQuiet = Config.instance().ffmpeg().loggingConfig();
        var codec = Config.instance().ffmpeg().codec();
        var formatOutput = " \\\n\t "; // results in a command better readable
        var filterComplex = "\"[0:v]vidstabtransform=input=\""+t+"\"[v];[0:a]afade=type=in:duration=1[a]\"";
        command = new StringBuilder()
                .append(Config.instance().ffmpeg().command())
                .append(" ").append(configQuiet).append(" ").append(configThreads)
                .append(formatOutput).append(" -i ").append(i)
                .append(formatOutput).append("-filter_complex ").append(filterComplex)
                .append(formatOutput).append("-acodec aac -b:a 192k -ac 2 -ar 44100 -vcodec ").append(codec).append(" -map [v] -map [a] ")
                .append(formatOutput).append("-y ")
                .append(formatOutput).append(configThreads)
                .append(formatOutput).append("-r ").append(framerate)
                .append(" -pix_fmt yuv420p ")
                .append(Config.instance().ffmpeg().encoderOptions())
                .append(" ").append(o).toString();

        CLI.exec(command, this);

        return new File(newName);
    }
}
