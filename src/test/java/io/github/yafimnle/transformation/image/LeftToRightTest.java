package io.github.yafimnle.transformation.image;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.ffmpeg.FFProbe;
import io.github.yafimnle.image.transformation.LeftToRight;
import io.github.yafimnle.transformation.Transformation;
import io.github.yafimnle.utils.CLI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.github.yafimnle.TestConstants.useHardwareAcceleration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeftToRightTest {
    Config c;
    Transformation cut = new LeftToRight();

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
        var actual = cut.fromImageToVideo(oImg, oVid, 10, c.destinationDir());

        // then
        assertEquals(Resolution.ULTRA_HD, FFProbe.instance().resolution(actual));
    }
}
