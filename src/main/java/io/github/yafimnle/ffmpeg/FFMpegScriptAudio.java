package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.common.Builder;

import java.util.List;

public class FFMpegScriptAudio extends FFMpegScript {
    private final String aacName;

    public FFMpegScriptAudio(String aacName) {
        this.aacName = aacName;
    }

    @Override
    public void fade(List<Builder> builders) {
        if (builders.size() > 1) {
            stringBuilder().append("  -filter_complex \"\\").append("\n");
            for (int i = 0; i <= builders.size() - 2; i++) {
                int fadeLength = builders.get(i).fadeLength();

                if (i == 0) {
                    stringBuilder().append("  [0:a][1:a]acrossfade=duration=").append(fadeLength).append("[afade1];\\").append("\n");
                } else if (i == (builders.size() - 2)) {
                    stringBuilder().append("  [afade").append(builders.size() - 2).append("][").append(i + 1).append(":a]acrossfade=duration=").append(fadeLength).append("[a]\\").append("\n");
                } else {
                    stringBuilder().append("  [afade").append(i).append("][").append(i + 1).append(":a]acrossfade=duration=").append(fadeLength).append("[afade").append(i + 1).append("];\\").append("\n");
                }
            }
            stringBuilder().append("  \"\\").append("\n");
            stringBuilder().append("  -vsync vfr -acodec aac -b:a 192k -ac 2 -ar 44100 -map \"[a]\" -y -b:a 192k -ar 44100 -ac 2 ").append(config.ffmpeg().threads()).append(" ").append(aacName).append("\n");
        } else {
            // only one input, no filter_complex
            stringBuilder().append("  -vsync vfr -acodec aac -b:a 192k -ac 2 -ar 44100 -y -b:a 192k -ar 44100 -ac 2 ").append(config.ffmpeg().threads()).append(" ").append(aacName).append("\n");
        }

        stringBuilder().append("end=$(date)").append("\n");
        stringBuilder().append("echo \"Encoding took time from $start to $end\"").append("\n");
    }
}
