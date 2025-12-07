package io.github.yafimnle.api;

import io.github.yafimnle.config.Resolution;

import java.io.File;

public class TestResource {
    /**
     * Returns the fully qualified path of a file from src/main/resources.<br>
     * Example:
     * <pre>
     *     <b>1920x1080_50fps.mp4</b>
     * </pre>
     * results in
     * <pre>
     *     <b>/home/oliver/ffmpeg-video-gen/ffmpeg-java/src/test/resources/1920x1080_50fps.mp4</b>
     * </pre>
     * @param resource
     * @return
     */
    public static String path(String resource) {
        return TestResource.class.getClassLoader().getResource(resource).getFile();
    }

    /**
     * Returns a file-object from path(String)
     * 
     * @param resource
     * @see TestResource#path(String)
     * @return
     */
    public static File file(String resource) {
        return new File(path(resource));
    }

    /**
     * Returns a file-object (result of encoding) based on a resource from src/test/resources and a target directory.
     * <br>
     * Example:
     * <pre>
     *     <b>/tmp/</b> and <b>1920x1080_50fps.mp4</b>
     * </pre>
     * results in
     * <pre>
     *     <b>/tmp/home/oliver/ffmpeg-video-gen/ffmpeg-java/src/test/resources/1920x1080_50fps.mp4</b>
     * </pre>
     * This is useful to check if a file has been copied.
     *
     * @param dir
     * @param resource
     * @return
     */
    public static File file(String dir, String resource) {
        return new File(dir+path(resource));
    }

    public static File file(String dir, String resource, Resolution quality) {
        String path = path(resource);
        return new File(dir+path+"-"+quality.apprev()+".mp4");
    }
}
