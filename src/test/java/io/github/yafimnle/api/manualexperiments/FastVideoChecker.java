package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.image.filter.Transformations;
import io.github.yafimnle.utils.CLI;

import java.io.File;

public class FastVideoChecker {
    public static void main(String[] args) {
        FastVideoChecker fastVideoChecker = new FastVideoChecker();
        fastVideoChecker.foo();
    }
    public void foo() {

        File input = TestResource.file("2160x1620_4to3-2.jpg");
        File output = new File("/tmp/1.mp4");

        Transformations
                .zoomin()
                .speed(0.0005)
                .fromImageToVideo(
                        input,
                        output,
                        5,
                        "/tmp"
                );

        CLI.exec("mplayer "+output, this);


        // TODO try something like this:
        // https://stackoverflow.com/questions/36499930/ffmpeg-zoom-not-smooth-centered-but-zigzag
/*
        To zoom from zoom level 1 (1280x720) to zoom level 5 (256x144) and pan to bottom right, with final position (1011,568) in 125 frames, and output video as 320x180, use

-vf zoompan=z='min(zoom+0.0320,5)':d=125:x='(iw-(iw/zoom))*(1011/1024)': \
                                         y='(ih-(ih/zoom))*(568/576)',scale=320x180

        where 0.0320 is division of (final zoom - initial zoom) / frames to zoom in, 1024 is input width - final width and 576 is input height - final height.
        FFmpeg doesn't seem to smooth out the interpolation, so fine zoom and pans will be jittery.



Or:
https://superuser.com/questions/1127615/ffmpeg-zoompan-filter-examples
*/


/* TODO ffmpeg minterpolate / smooth interpolation
https://superuser.com/questions/1005315/interpolation-with-ffmpeg
        examples
           ffmpeg -i input.lowfps.hevc -filter "minterpolate='fps=120'" output.120fps.hevc
           ffmpeg -i input.hevc -filter "minterpolate='mi_mode=mci:mc_mode=aobmc:vsbmc=1'" output.hevc
                                         minterpolate=fps=60:mi_mode=mci:mc_mode=aobmc:me_mode=bidir:vsbmc=1
                                         minterpolate='mi_mode=mci:mc_mode=aobmc:vsbmc=1:fps=120
           https://github.com/dthpham/sminterpolate
           https://ffmpeg.org/ffmpeg-filters.html#minterpolate



 */









/*
 TODO hstack, vstack enable in Builder?
Nebeneinander, einmal mit filter, einmal ohne... um effekte zu zeigen

https://hhsprings.bitbucket.io/docs/programming/examples/ffmpeg/create_comparison_video/using_crop_and_hstack_vstack.html

ffmpeg -y -i "${inf}" -filter_complex "
[0:v]crop='iw/2:ih:0:0'[vo];
[0:v]crop='iw/2:ih:iw/2:0',hue=b=-2[vf];
[vo][vf]hstack[v]
" -map '[v]' -an \
  "${pref}.mp4"


ffmpeg -y -i /home/oliver/ffmpeg-video-gen/ffmpeg-java/src/test/resources/1920x1080_50fps.mp4 \
  -filter_complex "[0:v]crop='iw/2:ih:0:0'[vo];[0:v]crop='iw/2:ih:iw/2:0',hue=b=-2[vf];[vo][vf]hstack[v]" -map '[v]' -an /tmp/1920x1080_50fps-hstack.mp4

ffmpeg -y -i /home/oliver/ffmpeg-video-gen/ffmpeg-java/src/test/resources/1920x1080_50fps.mp4 \
  -filter_complex "[0:v]crop='iw/2:ih:0:0'[vo];[0:v]crop='iw/2:ih:iw/2:0',unsharp=13:13:5[vf];[vo][vf]hstack[v]" -map '[v]' -an /tmp/1920x1080_50fps-hstack.mp4

 */








        /*
        TODO curtain effect
        https://video.stackexchange.com/questions/22388/how-to-get-this-transition-using-ffmpeg
        https://stackoverflow.com/questions/59562512/ffmpeg-curtain-effect-slideshow-from-images
        https://superuser.com/questions/1325851/how-to-create-curtain-barn-doors-and-circle-wipe-effects-in-ffmpeg
         */

    }
}
