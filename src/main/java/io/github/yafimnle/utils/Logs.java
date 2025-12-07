package io.github.yafimnle.utils;

import java.time.Duration;
import java.time.Instant;

public class Logs {
    private Logs() {
        // Utility class
    }
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String green(String command) {
        return ANSI_GREEN+command+ANSI_RESET;
    }

    public static String yellow(String command) {
        return ANSI_YELLOW+command+ANSI_RESET;
    }

    public static String blue(String command) {
        return ANSI_BLUE+command+ANSI_RESET;
    }

    public static String red(String command) {
        return ANSI_RED+command+ANSI_RESET;
    }

    public static String time(Instant start) {
        var encodingTime = Duration.between(start, Instant.now()).toSeconds();
        return String.format("%02dh:%02dm:%02ds", encodingTime / 3600, (encodingTime % 3600) / 60, (encodingTime % 60));
    }
}
