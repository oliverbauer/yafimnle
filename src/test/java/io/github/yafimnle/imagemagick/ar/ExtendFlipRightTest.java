package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendFlipRightTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration);
    }

    @ParameterizedTest
    @ValueSource(strings = {
//            "2160x1620_4to3.jpg",
//            "5184x3888_4to3.JPG",
            "1920x1080_16to9.jpg"
    })
    void magick(String source) {
        // given
        File i = TestResource.file(source);
        File o = new File(DESTINATION_DIR + source + "-" + getClass().getSimpleName() +  "-flip-right.jpg");
        Resolution given = FFProbe.instance().resolution(i);

        // when
        CLI.exec(
                new ExtendFlipRight(20).command(i, o), this
        );
        Resolution actual = FFProbe.instance().resolution(o);

        // then: Image has been created in correct dimension
        assertTrue(o.exists());
        int newWidth = (actual.height()*16/9);
        Resolution expectedResolution = Resolution.from(newWidth, actual.height());
        assertEquals(given.height(), actual.height()); // TODO automatically resizes "2160x1620_4to3.jpg"... this should be optional see Crop
        assertEquals(newWidth, actual.width());
        assertEquals("16:9", expectedResolution.ar());
    }
}
