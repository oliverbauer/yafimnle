package io.github.yafimnle.video.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.transformation.common.OutlineEntry;

public class Scale extends OutlineEntry {
    @Override
    public String toString() {
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        return "scale="+ Config.instance().resolution().dimension()+scaleFlags;
    }
}
