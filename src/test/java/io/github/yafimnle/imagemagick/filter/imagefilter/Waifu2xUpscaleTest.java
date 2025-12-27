package io.github.yafimnle.imagemagick.filter.imagefilter;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.imagemagick.filter.ImageFilters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Waifu2xUpscaleTest {
    @Test
    @Disabled
    void waifu2xUpscale() {
        // given
        File input = TestResource.file("1920x1080_16to9-2.jpg");

        var actual = ImageFilters.waifu2xUpscale().process(input, "/tmp");

        // then
        assertEquals(Resolution.ULTRA_HD, FFProbe.instance().resolution(actual));
    }
}
