package com.sunbird.entity.model.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sunbird.entity.util.QueryUtils;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = QueryUtils.Table.DATA_NODE)
@Entity(name = QueryUtils.Table.DATA_NODE)
@DynamicUpdate
@TypeDefs({ @TypeDef(name = "json", typeClass = JsonType.class) })
public class EntityDao implements Cloneable {

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

	@Column(name = "is_active")
	private Boolean isActive;

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

	@Column(name = "wf_id")
	private String wfId;

	@Transient
	private List<Map<String, Object>> children;

	@PrePersist
	private void prePersistFunction() {
		if (this.isActive == null) {
			this.isActive = Boolean.TRUE;
		}
		if (this.status == null) {
			this.status = "NEW";
		}
	}

	@PostPersist
	public void preUpdateFunction() {
	}

	public EntityDao clone() throws CloneNotSupportedException {
		return (EntityDao) super.clone();
	}
}
