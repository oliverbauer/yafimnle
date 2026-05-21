package io.github.yafimnle.imagemagick.ar;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.MagickConfig;
import io.github.yafimnle.exception.IllegalArgsException;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.*;

public class ExtractResolutionTest {
    private static final String DESTINATION_DIR = "/tmp/";

    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration);
    }

    // input is 16:9
    @Test
    void extract_valid() {
        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg-" + getClass().getSimpleName() + "-extract.jpg");

        ExtractResolution extractResolution = new ExtractResolution(100, 245, 0, 0);
        String command = extractResolution.command(i, o);
        String expected = MagickConfig.builder().build().command()+
                " -extract 100x245+0+0 "
                +i
                +" "
                +o;
        assertEquals(expected, command);

        // Execute
        CLI.exec(command, this);
        assertTrue(o.exists());
    }

    // input is 16:9
    @Test
    void extract_invalid_1() {
        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg-" + getClass().getSimpleName() + "-extract-should-not-exist.jpg");

        ExtractResolution extractResolution = new ExtractResolution(1921, 245, 0, 0);
        IllegalArgsException exception = assertThrows(IllegalArgsException.class, () -> {
            extractResolution.command(i, o);
        });

        String expectedMessage = "Image width is 1920 and target dimension dimWidth 1921. You requested starting from 0. Max allowed: 1920-1921=-1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void extract_invalid_2() {
        File i = TestResource.file("1920x1080_16to9.jpg");
        File o = new File(DESTINATION_DIR + "1920x1080_16to9.jpg-" + getClass().getSimpleName() + "-extract-should-not-exist.jpg");

        ExtractResolution extractResolution = new ExtractResolution(1820, 245, 101, 0);
        IllegalArgsException exception = assertThrows(IllegalArgsException.class, () -> {
            extractResolution.command(i, o);
        });

        String expectedMessage = "Image width is 1920 and target dimension dimWidth 1820. You requested starting from 101. Max allowed: 1920-1820=100";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
