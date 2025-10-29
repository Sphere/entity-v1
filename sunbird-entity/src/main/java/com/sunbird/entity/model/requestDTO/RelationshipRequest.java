package com.sunbird.entity.model.requestDTO;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RelationshipRequest {
    private String type; // e.g. "POSITION_ROLE", "ROLE_ACTIVITY", "ACTIVITY_COMPETENCY_LEVEL"
    private String parentId;
    private List<String> childIds;
    private Map<String, List<String>> childMap;

}
