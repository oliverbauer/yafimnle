package io.github.yafimnle.config;

// Note: Do not use lombok here in this class unless change from instance to "this"
public class FFMpegConfig {
    private static FFMpegConfig instance;

    private String command = "ffmpeg";

    private int imgToVidSeconds = 5;
    private int fadelength = 1;

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
    private String loglevel = "-loglevel quiet";

    private String builderLoglevel = "-loglevel quiet";
    private String logLevelVideoonly = "-loglevel quiet";
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
    private String vid2vidscaleFlags = "";
    public FFMpegConfig vid2vidscaleFlags(String flags) {
        instance.vid2vidscaleFlags = flags;
        return instance;
    }

    private String vid2vidaudioFilter = "-filter:a loudnorm -b:a 192k -ac 2 -ar 44100";

    // TODO use only one loglevel!
    private String logevelIimageToVideo = "-loglevel quiet";
    private String logevelAudioonlyVideoonly = "-loglevel quiet";
    private String logevelMergeAudioonlyVideoonly = "-loglevel quiet";

    private String videoCRF = "-crf 20";

    private Integer framerate = 25;

    /**
     * Configures the number of threads when creating the "-videoonly-{%1080p}.mp4"
     * <b>Note</b>: Needs to be an empty string or in the form '-threads x' (with x something like 1 or 2 or 4). If it is
     * an empty string, ffmpeg will decide (which normally means use a larger amount resulting in high CPU usage)
     */
    private String threads = "-threads 2";

    /**
     * https://silentaperture.gitlab.io/mdbook-guide/encoding/x264.html#ratecontrol-lookahead
     * <p>
     * "The ratecontrol lookahead (rc-lookahead) setting determines how far ahead the video buffer verifier (VBV) and macroblock tree (mbtree) can look. Raising this can slightly increase memory use, but it's generally best to leave this as high as possible:
     * --rc-lookahead 250
     * If you're low on memory, you can lower it to e.g. 60."
     */
    private Integer vidEncH264RCLookahreadFor2160p = 20;
    /**
     * https://silentaperture.gitlab.io/mdbook-guide/encoding/x264.html#ratecontrol-lookahead
     * <p>
     * "The ratecontrol lookahead (rc-lookahead) setting determines how far ahead the video buffer verifier (VBV) and macroblock tree (mbtree) can look. Raising this can slightly increase memory use, but it's generally best to leave this as high as possible:
     * --rc-lookahead 250
     * If you're low on memory, you can lower it to e.g. 60."
     */
    private Integer vidEncH264RCLookahreadFor1080p = 100;


    /**
     * If you know your input videos have same dimension. See e.g. HerbstInTirolUndBayern_V2_OutlineV2 for joining after new apporach of
     * single image->video.
     */
    private boolean forceSkipReencoding = false;



    // or libx265 or hevc_nvenc    libx264 (h264_nvenc)
    private String codec = "libx264";








    // Konstructor
    private FFMpegConfig() {
        // private
    }

    public static FFMpegConfig ffmpeg() {
        if (instance == null) {
            instance = new FFMpegConfig();
        }
        return instance;
    }

    public String codec() {
        return instance.codec;
    }
    public FFMpegConfig codec(String codec) {
        instance.codec = codec;
        return instance;
    }

    public String loglevel() {
        return instance.loglevel;
    }
    public FFMpegConfig loglevel(String loglevel) {
        instance.loglevel = loglevel;
        return instance;
    }
    public FFMpegConfig loglevelQuiet() {
        instance.loglevel = "-loglevel quiet";
        return instance;
    }

    public FFMpegConfig forceSkipReencoding(boolean skip) {
        instance.forceSkipReencoding = skip;
        return instance;
    }
    public boolean forceSkipReencoding() {
        return instance.forceSkipReencoding;
    }


    public String builderLoglevel() {
        return instance.builderLoglevel;
    }
    public FFMpegConfig quiet() {
        return instance;
    }

    public String logLevelVideoonly() {
        return instance.logLevelVideoonly;
    }
    public FFMpegConfig ffmpegLogLevelVideoonlyQuiet() {
        return instance;
    }


    /**
     * -crf 20
     * or
     *-rc vbr -cq 27
     * if (codec = h264_nvenc)
     *
     * @param crf
     * @return
     */
    public FFMpegConfig videoCRF(String crf) {
        instance.videoCRF = crf;
        return instance;
    }


    public FFMpegConfig fadelength(int fimFadelength) {
        instance.fadelength = fimFadelength;
        return instance;
    }

    public int fadelength() {
        return instance.fadelength;
    }


    public String command() {
        return instance.command;
    }

    public FFMpegConfig command(String command) {
        instance.command = command;
        return instance;
    }

    public String vid2vidscaleFlags() {
        return instance.vid2vidscaleFlags;
    }

    public FFMpegConfig disableVid2VidAudioFilter() {
        instance.vid2vidaudioFilter = "";
        return this;
    }

    public String vid2vidaudioFilter() {
        return instance.vid2vidaudioFilter;
    }

    public String logevelIimageToVideo() {
        return instance.logevelIimageToVideo;
    }

    public String logevelAudioonlyVideoonly() {
        return instance.logevelAudioonlyVideoonly;
    }

    public String logevelMergeAudioonlyVideoonly() {
        return instance.logevelMergeAudioonlyVideoonly;
    }

    public String videoCRF() {
        return instance.videoCRF;
    }

    public Integer framerate() {
        return instance.framerate;
    }
    public FFMpegConfig framerate(int framerate) {
        instance.framerate = framerate;
        return instance;
    }

    public String threads() {
        return instance.threads;
    }

    public FFMpegConfig threads(int threads) {
        instance.threads = "-threads "+threads;
        return instance;
    }

    public Integer vidEncH264RCLookahreadFor2160p() {
        return instance.vidEncH264RCLookahreadFor2160p;
    }

    public Integer vidEncH264RCLookahreadFor1080p() {
        return instance.vidEncH264RCLookahreadFor1080p;
    }

    public Integer imgToVidSeconds() {
        return instance.imgToVidSeconds;
    }
    public FFMpegConfig imgToVidSeconds(int seconds) {
        instance.imgToVidSeconds = seconds;
        return  instance;
    }
}
