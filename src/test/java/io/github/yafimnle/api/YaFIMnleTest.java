package io.github.yafimnle.api;

import io.github.yafimnle.YaFIMnle;
import io.github.yafimnle.common.FadeType;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.image.filter.Transformations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class YaFIMnleTest {
    @BeforeEach
    void freshConfig() {
        Config
                .freshInstance(useHardwareAcceleration)
                .sourceDir(new File(getClass().getClassLoader().getResource("2160x1620_4to3.jpg").getFile()).getParentFile().getAbsolutePath())
                .resolution(Resolution.LOW_QUALITY);
    }
    @Test
    void fadetypeFade() {
        // given
        FadeType type = FadeType.FADE;

        var cut = new YaFIMnle(
                "fadetype_" + type
        );

        // when
        cut.of(
                YaFIMnle.img("2160x1620_4to3-2.jpg").as("1").fadeType(type),
                YaFIMnle.img("2160x1620_4to3.jpg").as("2").fadeType(type),
                YaFIMnle.img("2160x1620_4to3-2.jpg").as("3")
        ).create();

        // then
    }

    @Test
    void testDefaultFadeLength() {
        // given
        var cut = new YaFIMnle(
                "testDefaultFadeLength"
        );

        // when
        var actual = cut.of(
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(1),
                YaFIMnle.img("2160x1620_4to3-2.jpg")
                        .transform(Transformations.none())
                        .fadeLength(1),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
        ).create();

        /*
         * Explain:
         * 3 Images of (default) 5 seconds would be 15 seconds. Since there are two fades of 1 seconds (default but set explicitly) result is 13 seconds
         */

        // then
        assertEquals("13.000000", FFProbe.instance().length(actual));
    }

    @Test
    void testFadeLength() {
        // given
        var cut = new YaFIMnle(
                "testFadeLength"
        );

        // when
        var actual = cut.of(
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(2),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(2),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
        ).create();

        /*
         * Explain:
         * 3 Images of (default) 5 seconds would be 15 seconds. Since there are two fades, two of 2 seconds
         *
         * 0  1  2  3  4  5  6  7  8  9  10 11
         * ----------------
         * | 1| 2| 3| 4| 5|
         * -------------------------
         *          | 1| 2| 3| 4| 5|
         *          -------------------------
         *                   | 1| 2| 3| 4| 5|
         *                   ----------------
         */

        // then
        assertEquals("11.000000", FFProbe.instance().length(actual));
    }

    @Test
    void testFadeLength2() {
        // given
        var cut = new YaFIMnle(
                "testFadeLength2"
        );

        // when
        var actual = cut.of(
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(2),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(1),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
        ).create();

        /*
         * Explain:
         * 3 Images of (default) 5 seconds would be 15 seconds. Since there are two fades, two of 2 seconds
         *
         * 0  1  2  3  4  5  6  7  8  9  10 11 12
         * ----------------
         * | 1| 2| 3| 4| 5|
         * -------------------------
         *          | 1| 2| 3| 4| 5|
         *          -------------------------
         *                      | 1| 2| 3| 4| 5|
         *                      ----------------
         */

        // then
        assertEquals("12.000000", FFProbe.instance().length(actual));
    }

    @Test
    void testFadeLength3() {
        // given
        var cut = new YaFIMnle(
                "testFadeLength3"
        );

        // when
        var actual = cut.of(
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .seconds(10)
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(2)
                        .as("1"),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(1)
                        .as("2"),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.none())
                        .as("3")
        ).create();

        /*
         * Explain:
         * 3 Images of (default) 5 seconds would be 15 seconds. Since there are two fades, two of 2 seconds
         *
         * 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17
         * -------------------------------        .            .
         * | 1| 2| 3| 4| 5| 6| 7| 8| 9|10|        .            .
         * -------------------------------        .            .
         *                         | 1| 2| 3| 4| 5|            .
         *                          ---------------            .
         *                                      | 1| 2| 3| 4| 5|
         *                                      ----------------
         * Note: Since first and third image are "equal" (source) we need "as"-feature
         */

        // then
        assertEquals("17.000000", FFProbe.instance().length(actual));
    }

    @Test
    void testFadeLength4() {
        // given
        var scriptname = "2160x1620_4to3-2-jpg-"+ getClass().getSimpleName()+"-testFadeLength4";
        var cut = new YaFIMnle(
                scriptname
        );

        // when
        var actual = cut.of(
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.zoomout())
                        .seconds(10)
                        .fadeType(FadeType.WIPERIGHT)
                        .fadeLength(5)
                        .as(scriptname+"-1"),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .transform(Transformations.zoomin())
                        .seconds(10)
                        .fadeType(FadeType.WIPELEFT)
                        .fadeLength(5)
                        .as(scriptname+"-2"),
                YaFIMnle.img("2160x1620_4to3.jpg")
                        .seconds(10)
                        .transform(Transformations.zoomout())
                        .fadeType(FadeType.SLIDEDOWN)
                        .fadeLength(5)
                        .as(scriptname+"-3"),
                YaFIMnle.img("2160x1620_4to3-2.jpg")
                        .transform(Transformations.zoomin())
                        .seconds(10)
                        .as(scriptname+"-4")
        ).create();

        /*
         * Explain:
         * 4 Images of 10 seconds would be 40 seconds. Since each fade is 5 seconds result is 25 seconds (40 - 3*5).
         *
         * 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
         * -------------------------------              .              .              .
         * | 1| 2| 3| 4| 5| 6| 7| 8| 9|10|              .              .              .
         * -------------------------------              .              .              .
         *                | 1| 2| 3| 4| 5| 6| 7| 8| 9|10|              .              .
         *                 -----------------------------               .              .
         *                               | 1| 2| 3| 4| 5| 6| 7| 8| 9|10|              .
         *                               -------------------------------              .
         *                                              | 1| 2| 3| 4| 5| 6| 7| 8| 9|10|
         *                                              -------------------------------
         */

        // then
        assertEquals("25.000000", FFProbe.instance().length(actual));
    }
}
