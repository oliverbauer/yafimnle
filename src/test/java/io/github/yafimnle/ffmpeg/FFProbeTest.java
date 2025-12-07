package io.github.yafimnle.ffmpeg;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FFProbeTest {
    @BeforeEach
    void freshConfig() {
        Config.freshInstance();
    }

    @Test
    void resolution_ar43_unknown() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");

        // when
        Resolution actual = FFProbe.instance().resolution(i);

        // then
        assertEquals(5184, actual.width());
        assertEquals(3888, actual.height());
        assertEquals("unknown", actual.apprev());
        assertEquals("5184x3888", actual.dimension());
        assertEquals("4:3", actual.ar());
        assertEquals(Resolution.UNKNOWN, actual);
    }

    @Test
    void resolution_ar169_fullhd() {
        // given
        File i = TestResource.file("1920x1080_16to9.jpg");

        // when
        Resolution actual = FFProbe.instance().resolution(i);

        // then
        assertEquals(1920, actual.width());
        assertEquals(1080, actual.height());
        assertEquals("1080p", actual.apprev());
        assertEquals("1920x1080", actual.dimension());
        assertEquals("16:9", actual.ar());
        assertEquals(Resolution.FULL_HD, actual);
    }

    @Test
    void resolution_ar169_unknown() {
        // given
        File i = TestResource.file("5184x2920_16to9.JPG");

        // when
        Resolution actual = FFProbe.instance().resolution(i);

        // then
        assertEquals(5184, actual.width());
        assertEquals(2920, actual.height());
        assertEquals("unknown", actual.apprev());
        assertEquals("5184x2920", actual.dimension());
        assertEquals("16:9", actual.ar());
        assertEquals(Resolution.UNKNOWN, actual);
    }
}
