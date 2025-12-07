package io.github.yafimnle.utils;

import io.github.yafimnle.exception.H264Exception;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {
    private FileUtils() {
        // Singleton
    }
    public static void writeStringBuilderToFile(StringBuilder stringBuilder, String filename) {
        var file = new File(filename);
        try (var writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(stringBuilder);
        } catch (IOException e) {
            throw new H264Exception(e);
        }
    }


    public static File file(String... parts) {
        var sb = new StringBuilder();
        for (int i=0; i<=parts.length-2; i++) {
            sb.append(parts[i]).append(File.separator);
        }
        sb.append(parts[parts.length-1]);
        return new File(sb.toString());
    }

    public static String escapeWhitespaces(File file) {
        return file.toString().replaceAll("\\s", "\\\\ ");
    }
}
