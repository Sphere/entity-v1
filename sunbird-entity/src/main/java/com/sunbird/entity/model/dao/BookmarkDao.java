package com.sunbird.entity.model.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sunbird.entity.util.QueryUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = QueryUtils.Table.BOOKMARK)
@Table(name = QueryUtils.Table.BOOKMARK)
public class BookmarkDao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = QueryUtils.TableFields.NODE_ID)
	private int nodeId;

	@Column(name = QueryUtils.TableFields.USER_ID)
	private String userId;

}
