package io.github.yafimnle.config;

import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.enums.Gravity;
import io.github.yafimnle.transformation.Transformations;
import io.github.yafimnle.transformation.image.None;

public class Presets {
    public static Config x264_normal_quality(String sourceDir, String destinationDir) {
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("libx264") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        // "-rc vbr -cq:v 24 -b_ref_mode middle -spatial-aq 1"
                        .encoderOptions("-crf 23") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
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
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .vid2vidscaleFlags(":flags=lanczos")
                        .fadelength(1)
                        .framerate(50)
                        .codec("libx264") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        // "-rc vbr -cq:v 24 -b_ref_mode middle -spatial-aq 1"
                        .encoderOptions("-crf 20 -profile:v high -preset slow") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                );
    }

    public static Config hw_h264_nvenc(String sourceDir, String destinationDir) {
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg -hwaccel cuda") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("h264_nvenc") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        .encoderOptions("-rc vbr -cq 30") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                );
    }

    public static Config hw_h265_hevc(String sourceDir, String destinationDir) {
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg -hwaccel cuda") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("hevc_nvenc") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        //.encoderOptions("-rc constqp -qp:v 20")
                        .encoderOptions("-rc vbr -cq 28") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                );
    }

    public static Config hw_h265_hevc_no_transformation(String sourceDir, String destinationDir) {
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER, true)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg -hwaccel cuda") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .codec("hevc_nvenc") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        //.encoderOptions("-rc constqp -qp:v 20")
                        .encoderOptions("-rc vbr -cq 28") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                )
                .transformConfig(TransformConfig.transformConfig()
                        .overrideVideoTransformation(Transformations.videoTransformationNone())
                        .overrideImageTransformation(new None()) // Still Image
                );
    }

    public static Config hw_h264_no_transformation(String sourceDir, String destinationDir) {
        return Config.instance()
                .resolution(Resolution.FULL_HD)
                .sourceDir(sourceDir)
                .destinationDir(destinationDir)
                .magick(MagickConfig.magick()
                        .command("/home/oliver/imagemagick-source/ImageMagick/utilities/magick")
                        .defaultImageAspectRatio(AR.crop(Gravity.CENTER)) // default is false, this is a speedup with img transform FastNone where to scale is neccessary
                )
                .ffmpeg(FFMpegConfig.ffmpeg()
                        .command("ffmpeg -hwaccel cuda") // default "ffmpeg"
                        .imgToVidSeconds(5)
                        .fadelength(1)
                        .framerate(25)
                        .vid2vidscaleFlags(":flags=lanczos")
                        .codec("h264_nvenc") // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
                        //.encoderOptions("-rc constqp -qp:v 20")
                        .encoderOptions("-rc vbr -cq 25") // default "-crf 23"           ungefaehr gleich zu "-rc vbr -cq 27", kleinerer cq -> bessere Qualität
                        .threads(1) // Slow down encoding, but reduce CPU usage
                        .forceSkipReencoding(false) // this is for concat/merging
                )
                .transformConfig(TransformConfig.transformConfig()
                        .overrideVideoTransformation(Transformations.videoTransformationNone())
                        .overrideImageTransformation(new None()) // Still Image
                );
    }
}
