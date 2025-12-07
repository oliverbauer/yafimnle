package io.github.yafimnle.melt;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.H264Exception;
import io.github.yafimnle.utils.CLI;
import io.github.yafimnle.utils.FileUtils;
import io.github.yafimnle.utils.Logs;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class MeltConcat {
    public static MeltConcat instance() {
        return new MeltConcat();
    }

    public void meltConcat(String name, File... inputs) {
        var destinationDir = Config.instance().destinationDir();
        var codec = Config.instance().ffmpeg().codec();

        List<String> list = Arrays
                .stream(inputs)
                .map(FileUtils::escapeWhitespaces)
                .toList();

        if (new File(destinationDir + "/" + name + "-melt.sh").exists()) {
            log.warn("File {}/{}-melt.sh exists, will not be overwritten!", destinationDir, name);
        } else {
            var sb = new StringBuilder();
            sb.append("melt \\\n");
            for (int i = 0; i <= list.size()- 1; i++) {
                String fqn = list.get(i);

                if (i == 0) {
                    sb.append(" ").append(fqn).append(" \\\n");
                } else {
                    sb.append(" ").append(fqn).append(" -mix 25 -mixer luma").append(" \\\n");
                }
            }

            sb.append(" -consumer avformat:").append(destinationDir).append("/").append(name).append(".mp4 acodec=libmp3lame vcodec=").append(codec).append(" crf=23");

            // Write videoonly-File
            var file = new File(destinationDir + "/" + name + "-melt.sh");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.append(sb);
            } catch (IOException e) {
                throw new H264Exception(e);
            }
            log.info("Generated {}/{}-melt.sh which will be executed right now!", destinationDir, name);
        }

        if (new File(destinationDir + "/" + name + ".mp4").exists()) {
            log.warn("File {}/{}.mp4 exists, will not be overwritten!", destinationDir, name);
        } else {
            var start = Instant.now();
            CLI.exec("bash " + destinationDir + "/" + name + "-melt.sh", this);
            log.info("Done after {}", Logs.time(start));
        }
    }
}
