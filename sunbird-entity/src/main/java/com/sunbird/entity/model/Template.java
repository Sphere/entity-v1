package com.sunbird.entity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template {

    private String data;
    private String id;
    private TemplateParams params;
}