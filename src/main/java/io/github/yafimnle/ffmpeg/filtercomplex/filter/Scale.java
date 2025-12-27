package io.github.yafimnle.ffmpeg.filtercomplex.filter;

import io.github.yafimnle.config.Config;

public class Scale extends FilterChainEntry {
    @Override
    public String getEntry() {
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        return "scale="+ Config.instance().resolution().dimension()+scaleFlags;
    }
}
