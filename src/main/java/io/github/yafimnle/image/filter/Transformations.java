package io.github.yafimnle.image.filter;

import io.github.yafimnle.transformation.image.LeftToRight;
import io.github.yafimnle.transformation.image.None;
import io.github.yafimnle.transformation.image.ZoomIn;
import io.github.yafimnle.transformation.image.ZoomOut;

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
