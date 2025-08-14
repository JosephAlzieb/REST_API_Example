package com.example.advanced_rest_api_example.repository;

import com.example.advanced_rest_api_example.exception.ResourceNotFoundException;
import com.example.advanced_rest_api_example.model.Employee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepository {

  private final Map<Long, Employee> employeeDB = new HashMap<>();
  private final AtomicLong idCounter = new AtomicLong();

  public Page<Employee> findAll(Optional<String> positionFilter, Pageable pageable) {
    List<Employee> employeeList = null;
    if (positionFilter.isPresent()) {
      String pos = positionFilter.get().toLowerCase();
      List<Employee> filtered = new ArrayList<>();
      for (Employee e : employeeDB.values()) {
        if (e.getPosition().toLowerCase().contains(pos)) {
          filtered.add(e);
        }
      }
      employeeList = filtered;
    } else {
      employeeList = employeeDB.values().stream().toList();
    }

    List<Employee> employeePage = employeeList.stream()
        .skip(pageable.getOffset())
        .limit(pageable.getPageSize())
        .toList();

    Page<Employee> page = new PageImpl(employeePage, pageable, employeeList.size());
    return page;
  }

  public Optional<Employee> findById(Long id) {
    Employee emp = employeeDB.get(id);
    return Optional.of(emp);
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
