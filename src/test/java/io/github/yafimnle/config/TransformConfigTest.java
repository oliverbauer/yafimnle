package io.github.yafimnle.config;

import io.github.yafimnle.YaFIMnle;
import io.github.yafimnle.common.Builder;
import io.github.yafimnle.imagemagick.ImageBuilder;
import io.github.yafimnle.ffmpeg.FilterComplexs;
import io.github.yafimnle.ffmpeg.VideoBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

class TransformConfigTest {

    @Test
    void preset_no_transformation() {
        // given
        Presets.hw_h265_hevc_no_transformation(
                new File(getClass().getClassLoader().getResource("2160x1620_4to3.jpg").getFile()).getParentFile().getAbsolutePath(),
                "/tmp/yafimnle"
        ).resolution(Resolution.LOW_QUALITY);

        var cut = new YaFIMnle("test1");

        // when
        cut.of(
                img("2160x1620_4to3-2.jpg").as("test1-1.mp4"),
                vid("1920x1080_50fps.mp4").as("test1-2.mp4"),
                img("2160x1620_4to3.jpg").as("test1-3.mp4")
        ).create();

        // then
    }


    String mainTitle = "Main constant";
    String subTitle = "Sub constant";
    String detailTitle = "NONE";
    protected ImageBuilder img(String img) {
        return YaFIMnle.img(img, FilterComplexs.zoomIn(detailTitle, subTitle, mainTitle, false));
    }
    protected VideoBuilder vid(String vid) {
        return YaFIMnle.vid(vid, FilterComplexs.videoTransformation(detailTitle, subTitle, mainTitle, false));
    }

    @Test
    void preset_no_transformation_override_img_zoom_in_and_text_outline() {
        // given
        Presets.hw_h265_hevc(
                new File(getClass().getClassLoader().getResource("2160x1620_4to3.jpg").getFile()).getParentFile().getAbsolutePath(),
                "/tmp/yafimnle"
        ).resolution(Resolution.LOW_QUALITY);

        var cut = new YaFIMnle("test2");

        // when
        detailTitle = "";
        Builder builder1 = img("2160x1620_4to3-2.jpg")
                .as("test2-1.mp4");
        detailTitle = "for video";
        Builder builder2 = vid("1920x1080_50fps.mp4")
                .filterCompex(FilterComplexs.videoTransformation(mainTitle, subTitle, detailTitle, true))
                .as("test2-2.mp4");
        detailTitle = "for image";
        Builder builder3 = img("2160x1620_4to3.jpg")
                .as("test2-3.mp4");

        cut.of(
                List.of(builder1, builder2, builder3)
        ).create();

        // then
    }
}
