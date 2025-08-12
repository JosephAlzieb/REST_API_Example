package com.example.advanced_rest_api_example.mapper;

import com.example.advanced_rest_api_example.dto.EmployeeRequestDTO;
import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.model.Employee;
import org.springframework.stereotype.Component;

/**
 * eventuell MapStruct nutzen - (automatisierte Konvertierung Controller ↔ DTO ↔ Entity)
 */
@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDTO dto) {
        if (dto == null) return null;
        Employee emp = new Employee();
        emp.setName(dto.getName());
        emp.setPosition(dto.getPosition());
        return emp;
    }

    public EmployeeResponseDTO toDTO(Employee emp) {
        if (emp == null) return null;
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(emp.getId());
        dto.setName(emp.getName());
        dto.setPosition(emp.getPosition());
        return dto;
    }
}
