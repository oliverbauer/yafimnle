package io.github.yafimnle.config;

import io.github.yafimnle.transformation.Transformation;

public class TransformConfig {
    private static TransformConfig instance;

    private Transformation videoTransformation;
    private Transformation imageTransformation;

    // Konstructor
    private TransformConfig() {
        // private
    }

    public static TransformConfig transformConfig() {
        if (instance == null) {
            instance = new TransformConfig();
        }
        return instance;
    }

    public Transformation overrideVideoTransformation() {
        return instance.videoTransformation;
    }

    public TransformConfig overrideVideoTransformation(Transformation videoTransformation) {
        instance.videoTransformation = videoTransformation;
        return instance;
    }

    public Transformation overrideImageTransformation() {
        return instance.imageTransformation;
    }

    public TransformConfig overrideImageTransformation(Transformation imageTransformation) {
        instance.imageTransformation = imageTransformation;
        return instance;
    }
}
