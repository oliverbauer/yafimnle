package io.github.yafimnle.ffmpeg.filtercomplex.filter;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.ffmpeg.ImageTransformer;
import io.github.yafimnle.ffmpeg.filtercomplex.FilterComplex;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeftToRightTest {
    Config c;
    FilterChainEntry cut = new LeftToRight(10);

    @BeforeEach
    void freshConfig() {
        c = Config.freshInstance(useHardwareAcceleration);
        c.resolution(Resolution.ULTRA_HD);
    }

    @Test
    void fromImageToVideo() {
        // given
        File i = TestResource.file("5184x3888_4to3.JPG");
        File oImg = new File(c.destinationDir() + "-5184x3888_4to3.JPG-" + getClass().getSimpleName() + "-temp.jpg");
        File oVid = new File(c.destinationDir() + "-5184x3888_4to3.JPG-" + getClass().getSimpleName() + "-temp.mp4");

        var preconvert = c.magick().command()+" "+i+" -gravity Center -crop x"+c.resolution().height()+"+0+0 -quality 100 "+oImg;
        CLI.exec(preconvert, this);

        // when
        var command = new ImageTransformer().transformImage(oImg, oVid, FilterComplex.of(false, cut).getFilterComplex(), 10);
        CLI.exec(command, this);

        // then
        assertEquals(Resolution.ULTRA_HD, FFProbe.instance().resolution(oVid));
    }
}
