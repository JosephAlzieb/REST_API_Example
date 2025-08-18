package com.example.advanced_rest_api_example.repository;

import com.example.advanced_rest_api_example.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  Page<Employee> findAll(Pageable pageable);
  Page<Employee> findByPosition(String position, Pageable pageable);
//  public Optional<Employee> findById(Long id);
}
