package com.example.advanced_rest_api_example.service;

import com.example.advanced_rest_api_example.dto.EmployeeRequestDTO;
import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import com.example.advanced_rest_api_example.logging.HasLogger;
import com.example.advanced_rest_api_example.mapper.EmployeeMapper;
import com.example.advanced_rest_api_example.model.Employee;
import com.example.advanced_rest_api_example.repository.EmployeeRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements HasLogger {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Page<EmployeeResponseDTO> findAll(Optional<String> position, Pageable pageable) {
        getLogger().info("Alle Mitarbeiter skip: {}, limit: {}, sort: {}", pageable.getOffset(), pageable.getPageSize(), pageable.getSort());
        return repository.findAll(position, pageable)
            .map(mapper::toDTO);
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
