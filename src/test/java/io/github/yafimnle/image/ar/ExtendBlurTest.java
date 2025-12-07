package io.github.yafimnle.image.ar;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendBlurTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1920x1080_16to9.jpg",
            "2160x1620_4to3.jpg",
            "5184x3888_4to3.JPG",
    })
    void magick(String source) {
        // given
        File i = TestResource.file(source);
        File o = new File(DESTINATION_DIR + source + "-" + getClass().getSimpleName() +  "-extend-blur.jpg");
        Resolution given = FFProbe.instance().resolution(i);

        // when
        CLI.exec(
                new ExtendBlur().command(i, o), this
        );
        Resolution actual = FFProbe.instance().resolution(o);

        // then: Image has been created in correct dimension
        assertTrue(o.exists());
        int newWidth = (actual.height()*16/9);
        Resolution expectedResolution = Resolution.from(newWidth, actual.height());
        assertEquals(given.height(), actual.height());
        assertEquals(newWidth, actual.width());
        assertEquals("16:9", expectedResolution.ar());
    }
}
