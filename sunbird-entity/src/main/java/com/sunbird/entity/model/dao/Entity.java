package com.sunbird.entity.model.dao;

import com.sunbird.entity.util.QueryUtils;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Table(name = "EntityDao")
@javax.persistence.Entity(name = "EntityDao")
@DynamicUpdate
@TypeDefs({ @TypeDef(name = "json", typeClass = JsonType.class) })
@Document(indexName = "entities",type = "doc")
public class Entity implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String type;

	@Column
	private String name;

	@Column
	private String description;

	@Type(type = "json")
	@Column(name = "additional_properties", columnDefinition = "json")
	private Map<String, Object> additionalProperties;

	@Column
	private String status;

	@Column
	private String source;

	@Column
	private String level;

	@Column(name = "level_id")
	private int levelId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reviewed_date")
	private Date reviewedDate;

	@Column(name = "reviewed_by")
	private String reviewedBy;

	@Type(type = "json")
	@Column(name = "translation", columnDefinition = "json")
	private Map<String, Object> translation;

	@Column
	private String code;


	@Transient
	private List<Map<String, Object>> children;


	@PostPersist
	public void preUpdateFunction() {
	}

	public Entity clone() throws CloneNotSupportedException {
		return (Entity) super.clone();
	}
}
