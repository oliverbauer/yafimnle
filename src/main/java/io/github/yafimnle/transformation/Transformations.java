package io.github.yafimnle.transformation;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.exception.IllegalArgsException;
import io.github.yafimnle.transformation.common.DrawBox;
import io.github.yafimnle.transformation.common.DrawText;
import io.github.yafimnle.transformation.common.OutlineEntry;
import io.github.yafimnle.transformation.image.ZoomPan;
import io.github.yafimnle.transformation.video.Scale;

import java.util.ArrayList;
import java.util.List;

public class Transformations {

    private Transformations() {

    }

    public static OutlineV2 videoTransformationNone() {
        return OutlineV2.of(
                true,
                new Scale() // TODO configurable lancoz
        );
    }

    // TODO Rename to text2->header1, text2->header2, text->header3 same for others
    public static OutlineV2 videoTransformation(String text, String text1, String text2, boolean withBox) {
        int boxHeight = 192;

        int text1x = 20;
        int text1y = 974;
        int text1fontsize = 96;

        int text2x = 20;
        int text2y = 930;
        int text2fontsize = 24;

        int text3x = 20;
        int text3y = 900;
        int text3fontsize = 24;

        switch (Config.instance().resolution()) {
            case ULTRA_HD -> {
                boxHeight *= 2;
                text1fontsize *= 2;
                text2fontsize *= 2;
                text3fontsize *= 2;
                text1y *= 2;
                text2y *= 2;
                text3y *= 2;
            }
            case FULL_HD -> {
                // default, see above
            }
            case LOW_QUALITY -> {
                boxHeight /= 2;
                text1fontsize /= 2;
                text2fontsize /= 2;
                text3fontsize /= 2;
                text1y /= 2;
                text2y /= 2;
                text3y /= 2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + Config.instance().resolution());
        }

        String text1neu = escape(text);
        String text2neu = escape(text1);
        String text3neu = escape(text2);

        List<OutlineEntry> outlineEntryList = new ArrayList<>();
        outlineEntryList.add(new Scale());
        if (withBox) {
            outlineEntryList.add(DrawBox.builder()
                    .y("ih-"+boxHeight)
                    .color("black@0.4")
                    .width("iw")
                    .height(String.valueOf(boxHeight))
                    .build());
        }
        outlineEntryList.add(DrawText.builder()
                .text(text1neu)
                .x(String.valueOf(text1x))
                .y(String.valueOf(text1y))
                .fontsize(String.valueOf(text1fontsize))
                .fontcolor("white")
                .build());
        outlineEntryList.add(DrawText.builder()
                .text(text2neu)
                .x(String.valueOf(text2x))
                .y(String.valueOf(text2y))
                .fontsize(String.valueOf(text2fontsize))
                .fontcolor("white")
                .build());
        outlineEntryList.add(DrawText.builder()
                .text(text3neu)
                .x(String.valueOf(text3x))
                .y(String.valueOf(text3y))
                .fontsize(String.valueOf(text3fontsize))
                .fontcolor("white")
                .build());

        return OutlineV2.of(true, outlineEntryList);
    }

    // TODO Rename to text2->header1, text2->header2, text->header3 same for others
    public static OutlineV2 videoTransformation(String text, String text1, String text2) {
        return videoTransformation(text, text1, text2, true);
    }

    public static OutlineV2 zoomIn(String text, String text2, String text3) {
        return imageTransformation(text, text2, text3, "'zoom+0.001'");
    }

    public static OutlineV2 zoomIn(String text, String text2, String text3, boolean withBox) {
        return imageTransformation(text, text2, text3, "'zoom+0.001'", withBox);
    }

    public static OutlineV2 zoomOut(String text, String text2, String text3) {
        return imageTransformation(text, text2, text3, "'if(lte(zoom,1.0),1.2,max(1.001,zoom-0.001))'");
    }

    public static OutlineV2 zoomOut(String text, String text2, String text3, boolean withBox) {
        return imageTransformation(text, text2, text3, "'if(lte(zoom,1.0),1.2,max(1.001,zoom-0.001))'", withBox);
    }

    public static OutlineV2 imageTransformationNone() {
        return OutlineV2.of(
                false
        );
    }

    public static OutlineV2 imageTransformation(String text, String text1, String text2, String zoom, boolean withBox) {
        int seconds = Config.instance().ffmpeg().imgToVidSeconds();

        int boxHeight = 192;

        int text1x = 20;
        int text1y = 974;
        int text1fontsize = 96;

        int text2x = 20;
        int text2y = 930;
        int text2fontsize = 24;

        int text3x = 20;
        int text3y = 900;
        int text3fontsize = 24;

        switch (Config.instance().resolution()) {
            case ULTRA_HD -> {
                boxHeight *= 2;
                text1fontsize *= 2;
                text2fontsize *= 2;
                text3fontsize *= 2;
                text1y *= 2;
                text2y *= 2;
                text3y *= 2;
            }
            case FULL_HD -> {
                // default, see above
            }
            case LOW_QUALITY -> {
                boxHeight /= 2;
                text1fontsize /= 2;
                text2fontsize /= 2;
                text3fontsize /= 2;
                text1y /= 2;
                text2y /= 2;
                text3y /= 2;
            }
            default -> throw new IllegalStateException("Unexpected value: " + Config.instance().resolution());
        }

        String text1neu = escape(text);
        String text2neu = escape(text1);
        String text3neu = escape(text2);

        List<OutlineEntry> outlineEntryList = new ArrayList<>();
        outlineEntryList.add(ZoomPan.builder()
                .duration(Config.instance().ffmpeg().framerate() * seconds)
                .z(zoom)
                .build());
        if (withBox) {
            outlineEntryList.add(DrawBox.builder()
                    .y("ih-"+boxHeight)
                    .color("black@0.4")
                    .width("iw")
                    .height(String.valueOf(boxHeight))
                    .build());
        }
        outlineEntryList.add(DrawText.builder()
                .text(text1neu)
                .x(String.valueOf(text1x))
                .y(String.valueOf(text1y))
                .fontsize(String.valueOf(text1fontsize))
                .fontcolor("white")
                .build());
        outlineEntryList.add(DrawText.builder()
                .text(text2neu)
                .x(String.valueOf(text2x))
                .y(String.valueOf(text2y))
                .fontsize(String.valueOf(text2fontsize))
                .fontcolor("white")
                .build());
        outlineEntryList.add(DrawText.builder()
                .text(text3neu)
                .x(String.valueOf(text3x))
                .y(String.valueOf(text3y))
                .fontsize(String.valueOf(text3fontsize))
                .fontcolor("white")
                .build());

        return OutlineV2.of(
                false,
                outlineEntryList
        );
    }

    public static OutlineV2 imageTransformation(String text, String text1, String text2, String zoom) {
        return imageTransformation(text, text1, text2, zoom, true);
    }

    private static String escape(String text) {
        var newText = text
                .replace(" ", "\\ ")
                .replace("(", "\\(")
  //              .replace(",", "\\,")
                .replace(")", "\\)");

        if (newText.contains(",")) {
            throw new IllegalArgsException("Es sind keine Kommas im Text erlaubt!");
        }

        return newText;
    }
}
