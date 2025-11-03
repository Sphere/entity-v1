package com.sunbird.entity.util;


import com.sunbird.entity.model.DTO.ResponseDTO;

import java.util.UUID;

public class ResponseUtil {

    // Success response
    public static <T> ResponseDTO<T> successResponse(T data, String apiId) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setId(apiId);

        ResponseDTO.Params params = new ResponseDTO.Params();
        params.setResmsgid(UUID.randomUUID().toString());
        params.setMsgid(UUID.randomUUID().toString());
        params.setStatus("SUCCESS");
        params.setErr(null);
        params.setErrmsg(null);
        response.setParams(params);

        ResponseDTO.Result<T> result = new ResponseDTO.Result<>();
        result.setData(data);
        response.setResult(result);

        return response;
    }

    // Error response
    public static <T> ResponseDTO<T> errorResponse(String apiId, String errorCode, String errorMessage) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setId(apiId);

        ResponseDTO.Params params = new ResponseDTO.Params();
        params.setResmsgid(UUID.randomUUID().toString());
        params.setMsgid(UUID.randomUUID().toString());
        params.setStatus("FAILED");
        params.setErr(errorCode);
        params.setErrmsg(errorMessage);
        response.setParams(params);

        response.setResponseCode("ERROR");

        ResponseDTO.Result<T> result = new ResponseDTO.Result<>();
        result.setData(null); // No data for errors
        response.setResult(result);

        return response;
    }
}

