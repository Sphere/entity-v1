package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ResponseDTO<T> {

    private String id;
    private String ver = "v1";
    private String ts = Instant.now().toString();
    private Params params;
    private String responseCode = "OK";
    private Result<T> result;

    @Getter
    @Setter
    public static class Params {
        private String resmsgid = UUID.randomUUID().toString();
        private String msgid = UUID.randomUUID().toString();
        private String status;
        private String err;
        private String errmsg;
    }

    @Getter
    @Setter
    public static class Result<T> {
        private T data; // This will hold the actual response payload for each API
    }
}

