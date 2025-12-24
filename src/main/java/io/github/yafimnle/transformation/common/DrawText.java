package io.github.yafimnle.transformation.common;

import lombok.Builder;

@Builder
public class DrawText extends OutlineEntry {
    @Builder.Default
    String text = "Yes";

    @Builder.Default
    String fontcolor = "white";

    @Builder.Default
    String fontsize = "80";

    // syntax for moving: "'(mod(round(t*30),w))'"
    @Builder.Default
    String x = "(w-text_w)/2"; // Center

    @Builder.Default
    String y = "h-text_h-81+(text_h/2)"; //(h-text_h)/2"; (Center) // 81 is boxheight/2

    FadeIn fadeIn;
    FadeOut fadeOut;

    public String toString() {
        var required = "drawtext=text=\""+text+"\":fontcolor="+fontcolor+":fontsize="+fontsize+":y="+y+":x="+x;
        if (fadeIn != null && fadeOut != null) {
            return required +","+fadeIn+","+fadeOut;
        } else if (fadeIn != null) {
            return required +","+fadeIn;
        } else if (fadeOut != null) {
            return required +","+fadeOut;
        }
        return required;
    }
}
