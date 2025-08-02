package com.sunbird.entity.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "competency_level_mapping")
public class CompetencyLevelMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "competency_id", nullable = false)
    private Integer competencyId;

    @Type(type = "json")
    @Column(name = "level_ids", columnDefinition = "json")
    private List<Integer> levelIds;
}

