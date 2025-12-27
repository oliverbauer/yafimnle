package io.github.yafimnle.ffmpeg.filtercomplex.filter;

import io.github.yafimnle.config.Config;

public class LeftToRight extends FilterChainEntry {

    private int seconds = 5;

    public LeftToRight() {
        this(5);
    }

    public LeftToRight(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public String getEntry() {
        var res = Config.instance().resolution();
        var dimension = Config
                .instance()
                .resolution()
                .dimension()
                .replace("x", ":");

        return "crop="+dimension+":x='(iw-"+res.width()+") * t / "+seconds+"':y=0";
    }
}
