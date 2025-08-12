package com.example.advanced_rest_api_example.service;

import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.model.Employee;
import com.example.advanced_rest_api_example.repository.EmployeeRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class EmployeeServiceTest {

    @MockitoBean
    private EmployeeRepository repository;

    @Autowired
    private EmployeeService service;

    @Test
    void testGetById() {
        Employee emp = new Employee(1L, "John", "Editor");
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(emp));

        EmployeeResponseDTO dto = service.findById(1L);
        Assertions.assertEquals("John", dto.getName());
    }
}
