package io.github.yafimnle.image.filter;

import io.github.yafimnle.image.transformation.LeftToRight;
import io.github.yafimnle.image.transformation.None;
import io.github.yafimnle.image.transformation.ZoomIn;
import io.github.yafimnle.image.transformation.ZoomOut;

public class Transformations {
    private Transformations() {
        // Utility class
    }
    public static None none() {
        return new None();
    }

    public static LeftToRight leftToRight() {
        return new LeftToRight();
    }

    public static ZoomIn zoomin() {
        return new ZoomIn();
    }

    public static ZoomOut zoomout() {
        return new ZoomOut();
    }
}
