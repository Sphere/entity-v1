package com.sunbird.entity.model.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class CompetencyLevelRequest {
    private Integer competencyId;
    private List<Integer> levelIds;
}
