package io.github.yafimnle.video;

public class DelogoFilter {
    // TODO video->video: delogo-filter: Gesichter aus Videos unkenntlich machen
    // ffmpeg -y -i DSCN7074_Raggaschlucht.MP4 -filter_complex "[0:v]delogo=x=1200:y=300:w=150:h=100[v]" -map '[v]' -an "6.mp4"
}
