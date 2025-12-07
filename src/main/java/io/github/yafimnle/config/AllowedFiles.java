package io.github.yafimnle.config;

import io.github.yafimnle.exception.H264Exception;

import java.io.File;

public class AllowedFiles {
    private AllowedFiles() {

    }

    public static void checkIfAllowedImage(File file) throws H264Exception {
        if (!file.getName().endsWith("JPG")
                && !file.getName().endsWith("jpg")
                && !file.getName().endsWith("nef")
                && !file.getName().endsWith("NEF")) {
            throw new H264Exception("You need to use image");
        }
    }

    public static void checkIfAllowedVideo(File file) throws H264Exception {
        if (!file.getName().endsWith("MP4")
                && !file.getName().endsWith("mp4")
                && !file.getName().endsWith("AVI")
                && !file.getName().endsWith("avi")
                && !file.getName().endsWith("mts")
                && !file.getName().endsWith("MTS")) {
            throw new H264Exception("You need to use video");
        }
    }
}
