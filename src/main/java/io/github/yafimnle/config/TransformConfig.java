package io.github.yafimnle.config;

import io.github.yafimnle.ffmpeg.filtercomplex.FilterComplex;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransformConfig {
    @Builder.Default
    private FilterComplex videoTransformation = null;
    @Builder.Default
    private FilterComplex imageTransformation = null;
}
