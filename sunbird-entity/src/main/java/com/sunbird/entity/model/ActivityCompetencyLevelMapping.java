package com.sunbird.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "activity_competency_level_mapping",type = "doc")
public class ActivityCompetencyLevelMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "activity_id", nullable = false)
    private Integer activityId;

    @Type(type = "json")
    @Column(name = "competency_levels_map", columnDefinition = "json")
    private Map<Integer, List<Integer>> competencyLevelsMap;
}

