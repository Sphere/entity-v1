package com.sunbird.entity.model.DTO;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class EntityResponseDTO {
    private Integer id;

    private String type;

    private String name;

    private String description;

    private Map<String, Object> additionalProperties;

    private String status;

    private String source;

    private String level;

    private Integer levelId;

    private Date createdDate;

    private String createdBy;

    private Date updatedDate;

    private String updatedBy;

    private Date reviewedDate;

    private String reviewedBy;

    private Map<String, Object> translation;

    private String code;

    private String language;
}
