package io.github.yafimnle.ffmpeg.filter;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.ffmpeg.filter.VidStab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;

class VidStabTest {
    @BeforeEach
    void freshConfig() {
        Config.freshInstance(useHardwareAcceleration);
    }

    @Test
    @Disabled
    void test() {
        // 1920x1080_50fps.mp4
        //String source = "/media/oliver/Extreme SSD/2022/2022.10.16.scharnitz.gleirschklamm/DSCN7943.MP4";
        // given
        File i = TestResource.file("1920x1080_50fps.mp4");

        // given
        //File input = new File(source);

        var actual = new VidStab().process(i, "/tmp");

    }
}
