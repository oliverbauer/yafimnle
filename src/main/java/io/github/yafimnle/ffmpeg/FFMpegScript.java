package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.common.Builder;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.utils.FileUtils;
import lombok.Getter;

import java.util.List;

public abstract class FFMpegScript {
    @Getter
    private final StringBuilder stringBuilder;
    protected final Config config;
    @Getter
    private int fullLength;

    protected FFMpegScript() {
        this.stringBuilder = new StringBuilder();
        this.config = Config.instance();

        stringBuilder.append("#!/bin/bash").append("\n").append("\n");
        stringBuilder.append("start=$(date)").append("\n");
        stringBuilder.append(Config.instance().ffmpeg().command())
                .append(" ")
                .append(config.ffmpeg().threads())
                .append(" ")
                .append(config.ffmpeg().loggingConfig())
                .append("\\")
                .append("\n");
    }

    public void appendInputs(List<Builder> builders) {
        int prevEnde;
        for (int i = 0; i <= builders.size() - 1; i++) {
            var builder = builders.get(i);
            var length = builder.encodedLength(); // FFProbe.instance().seconds(builder.encodingResult())

            if (i == 0) {
                prevEnde = 0;
                fullLength = length;
            } else {
                int fadeLength = builders.get(i - 1).fadeLength();
                prevEnde = fullLength - fadeLength;
                fullLength = fullLength + (length - fadeLength);
            }

            stringBuilder.append(" -i ")
                    .append(FileUtils.escapeWhitespaces(builder.encodingResult()))
                    // the following part is only a comment which is not necessary
                    .append(" `# [")
                    .append(i)
                    .append("] ")
                    .append(length)
                    .append("s, display=[")
                    .append(prevEnde)
                    .append(", ")
                    .append(fullLength)
                    .append("]s` \\")
                    // end of comment
                    .append("\n");
        }
    }

    public abstract void fade(List<Builder> builders);
}
