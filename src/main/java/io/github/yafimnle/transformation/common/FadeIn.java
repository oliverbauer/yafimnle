package io.github.yafimnle.transformation.common;

import lombok.Builder;

@Builder
public class FadeIn {
    // fade=t=in:st=4:d=1:alpha=1
    @Builder.Default
    int position = 1;
    @Builder.Default
    int duration = 1;

    @Override
    public String toString() {
        return "fade=t=in:st="+position+":d="+duration+":alpha=1";
    }
}
