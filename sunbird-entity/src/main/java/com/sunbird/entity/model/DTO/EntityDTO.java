package com.sunbird.entity.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityDTO<T> {
    private T entity;
}

