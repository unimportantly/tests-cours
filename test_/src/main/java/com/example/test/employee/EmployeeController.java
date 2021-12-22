package com.example.test.employee;

import com.example.test.employee.dto.EmployeeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> findAll(){
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> findById(@PathVariable Long id){
        try{
            return ResponseEntity.ok(this.employeeService.findOne(id).get());
        }
        catch(NoSuchElementException e) {
            return ResponseEntity.notFound().header(e.getMessage()).build();
        }
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> save(@RequestBody EmployeeDTO employeeDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.employeeService.save(employeeDTO));
    }

    @PutMapping
    public ResponseEntity<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO){
        return ResponseEntity.ok(this.employeeService.save(employeeDTO));
    }

    @DeleteMapping
    public ResponseEntity<Boolean> delete(@RequestBody EmployeeDTO employeeDTO){
        this.employeeService.delete(employeeDTO);
        return ResponseEntity.ok(true);
    }
}
