package com.example.test.employee.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {


    private Long id;
    private String name;
    private String email;
    private Date birthday;
    private int gender;
    private float salary;
}
