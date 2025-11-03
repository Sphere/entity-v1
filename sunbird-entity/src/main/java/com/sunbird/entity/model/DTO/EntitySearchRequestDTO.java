package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntitySearchRequestDTO {
    private String type;
    private String keyword; // optional
}
