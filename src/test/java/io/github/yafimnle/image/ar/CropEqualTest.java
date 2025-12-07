package io.github.yafimnle.image.ar;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.image.enums.Gravity;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CropEqualTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1920x1080_16to9.jpg",
            "2160x1620_4to3.jpg"
//            "5184x2920_16to9.JPG",
//            "5184x3888_4to3.JPG"
    })
    void magick(String source) {
        File i = TestResource.file(source);

        for (Gravity g : List.of(Gravity.CENTER, Gravity.NORTH, Gravity.SOUTH)) {
            File o = new File(DESTINATION_DIR + source + "-" + getClass().getSimpleName() + "-crop-"+g+".jpg");

            CLI.exec(
                    new Crop(g).command(i, o), this
            );

            // then: Image has been created in correct dimension
            assertTrue(o.exists());
            //assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(o));
        }


        // TODO 2025.11.28 -> Hier zeigt sich ein Qualitätsverlust wenn man in CropEqual auch ein "-resize " hat (das Bild wird schon auf FullHD skaliert)
        //                   Wenn man das nicht macht, muss im ImageBuilder der SanityCheck raus + intermediateImage = preprocessed; gesetzt werden
/*
        Transformation zoom = OutlineV2.of(
                ZoomPan.builder()
                        .duration(25*Config.instance().ffmpeg().imgToVidSeconds())
                        .y("ih/2-(ih/zoom/2)") // "ih/2-(ih/zoom/2)"
                        .z("'zoom+0.001'")
                        .size("1920x1080")
                        .build());
        img2(o)
                .disableIntermediateSanityCheck()
                .transform(zoom)
                .create();
 */



    }
}
