package io.github.yafimnle.api.manualexperiments;

import io.github.yafimnle.utils.CLI;

public class ConnectingStuff {
    public static void main(String[] args) {
        ConnectingStuff connectingStuff = new ConnectingStuff();
        connectingStuff.foo();
    }

    public void foo() {
        String input = ConnectingStuff.class.getClassLoader().getResource("2160x1620_4to3-2.jpg").getFile();
        String output = "/home/oliver/temp.jpg";

        int threshold = 70;
        int areaThreashold = 30;
        int connectedComponents = 8;

        String overlay = "-fill black -colorize 80 -fill black";
        //
        overlay = "-fuzz 90%  -fill red -opaque black";
        overlay = "-brightness-contrast 10";
        overlay = "-paint 2";
        overlay = "-blur 0x20";
        overlay = "-fill white -colorize 100 -blur 0x10";
        //overlay = "-fuzz 40% -fill green -opaque white";

        String command = "convert "+input+" \\( -clone 0 "+overlay+" \\) \\( -clone 0 -threshold "+threshold+"% -define connected-components:verbose=true -define connected-components:area-threshold="+areaThreashold+" -define connected-components:mean-color=true -connected-components "+connectedComponents+" \\) -compose over -composite "+output;

        String consoleOutput = CLI.exec(command, this);

        String parts[] = consoleOutput.split("\n");
        StringBuilder sb = new StringBuilder("convert "+output+" -stroke black -strokewidth 5 -fill none ");
        //StringBuilder sb = new StringBuilder("convert "+output+" ");
        for (int i=1; i<=parts.length-1; i++) {
            String s = parts[i].trim();

            s = s.substring(s.indexOf(":") + 2);
            s = s.substring(0, s.indexOf(" "));
            System.err.println(s);

            String[] p = s.split("x");

            int x = Integer.parseInt(p[0]);
            int y = Integer.parseInt(p[1].split("\\+")[0]);
            int w = Integer.parseInt(p[1].split("\\+")[1]);
            int h = Integer.parseInt(p[1].split("\\+")[2]);

            String drabox = w + "," + h + " " + (w + x) + "," + (h + y);

            sb.append("-draw \"rectangle " + drabox + "\" ");
        }
        sb.append("/home/oliver/temp2.jpg");

        CLI.exec(sb.toString(), this);
        CLI.exec("xdg-open /home/oliver/temp2.jpg", this);
    }
}
