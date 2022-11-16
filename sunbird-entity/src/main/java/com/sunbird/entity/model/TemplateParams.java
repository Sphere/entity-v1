package com.sunbird.entity.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateParams {

    public TemplateParams(String nodeName, String nodeId, String type) {
        this.nodeName = nodeName;
        this.nodeId = nodeId;
        this.type = type;
    }

    public String message;
    public String nodeId;
    public String messageTitle;
    public String type;
    public String rejectionMessage;
    public List<String> sendTo;
    public String nodeName;
    public String nodePath;
    public String subject;
    public String authToken;
}