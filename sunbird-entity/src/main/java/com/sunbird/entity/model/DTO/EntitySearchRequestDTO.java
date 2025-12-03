package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EntitySearchRequestDTO {
    private String searchText;
    private String status;
    private String level;
    private String code;
    private String language;
    private String type;
    private Map<String, String> query;
    private Integer limit;
    private Integer page;
    private Integer size;

}
