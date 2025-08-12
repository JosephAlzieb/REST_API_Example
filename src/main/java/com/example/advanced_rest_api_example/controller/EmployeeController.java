package com.example.advanced_rest_api_example.controller;

import com.example.advanced_rest_api_example.dto.EmployeeRequestDTO;
import com.example.advanced_rest_api_example.dto.EmployeeResponseDTO;
import com.example.advanced_rest_api_example.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee API", description = "CRUD operations for employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @Operation(summary = "Alle Mitarbeiter abrufen", description = "Optional nach Position filtern")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreiche Abfrage"),
            @ApiResponse(responseCode = "500", description = "Interner Serverfehler")
    })
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAll(
            @Parameter(description = "Optionaler Positionsfilter")
            @RequestParam Optional<String> position) {
        return ResponseEntity.ok(service.findAll(position));
    }

    @Operation(summary = "Mitarbeiter per ID abrufen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter gefunden"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Neuen Mitarbeiter erstellen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mitarbeiter erfolgreich erstellt"),
            @ApiResponse(responseCode = "400", description = "Validierungsfehler")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeResponseDTO> create(
            @Parameter(description = "Mitarbeiter Daten", required = true)
            @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO created = service.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Mitarbeiter aktualisieren")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mitarbeiter erfolgreich aktualisiert"),
            @ApiResponse(responseCode = "400", description = "Validierungsfehler"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> update(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id,
            @Parameter(description = "Aktualisierte Mitarbeiter Daten", required = true)
            @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updated = service.update(id, dto);
        return ResponseEntity.status(200).body(updated);
    }

    @Operation(summary = "Mitarbeiter löschen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mitarbeiter erfolgreich gelöscht"),
            @ApiResponse(responseCode = "404", description = "Mitarbeiter nicht gefunden")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID des Mitarbeiters", required = true)
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
