package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;

import java.io.File;

public class FFMpegJoiner {
    public void join(String outputscript, File... files) {
        String destinationDir = Config.instance().destinationDir();
        String apprev = Config.instance().resolution().apprev();

        StringBuilder stringBuilder = new StringBuilder();
        for (File file : files) {
            stringBuilder.append("file '").append(file.getName()).append("'\n");
        }
        // ffmpeg -f concat -i mylist.txt -c copy output.mp4

        String filename = destinationDir + "/" + outputscript + "-files-" + apprev + ".txt";
        FileUtils.writeStringBuilderToFile(stringBuilder, filename);

        String command = Config.instance().ffmpeg().command();
        CLI.exec(command+" -f concat -i "+filename+" -c copy "+destinationDir+"/"+outputscript+"-"+apprev+"-full.mp4", this);
    }
}
