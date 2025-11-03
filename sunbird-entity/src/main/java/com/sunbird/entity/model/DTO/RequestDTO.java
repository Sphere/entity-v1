package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDTO<T> {
    private T request;
}

