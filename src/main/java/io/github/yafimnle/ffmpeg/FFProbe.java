package io.github.yafimnle.ffmpeg;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.utils.CLI;
import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
public class FFProbe {
    private static FFProbe instance;

    Cache<String, Resolution> resolutionCache;

    private FFProbe() {
        resolutionCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .build();
    }

    public static FFProbe instance() {
        if (instance == null)
            instance = new FFProbe();
        return instance;
    }

    public Resolution resolution(File file) {
        var resolution = resolutionCache.getIfPresent(file.getAbsolutePath());
        if (resolution != null) {
            log.debug("Cache hit for resolution of {}", file.getAbsoluteFile());
            return resolution;
        }

        var fileWithExcapedWhitespaces = file.toString();
        fileWithExcapedWhitespaces = fileWithExcapedWhitespaces.replaceAll("\\s", "\\\\ ");

        var dimension =  CLI.exec("ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=p=0:s=x " + fileWithExcapedWhitespaces, this);

        if (dimension.contains("\n")) {
            dimension = dimension.split("\n")[0]; // TODO Workaround NationalparkKrka
        }

        var w = Integer.parseInt(dimension.substring(0, dimension.indexOf("x")));
        var h = Integer.parseInt(dimension.substring(dimension.indexOf("x") + 1));

        resolution = Resolution.from(w,h);
        log.debug("Put to resolution cache: {} -> {}", file.getAbsolutePath(), resolution);
        resolutionCache.put(file.getAbsolutePath(), resolution);

        return resolution;
    }

    public String length(File file) {
        var fileWithExcapedWhitespaces = file.toString();
        fileWithExcapedWhitespaces = fileWithExcapedWhitespaces.replaceAll("\\s", "\\\\ ");
        return CLI.exec("ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 " + fileWithExcapedWhitespaces, this);
    }

    public int seconds(File file) {
        var exactLength = length(file);
        return Integer.parseInt(exactLength.substring(0, exactLength.indexOf(".")));
    }
}
