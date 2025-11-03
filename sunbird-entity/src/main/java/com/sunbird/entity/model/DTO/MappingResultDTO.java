package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MappingResultDTO {
    private String parentId;
    private String type;
    private String status; // "SUCCESS" or "FAILED"
    private String message;
}

