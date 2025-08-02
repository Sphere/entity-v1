package com.sunbird.entity.model.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class PositionRoleRequest {
    private Integer positionId;
    private List<Integer> roleIds;
}
