package io.github.yafimnle.ffmpeg.filtercomplex;

import io.github.yafimnle.ffmpeg.filtercomplex.filter.FilterChainEntry;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class FilterComplex {

    List<FilterChainEntry> outlineEntries;
    private boolean isVideo = false;

    public FilterComplex() {
        outlineEntries = new ArrayList<>();
    }

    public static FilterComplex of(boolean isVideo, List<FilterChainEntry> filterChainEntry) {
        var filterComplex = new FilterComplex();
        filterComplex.isVideo = isVideo;
        filterComplex.outlineEntries.addAll(filterChainEntry);
        return filterComplex;
    }

    public static FilterComplex of(boolean isVideo, FilterChainEntry... filterChainEntry) {
        var filterComplex = new FilterComplex();
        filterComplex.isVideo = isVideo;
        filterComplex.outlineEntries.addAll(Arrays.asList(filterChainEntry));
        return filterComplex;
    }

    public String getFilterComplex() {
        int audioIndex = 0;
        if (!isVideo) {
            audioIndex = 1;
        }

        if (outlineEntries.size() == 1) {
            var sb = new StringBuilder();
            sb.append("  [0:v]").append(outlineEntries.getFirst().getEntry()).append("[v];");
            sb.append("  ["+audioIndex+":a]loudnorm,afade=type=in:duration=1:start_time=0[a]"); // TODO normize?

            return sb.toString();
        } else {
            var sb = new StringBuilder();

            sb.append("  [0:v]\\");
            sb.append("\n");
            sb.append("  ");
            sb.append(outlineEntries.getFirst().getEntry());
            sb.append("\\");
            sb.append("\n");
            sb.append("  [v1];\\");
            sb.append("\n");
            sb.append("  [v1]split=2[v1a][v1b];");
            sb.append("\n");
            for (int i=1; i<=outlineEntries.size()-1; i++) {
                sb.append("  [v"+ i+"a]\\");
                sb.append("\n");

                sb.append("  ");
                sb.append(outlineEntries.get(i).getEntry()).append("\\");

                if (i!=outlineEntries.size()-1) {
                    sb.append("\n");
                    sb.append("  [v" + (i) + "aDone];\\");
                    sb.append("\n");


                    sb.append("  [v"+ + (i)+"b][v"+ i+"aDone]overlay=0:0[v"+(i+1)+"];");
                    sb.append("\\");
                    sb.append("\n");
                    sb.append("  [v"+(i+1)+"]split=2[v"+(i+1)+"a][v"+(i+1)+"b];");
                    sb.append("\n");

                } else if (i == outlineEntries.size() -1) {
                    sb.append("\n");
                    sb.append("  [v" + (i) + "aDone];\\");
                    sb.append("\n");
                    sb.append("  [v"+i+"b][v"+i+"aDone]overlay=0:0");
                }
            }

            sb.append("\n");
            sb.append("[v];");
            sb.append("["+audioIndex+":a]loudnorm,afade=type=in:duration=1:start_time=0[a]"); // TODO normalize?
            sb.append("\n");
            return sb.toString();
        }
    }
}
