package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EntitySearchRequestDTO {
    private String type;
    private Map<String, String> query;
    private Integer limit;
}
