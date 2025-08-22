package com.sunbird.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "role_activity_mapping",type = "doc")
public class RoleActivityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "activity_ids", nullable = false)
    private List<Integer> activityIds;

    public RoleActivityMapping(Integer roleId, List<Integer> activityIds) {
        this.roleId = roleId;
        this.activityIds = activityIds;
    }
}

