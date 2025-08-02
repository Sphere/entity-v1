package com.sunbird.entity.model.requestDTO;

import lombok.Data;

import java.util.List;

@Data
public class RoleCompetencyRequest {
    private Integer roleId;
    private List<Integer> competencyIds;
}
