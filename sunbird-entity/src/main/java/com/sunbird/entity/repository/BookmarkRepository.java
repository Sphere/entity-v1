package com.sunbird.entity.repository;

import org.springframework.data.repository.CrudRepository;

import com.sunbird.entity.model.dao.BookmarkDao;

public interface BookmarkRepository extends CrudRepository<BookmarkDao, Long> {

}
