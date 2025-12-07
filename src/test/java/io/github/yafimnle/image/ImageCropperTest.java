package io.github.yafimnle.image;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.ar.AbstractAROptions;
import io.github.yafimnle.image.enums.Gravity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageCropperTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance()
                .destinationDir("/tmp/");
    }

    @ParameterizedTest
    @EnumSource(value = Resolution.class, names = { "FULL_HD", "LOW_QUALITY" })
    void input_16to9_1920x1080_to_different_quality(Resolution resolution) {
        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg" + "-" + getClass().getSimpleName() + "-" + resolution.apprev() + ".jpg");

        Config.instance().resolution(resolution);

        // when
        new ImageCropper().crop(
                i,
                o,
                AR.crop(Gravity.CENTER)
        );
        Resolution actual = FFProbe.instance().resolution(o);

        // then nothing has been cropped: Input is 16:9 resolution is always 16:9
        assertTrue(o.exists());
        assertEquals(1920, actual.width());
        assertEquals(1080, actual.height());
        assertEquals("16:9", actual.ar());
    }

    @ParameterizedTest
    @EnumSource(value = Resolution.class, names = { "FULL_HD", "LOW_QUALITY" })
    void input_4to3_5184x3888_to_different_quality(Resolution resolution) {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");
        File o = new File(DESTINATION_DIR + "5184x3888_4to3.JPG" + "-" + getClass().getSimpleName() + "-" + resolution.apprev() + ".jpg");

        Config.instance().resolution(resolution);

        // when
        new ImageCropper().crop(
                i,
                o,
                AR.crop(Gravity.CENTER)
        );
        Resolution actual = FFProbe.instance().resolution(o);

        // then independently of resolution (always 16:9) crop is unique
        assertTrue(o.exists());
        assertEquals(5184, actual.width());
        assertEquals(2916, actual.height());
        assertEquals("16:9", actual.ar());
    }

    @ParameterizedTest
    @MethodSource("generatorAR")
    void input_4to3_2160x1620_to_different_aspect_ratio_style(AbstractAROptions ar) {
        Resolution resolution = Resolution.FULL_HD;
        Config.instance().resolution(resolution);

        File i = TestResource.file("2160x1620_4to3.jpg");
        File o = new File(DESTINATION_DIR + "2160x1620_4to3.jpg" + "-" +getClass().getSimpleName() + "-" + resolution.apprev() + "-" + ar.getClass().getSimpleName() + ".jpg");

        // when
        new ImageCropper().crop(
                i,
                o,
                ar
        );

        // then: File has been copied and right dimension
        assertTrue(o.exists());
        //assertEquals(resolution, FFProbe.instance().resolution(o)); // TODO Not full hd but 16:9...
        /*
         * Testen:
         * convert blur-50.jpg -distort ScaleRotateTranslate 1.5,10  -vignette 0x200 -quality 100% crop-equal-scaleRotateTranslate.jpg
         * convert blur-50.jpg -vignette 0x200 -quality 100% crop-equal-scaleRotateTranslate.jpg
         *
         * https://besly.de/index.php/painting/imagemagick/imagemagick-tips-and-tricks
         *
         *
         *
         * https://legacy.imagemagick.org/Usage/photos/ tODO
         */

    }

    private static Stream<Arguments> generatorAR() {
        return Stream.of(
                Arguments.of(AR.crop(Gravity.CENTER)),
                Arguments.of(AR.crop(Gravity.NORTH)),
                Arguments.of(AR.crop(Gravity.SOUTH)),
                Arguments.of(AR.appendFlipped(20)),
                Arguments.of(AR.appendFlippedLeft(20)),
                Arguments.of(AR.appendFlippedRight(20)),
                Arguments.of(AR.enlargeByColor()),
                Arguments.of(AR.blur(10)),
                Arguments.of(AR.superzoom())
         );
    }
}
