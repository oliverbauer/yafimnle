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

class ExtendSuperzoomTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2160x1620_4to3.jpg",
            //"6000x4000_3to2.nef",
            "5184x3888_4to3.JPG",
            "1920x1080_16to9.jpg"
    })
    void magick(String source) {
        File i = TestResource.file(source);
        File o = new File(DESTINATION_DIR + source + "-" + getClass().getSimpleName() + "-superzoom.jpg");

        ExtendSuperzoom extendSuperzoom = new ExtendSuperzoom();
        CLI.exec(extendSuperzoom.command(i, o), this);

        // then: Image has been created in correct dimension
        assertTrue(o.exists());
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(o));
    }
}
