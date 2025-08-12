package com.example.advanced_rest_api_example.model;
public class Employee {

    private Long id;
    private String name;
    private String position;

    public Employee(Long id, String name, String position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Employee() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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