package com.sunbird.entity.model.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class ActivityCompetencyLevelRequest {
    private Integer activityId;
    private Integer competencyId;
    private List<Integer> levelIds;
}
