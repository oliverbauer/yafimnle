package io.github.yafimnle.image.enums;

public enum Gravity {
    NORTH_WEST("NorthWest"),
    NORTH("North"),
    NORTH_EAST("NorthEast"),
    WEST("West"),
    CENTER("Center"),
    EAST("East"),
    SOUTH_WEST("SouthWest"),
    SOUTH("South"),
    SOUTH_EAST("SouthEast");

    String name;

    Gravity(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
