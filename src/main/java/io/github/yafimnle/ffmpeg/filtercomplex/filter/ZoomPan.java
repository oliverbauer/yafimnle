package io.github.yafimnle.ffmpeg.filtercomplex.filter;

import io.github.yafimnle.config.Config;
import lombok.Builder;

@Builder
public class ZoomPan extends FilterChainEntry {
    public static ZoomPan zoomIn() {
        return ZoomPan.builder().build();
    }
    public static ZoomPan zoomOut() {
        return ZoomPan.builder().z("'if(lte(zoom,1.0),1.2,max(1.001,zoom-0.001))'").build();
    }

    /**
     * Scale, use something like "8000:-1" (default).
     */
    @Builder.Default
    String scale = "8000:-1";

    /**
     * Default: 'zoom+0.001' "speed"
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#zoompan">https://ffmpeg.org/ffmpeg-filters.html#zoompan</a>
     */
    @Builder.Default
    String z="'zoom+0.001'";

    /**
     * Defaults to center: iw/2-(iw/zoom/2)
     */
    @Builder.Default
    String x = "iw/2-(iw/zoom/2)";

    /**
     * Defaults to center: ih/2-(ih/zoom/2)
     */
    @Builder.Default
    String y = "ih/2-(ih/zoom/2)";

    /**
     * Duration in frames.
     * @see <a href="https://ffmpeg.org/ffmpeg-filters.html#zoompan">https://ffmpeg.org/ffmpeg-filters.html#zoompan</a>
     */
    @Builder.Default
    int duration = 250;

    @Override
    public String getEntry() {
        String scaleFlags = Config.instance().ffmpeg().scaleFlags();
        return "scale="+scale+scaleFlags+",zoompan=z="+z+":s="+ Config.instance().resolution().dimension() +":x="+x+":y="+y+":d="+ duration;
    }
}