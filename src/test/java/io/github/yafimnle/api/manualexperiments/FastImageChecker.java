package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.api.TestResource;
import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.filter.ImageFilters;
import io.github.yafimnle.utils.CLI;

import java.io.File;

public class FastImageChecker {
    public static void main(String[] args) {
        FastImageChecker fastImageChecker = new FastImageChecker();
        fastImageChecker.foo();;
    }
    public void foo() {

        File input = TestResource.file("2160x1620_4to3-2.jpg");
        File output = new File("/tmp/1.jpg");

        input = ImageFilters.paint(1085, 1185, 65, 90, 5).process(input, "/tmp"); // gay
        input = ImageFilters.paint(1910, 1185, 40, 40, 5).process(input, "/tmp"); // karoheld-typ
        input = ImageFilters.paint(1260, 1225, 25, 25, 5).process(input, "/tmp"); // chinese girl
        input = ImageFilters.paint(295, 1245, 70, 70, 5).process(input, "/tmp");  // typ unten links

        CLI.exec(AR.enlargeByColor().command(input, output), this);
        CLI.exec("xdg-open " + output, this);

        /*
        ffmpeg -ss 00:00:26 -i CLIP0127_Herceg_Novi_360grad.AVI-1080p.mp4 -vframes 1 output.jpg -y
         */
    }
}
