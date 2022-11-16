package com.sunbird.entity.model;

import java.util.List;
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
public class Notification {

    private String mode;
    private String deliveryType;
    private EmailConfig config;
    private List<String> ids;
    private Template template;

}