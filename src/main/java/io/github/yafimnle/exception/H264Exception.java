package io.github.yafimnle.exception;

import java.io.IOException;

public class H264Exception extends RuntimeException {
    public H264Exception(IOException e) {
        super(e);
    }

    public H264Exception(String youNeedToUseVideo) {
        super(youNeedToUseVideo);
    }
}
