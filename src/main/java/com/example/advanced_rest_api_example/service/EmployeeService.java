package com.example.advanced_rest_api_example.service;

import com.example.advanced_rest_api_example.dto.EmployeeRequestDTO;
import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import com.example.advanced_rest_api_example.logging.HasLogger;
import com.example.advanced_rest_api_example.mapper.EmployeeMapper;
import com.example.advanced_rest_api_example.model.Employee;
import com.example.advanced_rest_api_example.repository.EmployeeRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements HasLogger {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeResponseDTO> findAll(Optional<String> position) {
        return repository.findAll(position).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    public EmployeeResponseDTO findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    public EmployeeResponseDTO create(EmployeeRequestDTO emp) {
        Employee e = mapper.toEntity(emp);
        EmployeeResponseDTO saved = mapper.toDTO(repository.create(e));
        getLogger().info("Mitarbeiter gespeichert mit ID: {}", saved.getId());
        return saved;
    }

    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO emp) {
        Employee e = mapper.toEntity(emp);
        EmployeeResponseDTO saved = mapper.toDTO(repository.update(id, e));
        getLogger().info("Mitarbeiter aktualisiert mit ID: {}", saved.getId());
        return saved;
    }

    public void delete(Long id) {
        repository.delete(id);
        getLogger().info("Mitarbeiter mit ID {} gelöscht", id);
    }
}
