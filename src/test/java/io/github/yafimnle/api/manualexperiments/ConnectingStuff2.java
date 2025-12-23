package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.utils.CLI;

public class ConnectingStuff2 {
    public static void main(String[] args) {
        ConnectingStuff2 connectingStuff2 = new ConnectingStuff2();
        connectingStuff2.foo();
    }
    public void foo() {

        String input = ConnectingStuff2.class.getClassLoader().getResource("2160x1620_4to3-2.jpg").getFile();
        String output = "/home/oliver/temp-x.jpg";

        // https://stackoverflow.com/questions/69044283/imagemagick-to-crop-image-based-on-based-on-rectangular-border-color
        String command = "color=\"srgb(15,22,12)\"\n" +
                "fuzzval=9\n" +
                "bbox=`convert \\\n" +
                input+" \\\n" +
                "-fuzz $fuzzval% \\\n" +
                "-fill black +opaque \"$color\" \\\n" +
                "-fill white -opaque \"$color\" \\\n" +
                "-type bilevel \\\n" +
                "-define connected-components:exclude-header=true \\\n" +
                "-define connected-components:area-threshold=500 \\\n" +
                "-define connected-components:keep-top=1 \\\n" +
                "-define connected-components:verbose=true \\\n" +
                "-define connected-components:mean-color=true \\\n" +
                "-connected-components 8 null: | \\\n" +
                "grep \"gray(255)\" | awk '{print $2}'`\n" +
                "echo \"$bbox\"\n" +
                "convert "+input+" -crop $bbox +repage "+output;

        String consoleOutput = CLI.exec(command, this);
        CLI.exec("xdg-open "+output, this);
    }
}
