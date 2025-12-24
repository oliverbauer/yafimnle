package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.common.Builder;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FFMpegScriptVideo extends FFMpegScript {

    public void fade(List<Builder> builders) {
        List<Integer> vidLengthList = new ArrayList<>();
        List<Integer> vidEndList = new ArrayList<>();

        var ende = 0;
        var prevEnde = 0;
        for (int i = 0; i <= builders.size() - 1; i++) {
            var builder = builders.get(i);
            var length = builder.encodedLength();

            vidLengthList.add(length);

            if (i == 0) {
                ende = vidLengthList.get(i);
                prevEnde = 0;
                log.debug("Builder {} ends at {} since first video", i, ende);
            } else {
                int fadeLength = builders.get(i - 1).fadeLength();
                ende = ende + (vidLengthList.get(i) - fadeLength);
                prevEnde = prevEnde - fadeLength;
                log.debug("Builder {} ends at {} since 'fade of previous is {}', so fade-in starts at '{} - {}' and length of video is {}", i, ende, fadeLength, vidEndList.get(i-1), fadeLength, length);
            }
            vidEndList.add(ende);

        }

        if (builders.size() > 1) {
            for (int i = 0; i <= builders.size() - 2; i++) {
                var fadeType = builders.get(i).fadeType().param();
                var fadeLength = builders.get(i).fadeLength();
                var offset = vidEndList.get(i) - fadeLength;

                if (i == 0) {
                    stringBuilder().append("  [0:v][1:v]xfade=transition=" + fadeType + ":duration=" + fadeLength + ":offset=" + offset + "[vfade1];\\").append("\n");
                } else if (i == (builders.size() - 2)) {
                   stringBuilder().append("  [vfade" + (builders.size() - 2) + "][" + (builders.size() - 1) + ":v]xfade=transition=" + fadeType + ":duration=" + fadeLength + ":offset=" + offset + "[v]\"\\").append("\n");
                } else {
                   stringBuilder().append("  [vfade" + i + "][" + (i + 1) + ":v]xfade=transition=" + fadeType + ":duration=" + fadeLength + ":offset=" + offset + "[vfade" + (i + 1) + "];\\").append("\n");
                }
            }
        }
    }
}
