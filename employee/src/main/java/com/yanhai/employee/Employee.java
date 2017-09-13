package com.yanhai.employee;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author yanhai
 */
@Data
@Document
public class Employee {

    @Id
    private String id;

    private String name;

    private Gender gender;

    private Integer age;

    private String description;
}
