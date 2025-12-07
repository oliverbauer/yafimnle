package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.utils.CLI;

public class Ex4Imagemagick {
    public static void main(String[] args) {
        Ex4Imagemagick ex4Imagemagick = new Ex4Imagemagick();
        ex4Imagemagick.foo();
    }
    public void foo() {


/*
    5184x3888 = wxh      %[fx:w]x%[fx:h] inut
    2588 = w/2           %[fx:w/2]
    1944 = h/2           %[fx:h/2]
    1294 = w/4           %[fx:w/4]
    972  = h/4           %[fx:h/4]
 */

    String command1 = """
   /home/oliver/ffmpeg-video-gen/ffmpeg-java/binaries/magick-7.1.1-43-Q16-HDRI-x86_64                 `# needed version 7.0.0`\\
            /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888.jpg                   `# input file`\\
            -strokewidth 10 -stroke "rgba(255,0,0,0.5)" -draw "line 0,0 %[fx:w],%[fx:h]"              `# northwest to southeast`\\
            -strokewidth 10 -stroke "rgba(255,0,0,0.5)" -draw "line 0,%[fx:h] %[fx:w],0"              `# southwest to northeast`\\
            -strokewidth 10 -stroke "rgba(10,0,0,10.5)" -draw "line %[fx:w/2],%[fx:h] %[fx:w/2],0"    `# south to north`\\
            -strokewidth 10 -stroke "rgba(255,0,0,0.5)" -draw "line 0,%[fx:h/2] %[fx:w],%[fx:h/2]"    `# west to east`\\
            -strokewidth 10 -stroke "rgba(255,0,0,0.5)" -draw "line 0,%[fx:h/4] %[fx:w/4],%[fx:h/4]"  `# west of upper left box to east of upper left box`\\
            -strokewidth 10 -stroke "rgba(255,0,0,0.5)" -draw "line %[fx:w/4],0 %[fx:w/4],%[fx:h/4]"  `# north of upper left box to south of upper left box`\\
            -stroke black -undercolor yellow -pointsize 80 -gravity center -annotate 0 'CENTER'       `# comment`\\
                                                           -gravity north -annotate 1 'NORTH'         `# comment`\\
                                                           -gravity Northeast -annotate 1 'Northeast' `# comment`\\
                                                           -gravity east -annotate 1 'EAST'           `# comment`\\
            -stroke black -undercolor green -pointsize 80  -gravity Southeast -annotate 1 'Southeast' `# comment`\\
                                                           -gravity south -annotate 1 'SOUTH'         `# comment`\\
                                                           -gravity Southwest -annotate 1 'Southwest' `# comment`\\
                                                           -gravity west -annotate 1 'WEST'           `# comment`\\
                                                           -gravity Northwest -annotate 1 'Northwest' `# comment`\\
                                                           -gravity Northwest -annotate +1100+922 'Center of Northwest'           `# comment`\\
            -stroke blue -undercolor lightblue -pointsize 80 -annotate +75+%[fx:75+h/4] '(x,y)=(0,h/4)=(0,%[fx:h/4])'             `# comment`\\
            -stroke blue -undercolor lightblue -pointsize 80 -annotate +75+%[fx:75+h/2] '(x,y)=(0,h/2)=(0,%[fx:h/2])'             `# comment`\\
            -stroke blue -undercolor lightblue -pointsize 80 -annotate +%[fx:75+w/4]+75 '(x,y)=(w/4,0)=(%[fx:w/4],0)'             `# comment`\\
            -stroke blue -undercolor lightblue -pointsize 80 -annotate +%[fx:75+w/2]+75 '(x,y)=(w/2,0)=(%[fx:w/2],0)'            `# comment`\\
            -stroke green -strokewidth 10 -fill none -draw "rectangle %[fx:w/4],%[fx:h/4] %[fx:1920+w/4],%[fx:1080+h/4]"\\
            /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text.jpg         `# output file`
       \s""";
        CLI.exec(command1, this);
        CLI.exec("xdg-open /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text.jpg", this);


        String command2 = """
        /home/oliver/Downloads/magick -extract 1920x1080+1296+972 /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text.jpg /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text-extract.jpg
        """;
        CLI.exec(command2, this);
        CLI.exec("xdg-open /home/oliver/ffmpeg-video-gen/experiments/zoom/input_orig_5184x3888-with-text-extract.jpg", this);
    }
}
