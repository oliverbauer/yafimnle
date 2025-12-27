package io.github.yafimnle.imagemagick;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.imagemagick.ar.AR;
import io.github.yafimnle.imagemagick.filter.ImageFilters;
import io.github.yafimnle.ffmpeg.filtercomplex.FilterComplex;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.DrawBox;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.DrawText;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.FadeIn;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.FadeOut;
import io.github.yafimnle.ffmpeg.filtercomplex.filter.ZoomPan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageBuilderTest {
    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration);
    }

    @Test
    void default_encoding_from_5188x3888_to_1920x1080_with_cutting() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");

        // when
        var actual = new ImageBuilder(i).as("default_encoding_from_5188x3888_to_1920x1080_with_cutting").create();

        // then: Video has been created in correct dimension
        assertEquals(Resolution.from(1920,1080), FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void default_encoding_from_5188x3888_to_1920x1080_with_cutting_NEU() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");
        int seconds = 10;

        FilterComplex filterComplex = FilterComplex.of(
                false,
                ZoomPan.builder()
                        .duration(25*seconds)
                        .y("ih/2-(ih/zoom/2)") // "ih/2-(ih/zoom/2)"
                        .z("'zoom+0.001'") // 0.001 is default 0.01 is faster
                        .build(),
                DrawBox.builder()
                        .color("black@0.9") // black@0.4    red@0.1
                        .x("30")
                        .y("ih-192")
                        .height("162")
                        .width("iw-60")
                        .fadeIn(FadeIn.builder().position(0).duration(2).build())
                        .fadeOut(FadeOut.builder().position(8).duration(2).build())
                        .build(),
                DrawText.builder()
                        .text("Ein\\ wunderschoenes\\ Bild\\ mit\\ Ken-Burns-Effekt")
                        .fontcolor("white")
                        .fontsize("80")
                        .x("30")
                        .y("h - 140")
                        .fadeIn(FadeIn.builder().position(0).duration(2).build())
                        .fadeOut(FadeOut.builder().position(8).duration(2).build())
                        .build()
        );

        // when
        var actual = new ImageBuilder(i)
                .seconds(seconds)
                .filterCompex(filterComplex)
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
        assertEquals("10.000000", FFProbe.instance().length(actual));
    }

    @Test
    void default_encoding_from_5188x3888_to_1920x1080_with_cutting_with_outline() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");

        // when
        var actual = new ImageBuilder(i)
                .as("default_encoding_from_5188x3888_to_1920x1080_with_cutting_with_outline")
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void lowQuality_encoding_from_5188x3888_to_960x540_with_cutting() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");
        Config.instance().resolution(Resolution.LOW_QUALITY);

        // when
        var actual = new ImageBuilder(i)
                .as("lowQuality_encoding_from_5188x3888_to_960x540_with_cutting")
                .create();

        // then
        assertEquals(Resolution.LOW_QUALITY, FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void highQuality_encoding_from_5188x3888_to_3840x2160_with_cutting() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");
        Config.instance().resolution(Resolution.ULTRA_HD);

        // when
        var actual = new ImageBuilder(i)
                .as("highQuality_encoding_from_5188x3888_to_3840x2160_with_cutting")
                .create();

        // then
        assertEquals(Resolution.ULTRA_HD, FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void enlargeBlur() {
        // given
        File i = TestResource.file("2160x1620_4to3.jpg");

        // when
        var actual = new ImageBuilder(i)
                .ar(AR.blur(20))
                .as("enlargeBlur")
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void fromNEF() {
        // given
        File i = TestResource.file("6000x4000_3to2.nef");

        // when
        var actual = new ImageBuilder(i)
                .as("fromNEF")
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
    }

    @Test
    void default_encoding_from_5188x3888_to_1920x1080_with_cutting_and_preprocessing_scale_filter() {
        // given
        File i = TestResource.file("2160x1620_4to3-2.jpg");

        // when
        var actual = new ImageBuilder(i)
                //.scalePreprocessingFilter(ScalePreprocessingFilters.partialBlur(1085, 1185, 65, 90, 10))
                //.scalePreprocessingFilter(ScalePreprocessingFilters.implode(1085, 1185, 65, 90, 1.5))
                //.appendScalePreprocessingFilter(ScalePreprocessingFilters.paint(1085, 1185, 65, 90, 4))
                //.appendScalePreprocessingFilter(ScalePreprocessingFilters.paint(1910, 1185, 40, 40, 4))
                .appendImageFilterBeforeCrop(ImageFilters.partialBlur(1085, 1185, 65, 90, 10))
                .appendImageFilterBeforeCrop(ImageFilters.partialBlur(1910, 1185, 40, 40, 10))
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
        assertEquals("5.000000", FFProbe.instance().length(actual));
    }

    @Test
    void from_portrait() {
        // given
        File i = TestResource.file("1620x2160-portrait.jpg");

        // when
        var actual = new ImageBuilder(i)
                .appendImageFilterBeforeCrop(ImageFilters.extend())
                .as("from_portrait")
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
    }

    @Test
    void rotate_then_from_portrait() {
        // given
        File i = TestResource.file("2160x1620-rotated.jpg");

        // when
        var actual = new ImageBuilder(i)
                .appendImageFilterBeforeCrop(ImageFilters.rotate(90))
                .appendImageFilterBeforeCrop(ImageFilters.extend()) // "fromPortrait"
                .as("rotate_then_from_portrait")
                .create();

        // then
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(actual));
    }
}
