package com.example.advanced_rest_api_example.controller;

import com.example.advanced_rest_api_example.dto.EmployeeRequestDTO;
import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.mapper.EmployeeMapper;
import com.example.advanced_rest_api_example.model.Employee;
import com.example.advanced_rest_api_example.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeMapper mapper;

    public EmployeeController(EmployeeService service, EmployeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Alle Mitarbeiter abrufen", description = "Optional nach Position filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreiche Abfrage"),
            @ApiResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @GetMapping
    public List<EmployeeResponseDTO> getAll(
            @Parameter(description = "Optionaler Positionsfilter")
            @RequestParam Optional<String> position) {
        return service.getAll(position).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Mitarbeiter per ID abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter gefunden"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @GetMapping("/{id}")
    public EmployeeResponseDTO getById(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id) {
        return mapper.toDTO(service.getById(id));
    }

    @Operation(summary = "Neuen Mitarbeiter erstellen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mitarbeiter erfolgreich erstellt"),
            @ApiResponse(responseCode = "400", description = "Validierungsfehler")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(
            @Parameter(description = "Mitarbeiter Daten", required = true)
            @Valid @RequestBody EmployeeRequestDTO dto) {
        Employee emp = mapper.toEntity(dto);
        return mapper.toDTO(service.create(emp));
    }

    @Operation(summary = "Mitarbeiter aktualisieren")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Validierungsfehler"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @PutMapping("/{id}")
    public EmployeeResponseDTO update(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id,
            @Parameter(description = "Aktualisierte Mitarbeiter Daten", required = true)
            @Valid @RequestBody EmployeeRequestDTO dto) {
        Employee emp = mapper.toEntity(dto);
        return mapper.toDTO(service.update(id, emp));
    }

    @Operation(summary = "Mitarbeiter löschen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mitarbeiter erfolgreich gelöscht"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id) {
        service.delete(id);
    }
}
