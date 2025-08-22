package com.sunbird.entity.model.requestDTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ActivityCompetencyLevelRequest {
    private Integer activityId;
    private Map<Integer, List<Integer>> competencyLevelsMap;

}
