package com.sunbird.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "position_role_mapping",type = "doc")
public class PositionRoleMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "position_id", nullable = false)
    private Integer positionId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;
}
