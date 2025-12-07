package io.github.yafimnle.common;

public enum FadeType {
    // https://ottverse.com/crossfade-between-videos-ffmpeg-xfade-filter/
    FADE("fade"), // Default
    CIRCLEOPEN("circleopen"),
    HBLUR("hblur"),
    DISSOLVE("dissolve"),
    RADIAL("radial"),
    PIXELIZE("pixelize"),
    HLSLICE("hlslice"),
    HRSLICE("hrslice"),
    VUSLICE("vuslice"),
    VDSLICE("vdslice"),
    FADEGRAYS("fadegrays"),
    FADEBLACK("fadeblack"),
    FADEWHITE("fadewhite"),
    RECTCROP("rectcrop"),
    CIRCLECROP("circlecrop"),
    WIPELEFT("wipeleft"),
    WIPERIGHT("wiperight"),
    SLIDEDOWN("slidedown"),
    SLIDEUP("slideup"),
    DISTANCE("distance");

    private String param;

    FadeType(String parameter) {
        this.param = parameter;
    }

    public String param() {
        return param;
    }
}
