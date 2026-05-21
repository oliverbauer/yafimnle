package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.MagickConfig;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendByColorTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration);
    }

    // input is already 16:9
    @Test
    void default_black_color_no_extend() {
        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg-" + getClass().getSimpleName() + "-extendbycolor.jpg");

        ExtendByColor extendByColor = new ExtendByColor();
        String command = extendByColor.command(i, o);

        String expected = MagickConfig.builder().build().command()+
                " -limit thread 1 "
                +i
                +" -resize x1080  -quality 100% -background Black -compose Copy -gravity Center -extent 1920x1080+0+0 "
                +o;
        assertEquals(expected, command);

        // Execute
        CLI.exec(command, this);
        assertTrue(o.exists());
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(o));
    }

    // input is 16:9 (1080p) will be resized to 2160p, no extension by color will be applied
    @Test
    void default_black_color_extend_to_4k() {
        // override target quality
        Config.freshInstance().resolution(Resolution.ULTRA_HD);

        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg-" + getClass().getSimpleName() + "-4k.jpg");

        ExtendByColor extendByColor = new ExtendByColor();
        String command = extendByColor.command(i, o);

        String expected = MagickConfig.builder().build().command()+
                " -limit thread 1 "
                +i
                +" -resize x2160  -quality 100% -background Black -compose Copy -gravity Center -extent 3840x2160+0+0 "
                +o;
        assertEquals(expected, command);

        // Execute
        CLI.exec(command, this);
        assertTrue(o.exists());
        assertEquals(Resolution.ULTRA_HD, FFProbe.instance().resolution(o));
    }

    // input 4:3 will be extended to 16:9 with black borders
    @Test
    void default_black_color_with_extend() {
        File i = TestResource.file("2160x1620_4to3.jpg");
        File o = new File(DESTINATION_DIR + "2160x1620_4to3.jpg-" + getClass().getSimpleName() + "-extendbycolor.jpg");

        ExtendByColor extendByColor = new ExtendByColor();
        String command = extendByColor.command(i, o);

        String expected = MagickConfig.builder().build().command()+
                " -limit thread 1 "
                +i
                +" -resize x1080  -quality 100% -background Black -compose Copy -gravity Center -extent 1920x1080+0+0 "
                +o;
        assertEquals(expected, command);

        // Execute
        CLI.exec(command, this);
        assertTrue(o.exists());
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(o));
    }

    // input 4:3 will be extended to 16:9 with black borders
    @Test
    void yellow_color_with_extend() {
        File i = TestResource.file("2160x1620_4to3.jpg");
        File o = new File(DESTINATION_DIR + "2160x1620_4to3.jpg-" + getClass().getSimpleName() + "-extendbycolor-yellow.jpg");

        ExtendByColor extendByColor = new ExtendByColor().color("Yellow");
        String command = extendByColor.command(i, o);

        String expected = MagickConfig.builder().build().command()+
                " -limit thread 1 "
                +i
                +" -resize x1080  -quality 100% -background Yellow -compose Copy -gravity Center -extent 1920x1080+0+0 "
                +o;
        assertEquals(expected, command);

        // Execute
        CLI.exec(command, this);
        assertTrue(o.exists());
        assertEquals(Resolution.FULL_HD, FFProbe.instance().resolution(o));
    }
}
