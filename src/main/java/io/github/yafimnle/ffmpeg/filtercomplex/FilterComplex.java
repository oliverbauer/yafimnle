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
        FilterComplex filterComplex = new FilterComplex();
        filterComplex.isVideo = isVideo;
        filterComplex.outlineEntries.addAll(filterChainEntry);
        return filterComplex;
    }

    public static FilterComplex of(boolean isVideo, FilterChainEntry... filterChainEntry) {
        FilterComplex filterComplex = new FilterComplex();
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
            StringBuilder s = new StringBuilder();
            s.append("  [0:v]").append(outlineEntries.getFirst().getEntry()).append("[v];");
            s.append("  ["+audioIndex+":a]loudnorm,afade=type=in:duration=1:start_time=0[a]"); // TODO normize?

            return s.toString();
        } else {
            StringBuilder s = new StringBuilder();

            s.append("  [0:v]\\");
            s.append("\n");
            s.append("  ");
            s.append(outlineEntries.getFirst().getEntry());
            s.append("\\");
            s.append("\n");
            s.append("  [v1];\\");
            s.append("\n");
            s.append("  [v1]split=2[v1a][v1b];");
            s.append("\n");
            for (int i=1; i<=outlineEntries.size()-1; i++) {
                s.append("  [v"+ i+"a]\\");
                s.append("\n");

                s.append("  ");
                s.append(outlineEntries.get(i).getEntry()).append("\\");

                if (i!=outlineEntries.size()-1) {
                    s.append("\n");
                    s.append("  [v" + (i) + "aDone];\\");
                    s.append("\n");


                    s.append("  [v"+ + (i)+"b][v"+ i+"aDone]overlay=0:0[v"+(i+1)+"];");
                    s.append("\\");
                    s.append("\n");
                    s.append("  [v"+(i+1)+"]split=2[v"+(i+1)+"a][v"+(i+1)+"b];");
                    s.append("\n");

                } else if (i == outlineEntries.size() -1) {
                    s.append("\n");
                    s.append("  [v" + (i) + "aDone];\\");
                    s.append("\n");
                    s.append("  [v"+i+"b][v"+i+"aDone]overlay=0:0");
                }
            }

            s.append("\n");
            s.append("[v];");
            s.append("["+audioIndex+":a]loudnorm,afade=type=in:duration=1:start_time=0[a]"); // TODO normalize?
            s.append("\n");
            return s.toString();
        }
    }
}
