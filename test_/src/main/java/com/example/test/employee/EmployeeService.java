package com.example.test.employee;

import com.example.test.employee.dto.EmployeeDTO;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EmployeeService {

    private EmployeeRepository employeeRepository;
    private ModelMapper modelMapper;

    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * returns a list of employees
     * @return List<Employee>
     */
    public List<EmployeeDTO> findAll(){
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employee ->
            employeeDTOList.add(modelMapper.map(employee, EmployeeDTO.class)));
        return employeeDTOList;
    }

    /**
     * finds an employee through their id
     * @param id Long
     * @return Optional<EmployeeDTO>
     */
    public Optional<EmployeeDTO> findOne(final Long id) throws NoSuchElementException {
        Optional<Employee> optionalEmployee = this.employeeRepository.findById(id);
        // returns the employee as an optional dto, the get throws an exception if nothing is found
            return Optional.of(this.modelMapper.map(optionalEmployee.get(), EmployeeDTO.class));
    }

    /**
     * persists an employee on the database
     * @param employeeDTO EmployeeDTO
     * @return EmployeeDTO
     */
    public EmployeeDTO save(EmployeeDTO employeeDTO){
        return this.modelMapper.map(this.employeeRepository.save
                (this.modelMapper.map(employeeDTO, Employee.class)), EmployeeDTO.class);
    }

    /**
     * deletes an employee
     * @param employeeDTO EmployeeDTO
     */
    public void delete(EmployeeDTO employeeDTO){
        this.employeeRepository.delete(this.modelMapper.map(employeeDTO, Employee.class));
    }
}
