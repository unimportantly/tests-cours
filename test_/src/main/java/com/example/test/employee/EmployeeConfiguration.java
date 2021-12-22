package com.example.test.employee;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeConfiguration {

    @Bean
    public EmployeeService employeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper){
        return new EmployeeService(employeeRepository, modelMapper);
    }
}
