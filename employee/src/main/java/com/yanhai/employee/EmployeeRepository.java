package com.yanhai.employee;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/employee")
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, String> {

}
