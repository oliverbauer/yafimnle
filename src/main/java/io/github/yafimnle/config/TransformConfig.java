package io.github.yafimnle.config;

import io.github.yafimnle.transformation.Transformation;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransformConfig {
    private Transformation videoTransformation;
    private Transformation imageTransformation;
}
