package com.example.advanced_rest_api_example.service;

import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import com.example.advanced_rest_api_example.model.Employee;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmployeeService {

    private final Map<Long, Employee> employeeDB = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public List<Employee> getAll(Optional<String> positionFilter) {
        if (positionFilter.isPresent()) {
            String pos = positionFilter.get().toLowerCase();
            List<Employee> filtered = new ArrayList<>();
            for (Employee e : employeeDB.values()) {
                if (e.getPosition().toLowerCase().contains(pos)) {
                    filtered.add(e);
                }
            }
            return filtered;
        }
        return new ArrayList<>(employeeDB.values());
    }

    public Employee getById(Long id) {
        Employee emp = employeeDB.get(id);
        if (emp == null) throw new ResourceNotFoundException("Mitarbeiter nicht gefunden: " + id);
        return emp;
    }

    public Employee create(Employee emp) {
        emp.setId(idCounter.incrementAndGet());
        employeeDB.put(emp.getId(), emp);
        return emp;
    }

    public Employee update(Long id, Employee emp) {
        if (!employeeDB.containsKey(id))
            throw new ResourceNotFoundException("Mitarbeiter nicht gefunden: " + id);
        emp.setId(id);
        employeeDB.put(id, emp);
        return emp;
    }

    public void delete(Long id) {
        if (!employeeDB.containsKey(id))
            throw new ResourceNotFoundException("Mitarbeiter nicht gefunden: " + id);
        employeeDB.remove(id);
    }
}
