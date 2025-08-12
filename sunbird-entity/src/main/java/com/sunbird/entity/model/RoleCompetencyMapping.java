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
@Document(indexName = "activity_competency_level_mapping",type = "doc")
public class RoleCompetencyMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "competency_id", nullable = false)
    private Integer competencyId;
}

