package io.github.yafimnle.config;

import io.github.yafimnle.common.FadeType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FFMpegConfig {
    @Builder.Default
    private String command = "ffmpeg";
    @Builder.Default
    private int imgToVidSeconds = 5;
    @Builder.Default
    private int fadelength = 1;
    @Builder.Default
    private FadeType fadeType = FadeType.FADE;

    /**
     * A global configuration for all ffmpeg commands. Defaults to quiet logging. You may
     * set this to an empty string to use default logging of ffmpeg. Logging could be overwritten for different
     * types of ffmpeg usage (overwrites this default).
     * E.g.
     * <pre>
     *     globally quit, but encoding image to video with default:
     *     - ffmpeg_loglevel_global = "-loglevel quiet"
     *     - ffmpeg_loglevel_image_to_video = ""
     * </pre>
     * Implementation will set loglevel for e.g. image to video converstion to ffmpeg_loglevel_image_to_video
     */
    @Builder.Default
    private String loggingConfig = "-loglevel quiet";

    /**
     * Appends flags while scaling an input video to an output video, if input video has a different dimension than
     * requested "Resolution" (this means it is used for upscale or downscale a video). It will not be applied if only
     * the framerate of input video changes from requested "framerate".
     * <p>
     * <b>Default</b> "" (means no flags are appended, ffmpeg will use "bicubic" by its default)
     * </p>
     * <p>
     * <b>Note</b> Changing this value may enlarge encoding significantly!
     * </p>
     * Some experiments already taken (searched SO):
     * <ul>
     *     <li>:flags=lanczos</li>
     *     <li>:flags=lanczos:param0=3</li>
     *     <li>:flags=lanczos,atadenoise</li>
     *     <li>:flags=lanczos+accurate_rnd</li>
     *     <li>:flags=lanczos+accurate_rnd,nlmeans</li>
     *     <li>:flags=lanczos+accurate_rnd,atadenoise,hqdn3d=4,unsharp=7:7:0.5</li>
     * </ul>
     * <p>
     * Dokumentation to read:
     * <ul>
     *     <li>Scale - https://ffmpeg.org/ffmpeg-scaler.html</li>
     *     <ul>
     *         <li>lanczos (Lanczos rescaling algorithm)</li>
     *         <li>bicubic (default if not set)</li>
     *     </ul>
     *     <li>Denoise - https://trac.ffmpeg.org/wiki/DenoiseExamples</li>
     *     <ul>
     *          <li>atadenoise (Adaptive Temporal-Averaging denoiser)</li>
     *          <li>nlmeans (Non-Local Means denoiser)</li>
     *     </ul>
     *     <li>Misc</li>
     *     <ul>
     *         <li>https://www.reddit.com/r/ffmpeg/comments/murf35/prores_to_hevc_compression_when_converting_from/</li>
     *     </ul>
     * </ul>
     * </p>
     */
    @Builder.Default
    private String vid2vidscaleFlags = "";
    @Builder.Default
    private String vid2vidaudioFilter = "-filter:a loudnorm -b:a 192k -ac 2 -ar 44100";

    /**
     * <b>Default:</b> "-crf 20" (used by default codec "libx264")
     * <p>
     * FFMpeg use "-crf 23" as default for H.264 (see <a href="https://trac.ffmpeg.org/wiki/Encode/H.264">https://trac.ffmpeg.org/wiki/Encode/H.264</a>)
     * (codec: libx264). Lower values means better quality but although larger videos.
     * </p>
     * <p>
     * <b>Note</b>: This option is not supported on codecs like "h264_nvenc" and "hevc_nvenc". If you got a NVidia card and like to use
     * hardware acceleration to you need to override this!
     * </p>
     * <p>
     * Tested with GeForce RTX 2060 SUPER.
     * </p>
     */
    @Builder.Default
    private String encoderOptions = "-crf 20";
    @Builder.Default
    private Integer framerate = 25;

    /**
     * Configures the number of threads when creating the "-videoonly-{%1080p}.mp4"
     * <b>Note</b>: Needs to be an empty string or in the form '-threads x' (with x something like 1 or 2 or 4). If it is
     * an empty string, ffmpeg will decide (which normally means use a larger amount resulting in high CPU usage)
     */
    @Builder.Default
    private String threads = "-threads 2";

    /**
     * https://silentaperture.gitlab.io/mdbook-guide/encoding/x264.html#ratecontrol-lookahead
     * <p>
     * "The ratecontrol lookahead (rc-lookahead) setting determines how far ahead the video buffer verifier (VBV) and macroblock tree (mbtree) can look. Raising this can slightly increase memory use, but it's generally best to leave this as high as possible:
     * --rc-lookahead 250
     * If you're low on memory, you can lower it to e.g. 60."
     *
     *- x264-params rc_lookahead=20
     * -x264-params rc_lookahead=20:threads=2:slices=0
     */
    @Builder.Default
    private String vidEncH264RCLookahreadFor2160p = "";

    /**
     * If you know your input videos have same dimension. See e.g. HerbstInTirolUndBayern_V2_OutlineV2 for joining after new apporach of
     * single image->video.
     */
    @Builder.Default
    private boolean forceSkipReencoding = false;

    /**
     * <p>
     * <b>Default:</b> "libx264" for usage of H.264
     * </p>
     * <p>
     * Note: encoderOptions needs to be adapted if codecs like h264_nvenc or hevc_nvenc are used!
     * </p>
     */
    @Builder.Default
    private String codec = "libx264";
    @Builder.Default
    private String profile = null; // See https://trac.ffmpeg.org/wiki/Encode/H.264 you may set this to "main", "high", "high422"
    @Builder.Default
    private String preset = null; // See https://trac.ffmpeg.org/wiki/Encode/H.264 you may set this to "ultrafast", "veryslow". Default by FFMpeg: "medium"
}
