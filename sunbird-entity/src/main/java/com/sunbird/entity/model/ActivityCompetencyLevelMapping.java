package com.sunbird.entity.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;

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

    @Column(name = "competency_id", nullable = false)
    private Integer competencyId;

    @Type(type = "json")
    @Column(name = "level_ids", columnDefinition = "json")
    private List<Integer> levelIds; // Ordered list
}
