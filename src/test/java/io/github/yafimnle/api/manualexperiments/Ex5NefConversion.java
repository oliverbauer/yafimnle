package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.config.Config;
import io.github.yafimnle.config.Resolution;
import io.github.yafimnle.image.ImageBuilder;
import io.github.yafimnle.image.ar.AR;
import io.github.yafimnle.image.enums.Gravity;
import io.github.yafimnle.utils.CLI;

import java.io.File;

public class Ex5NefConversion {
    public static void main(String[] args) {
        Ex5NefConversion ex5NefConversion = new Ex5NefConversion();
        ex5NefConversion.foo();
    }

    public void foo() {

        String destinationDir = "/tmp/nef/";
        CLI.exec("rm -rf "+destinationDir, this);
        CLI.exec("mkdir "+destinationDir, this);

        File input = new File(Ex5NefConversion.class.getClassLoader().getResource("6000x4000_3to2.nef").getFile());
        File output = new File(destinationDir+"input.nef");
        File output2 = new File(destinationDir+"input.jpg");
        CLI.exec("cp "+input+" "+output, this);

        CLI.exec("exiftool -b -JpgFromRaw "+output+" > "+output2, this);

        Resolution quality = Resolution.FULL_HD;


        Config.instance()
                .destinationDir("/tmp/nef/")
                .resolution(quality);
        ImageBuilder imageBuilder = new ImageBuilder(output2)
                .ar(AR.crop(Gravity.SOUTH));

        // when
        imageBuilder.create();

        //CLI.exec("xdg-open "+destinationDir+"input.jpg-"+quality.apprev()+".mp4");
    }
}
