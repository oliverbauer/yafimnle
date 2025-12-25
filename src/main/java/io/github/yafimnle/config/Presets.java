package io.github.yafimnle.config;

import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.enums.Gravity;
import io.github.yafimnle.image.filter.ImageFilters;
import io.github.yafimnle.transformation.Transformations;
import io.github.yafimnle.image.transformation.None;

import java.util.List;

public class Presets {
    public static Config x264_normal_quality(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg")
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("libx264")
                        .encoderOptions("-crf 23")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                );
    }

    /**
     * For nightly builds. This is a very slow option which gives very nice looking results.
     * <p>
     *     Options used
     *     <ul>
     *         <li>H.264 Codec with CRF 23 and High Profile and Preset Slow</li>
     *         <li>Full HD Resolution</li>
     *         <li>No hardware acceleration</li>
     *         <li>No direct scale on images, only aspect ratio (scale will be applied when image to video conversion takes place)</li>
     *         <li>50 FPS</li>
     *         <li>Lanczos for Scaling</li>
     *     </ul>
     * </p>
     * @param sourceDir
     * @param destinationDir
     * @return
     */
    public static Config x264_high_quality(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER))
                        .preprocessFilters(List.of(ImageFilters.waifu2xUpscale()))
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg")
                        .imgToVidSeconds(5)
                        .scaleFlags(":flags=lanczos")
                        .fadelength(1)
                        .framerate(50)
                        .codec("libx264")
                        .preset("slow")
                        .profile("high")
                        .encoderOptions("-crf 20")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                );
    }

    public static Config hw_h264_nvenc(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, false)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg -hwaccel cuda")
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .scaleFlags(":flags=lanczos")
                        .codec("h264_nvenc")
                        .encoderOptions("-rc vbr -cq 23")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                );
    }

    public static Config hw_h265_hevc(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, false)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg -hwaccel cuda")
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .scaleFlags(":flags=lanczos")
                        .codec("hevc_nvenc")
                        .encoderOptions("-rc vbr -cq 23")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                );
    }

    public static Config hw_h265_hevc_no_transformation(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is necessary
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg -hwaccel cuda")
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("hevc_nvenc")
                        .encoderOptions("-rc vbr -cq 28")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                )
                .transformConfig(TransformConfig.builder()
                        .videoTransformation(Transformations.videoTransformationNone())
                        .imageTransformation(new None()) // Still Image
                        .build()
                );
    }

    public static Config hw_h264_no_transformation(String sourceDir, String destinationDir) {
        return Config.freshInstance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.builder()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER))
                        .build()
                )
                .ffmpeg(FFMpegConfig.builder()
                        .command("ffmpeg -hwaccel cuda")
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .scaleFlags(":flags=lanczos")
                        .codec("h264_nvenc")
                        .encoderOptions("-rc vbr -cq 25")
                        .threads("-threads 1") // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                        .build()
                )
                .transformConfig(TransformConfig.builder()
                        .videoTransformation(Transformations.videoTransformationNone())
                        .imageTransformation(new None()) // Still Image
                        .build()
                );
    }
}
