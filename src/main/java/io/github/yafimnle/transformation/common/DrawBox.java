package io.github.yafimnle.transformation.common;

import lombok.Builder;

@Builder
public class DrawBox extends OutlineEntry {
    /**
     * Default: ih-192
     * TODO should align with height of box.. maybe create some verical (CENTER,NORTH,SOUTH) or reuse Gravity?
     */
    @Builder.Default
    String y = "ih - 162"; // "ih/2 - 81"; // since height is 162... or "0" for North

    /**
     * Default 30
     */
    @Builder.Default
    String x = "0";

    /**
     * Default: black@0.4
     */
    @Builder.Default
    String color = "black@0.8";

    /**
     * Default: iw
     */
    @Builder.Default
    String width = "iw";

    /**
     * Default: 162
     * TODO should align with y-position of box
     */
    @Builder.Default
    String height = "162";

    /**
     * Default: fill
     */
    @Builder.Default
    String t = "fill";

    /**
     * Optional.
     */
    FadeIn fadeIn;
    /**
     * Optional.
     */
    FadeOut fadeOut;

    public String toString() {
        var reequired = "drawbox=y="+y+":x="+x+":color="+color+":width="+width+":height="+height+":t="+t;
        if (fadeIn != null && fadeOut != null) {
            return reequired+","+fadeIn+","+fadeOut;
        } else if (fadeIn != null) {
            return reequired+","+fadeIn;
        } else if (fadeOut != null) {
            return reequired+","+fadeOut;
        }
        return reequired;
    }
}
