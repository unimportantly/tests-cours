package com.example.test;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.test.employee.Employee;
import com.example.test.employee.EmployeeController;
import com.example.test.employee.EmployeeService;
import com.example.test.employee.dto.EmployeeDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;


@WebMvcTest(controllers = EmployeeController.class)
public class EmployeeControllerTest {

    /*
    simulate a component (a request for instance)
     */
    @Autowired
    private MockMvc mockMvc;

    /*
    create a fake service
     */
    @MockBean
    private EmployeeService employeeService;

    /*
    these 2 methods create an employee for testing purposes
     */
    private EmployeeDTO employeeDTO(){
        return new EmployeeDTO(1L, "jean", "email@email.com", new Date(), 1, 125f);
    }

    private EmployeeDTO employeeDTOUpdate(){
        return new EmployeeDTO(1L, "pierre", "email@email.com", new Date(), 1, 125f);
    }

    @Test
    public void testFindAllEmployees() throws Exception {
        this.mockMvc.perform(get("/employees"))//perform a get request on "employees"
                .andExpect(status().isOk());// tell it the expected result
    }

    @Test
    public void testFindAllEmployees2() throws Exception {
        this.mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // -> here we check if the list is empty?
        ;
    }

    @Test
    public void testCreateEmployee() throws Exception{
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        EmployeeDTO employeeDTO = this.employeeDTO();
        String body = json.toJson(employeeDTO);
        System.out.println(json);
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated()); //mocked the creation of an employee
    }

    /**
     * this is expected not to work & return a 404 error
     * @throws Exception
     */
    @Test
    public void testFindOneEmployeeWhereWrongIdOrInexistantEmployee() throws Exception{
        this.mockMvc.perform(get("/employees/1"))
                .andExpect(status().isNotFound());
        }

    @Test
    public void testFindOneEmployee() throws Exception{
        /*
        here we mock the service so that it'll return an employeeDTO by faking its existence in the db
         */
        EmployeeDTO employeeDTO = this.employeeDTO(); //create an employee object
        BDDMockito.given(employeeService.findOne(1L)) // create a mock of the DB(not really a mock, it
                // intercepts any communication that bounce from the service to the repo, then asks runs it
                // so here the employee isn't on the db, but it's here
                .willReturn(Optional.of(employeeDTO)); // tells it what the return will be
        /*
        the actual test of the route starts here
         */
        MvcResult result = this.mockMvc.perform(get("/employees/1")) // now points the correct route
                .andExpect(status().isOk())
                .andReturn(); // returns the resulting object and store it, so it can be used later
        Gson json = new GsonBuilder().create(); // create a gson to parse objects into json or vice versa
        // here we transform the json into a dto
        EmployeeDTO response = json.fromJson(result.getResponse().getContentAsString(), //tell it what to parse
                EmployeeDTO.class); // and the target class
        Assertions.assertEquals(response.getName(), "jean");
    }

    @Test
    public void testUpdateEmployee() throws Exception{
        EmployeeDTO employeeDTO = this.employeeDTO(); //create an employee object
        EmployeeDTO employeeDTO1 = this.employeeDTOUpdate();
        BDDMockito.given(employeeService.findOne(1L))
                .willReturn(Optional.of(employeeDTO));
        MvcResult result = this.mockMvc.perform(get("/employees/1")) // now points the correct route
                .andExpect(status().isOk())
                .andReturn(); // returns the resulting object
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create(); // create a gson to parse into an employeeDTO
        EmployeeDTO response = json.fromJson(result.getResponse().getContentAsString(), //tell it what to parse
                EmployeeDTO.class);

        response.setName("pierre");
        // transform the dto into json
        String bodyToSave = json.toJson(response);
        System.out.println(bodyToSave);
        BDDMockito.when(employeeService.save(any(EmployeeDTO.class))) /* when you call the save method with ANY
        object so long as it's and instance of EmployeeDTO.class it'll return the one we select.
        */
                .thenReturn(employeeDTO1);
        MvcResult mvcResult = this.mockMvc.perform(put("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bodyToSave))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult);
        EmployeeDTO finalBody = json.fromJson(mvcResult.getResponse().getContentAsString(),
                EmployeeDTO.class);

        System.out.println(finalBody);
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        Gson json = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String body = json.toJson(this.employeeDTO());
        this.mockMvc.perform(delete("/employees").contentType(MediaType.APPLICATION_JSON)
                .content(body)).andExpect(status().isOk());
    }
}
