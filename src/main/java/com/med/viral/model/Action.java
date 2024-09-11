package com.med.viral.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
public class Action {

    @Id
    private Integer id;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private String createdDate;
    @Enumerated
    private ActionType actionType;
    private String fieldName;
    private String oldVersion;
    private String newVersion;
}
