package io.github.yafimnle.utils;

import io.github.yafimnle.exception.H264Exception;
import io.github.yafimnle.ffmpeg.FFProbe;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Log4j2
public class CLI {
    private static Executor exec = DefaultExecutor.builder().get();

    public static String exec(String command, Object clazz) {
        if (clazz.getClass().equals(FFProbe.class)) {
            log.debug("Executing ({})...\n\t{}", clazz.getClass().getSimpleName(), Logs.green(command));
        } else {
            log.info("Executing ({})...\n\t{}", clazz.getClass().getSimpleName(), Logs.green(command));
        }

        var commandLine = new CommandLine("sh")
                .addArgument("-c")
                .addArgument(command, false);

        var byteArrayOutputStream = new ByteArrayOutputStream();
        var streamHandler = new PumpStreamHandler(byteArrayOutputStream);
        exec.setStreamHandler(streamHandler);

        try {
            var start = Instant.now();
            exec.execute(commandLine);
            var output = byteArrayOutputStream.toString().trim();
            if (output.isEmpty()) {
                log.info("Executed in {}", Logs.time(start));
            } else {
                if (clazz.getClass().equals(FFProbe.class)) {
                    log.debug("Executed (output {}) in {}", Logs.blue(output), Logs.time(start));
                } else {
                    log.info("Executed (output {}) in {}", Logs.blue(output), Logs.time(start));
                }
            }
            return output;
        } catch (IOException e) {
            log.error("Execute failed:\n {}\n {}", command, Logs.red(byteArrayOutputStream.toString().trim()));
            if (command.contains("-loglevel quiet")) {
                log.error("Retry without quiet to view the error:");
                return exec(command.replace("-loglevel quiet", ""), clazz);
            } else {
                throw new H264Exception(e);
            }
        }
    }

    public void execWithInheritIO(String command) throws IOException, InterruptedException {
        var builder = new ProcessBuilder("bash", command);
        builder.inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE);
        builder.start().waitFor();
    }
}