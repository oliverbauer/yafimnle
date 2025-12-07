package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.utils.CLI;

public class Ex5Imagemagick {
    public static void main(String[] args) {
        Ex5Imagemagick ex5Imagemagick = new Ex5Imagemagick();
        ex5Imagemagick.foo();
    }
    public void foo() {


/*
    5184x3888 = wxh      %[fx:w]x%[fx:h] inut
    2588 = w/2           %[fx:w/2]
    1944 = h/2           %[fx:h/2]
    1294 = w/4           %[fx:w/4]
    972  = h/4           %[fx:h/4]
 */

    String output = "/tmp/temp.jpg";
    String command1 = """
   /home/oliver/ffmpeg-video-gen/ffmpeg-java/binaries/magick-7.1.1-43-Q16-HDRI-x86_64                 `# needed version 7.0.0`\\
            /home/oliver/ffmpeg-video-gen/experiments/cinemascope/DSC01329.jpg                   `# input file`\\
            -stroke green -strokewidth 10 -fill none -draw "rectangle %[fx:0],%[fx:h/4] %[fx:3840],%[fx:2160+h/4]"\\
            -stroke yellow -strokewidth 10 -fill none -draw "rectangle %[fx:100],%[fx:h/4] %[fx:3840+100],%[fx:2160+h/4]"\\
            -stroke red -strokewidth 10 -fill none -draw "rectangle %[fx:w-3840],%[fx:h/4] %[fx:w],%[fx:2160+h/4]"\\
            /tmp/temp.jpg         `# output file`
       \s""";
        CLI.exec(command1, this);
        CLI.exec("xdg-open /tmp/temp.jpg", this);

/*
        String command2 = """
        /home/oliver/Downloads/magick -extract 1920x1080+1296+972 /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text.jpg /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text-extract.jpg
        """;
        CLI.execute(command2);
        CLI.exec("xdg-open /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text-extract.jpg");
 */
    }
}
