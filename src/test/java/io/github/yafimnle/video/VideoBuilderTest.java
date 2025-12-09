package io.github.yafimnle.video;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;

// TODO Add a fucking mts file since it fails in NationalparkKrka
class VideoBuilderTest {
    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration).resolution(Resolution.LOW_QUALITY);
    }

    @Test
    void low_quality_encoding_from_1920x1080_to_960x540_with_cutting_to_first_3_seconds() {
        // given
        VideoBuilder videoBuilder = new VideoBuilder(TestResource.file("1920x1080_50fps.mp4"));

        // when
        File actual1 = videoBuilder.seconds("00:00:00", "00:00:03").create();

        // then
        assertEquals(Resolution.from(960,540), FFProbe.instance().resolution(actual1));
        assertEquals("3.000000", FFProbe.instance().length(actual1));
    }

    @Test
    void low_quality_encoding_from_1920x1080_to_960x540_with_two_cuts_2sec_and_3sec_and_defined_output_names() {
        // given
        File file = TestResource.file("1920x1080_50fps.mp4");
        VideoBuilder videoBuilder = new VideoBuilder(file);

        // when
        File actual1 = videoBuilder.seconds("00:00:00", "00:00:02").as("part1.mp4").create();
        File actual2 = videoBuilder.seconds("00:00:08", "00:00:11").as("part2.mp4").create();

        // then
        assertEquals(Resolution.from(960,540), FFProbe.instance().resolution(actual1));
        assertEquals("2.000000", FFProbe.instance().length(actual1));
        assertEquals(Resolution.from(960,540), FFProbe.instance().resolution(actual2));
        assertEquals("3.000000", FFProbe.instance().length(actual2));
    }
}
