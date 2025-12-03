package com.sunbird.entity.model.es;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * TODO: check FieldType.Keyword and its usage in search query
 */
@Getter
@Setter
@Document(indexName = "competencies-v1")
@Mapping(mappingPath = "es/mappings/es-entity-addition-prop-flatten-mapping.json") //TODO: check json config (without it mapper can work - major thing is flattening)
public class EntityDocument {

    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private String type;

    // full-text search + keyword for sorting/aggregations
    @Field(type = FieldType.Text, analyzer = "english")
    private String name;

    @Field(type = FieldType.Text, analyzer = "english")
    private String description;

    // Avoid mapping explosion for arbitrary key/value JSON -> flattened
    @Field
    private Map<String, Object> additionalProperties;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Integer)
    private Integer levelId;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS||strict_date_optional_time||epoch_millis")
    private Date createdDate;

    @Field(type = FieldType.Keyword)
    private String createdBy;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS||strict_date_optional_time||epoch_millis")
    private Date updatedDate;

    @Field(type = FieldType.Keyword)
    private String updatedBy;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss.SSS||strict_date_optional_time||epoch_millis")
    private Date reviewedDate;

    @Field(type = FieldType.Keyword)
    private String reviewedBy;

    // translations: if it's a small map like {"en": "...", "hi": "..."}, this object is fine.
    @Field(type = FieldType.Object)
    private Map<String, Object> translation;

    @Field(type = FieldType.Keyword)
    private String code;

    @Field(type = FieldType.Keyword)
    private String language;

}

