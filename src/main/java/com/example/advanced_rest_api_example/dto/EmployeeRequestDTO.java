package com.example.advanced_rest_api_example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeRequestDTO {

    @NotBlank(message = "Name darf nicht leer sein")
    @Size(min = 2, max = 50, message = "Name muss zwischen 2 und 50 Zeichen sein")
    private String name;

    @NotBlank(message = "Position darf nicht leer sein")
    private String position;

    public EmployeeRequestDTO(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}