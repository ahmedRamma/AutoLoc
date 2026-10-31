package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;

import java.util.List;

public interface IEmployeService {

    EmployeResponse create(EmployeRequest request);

    EmployeResponse findById(Long id);

    List<EmployeResponse> findAll();

    EmployeResponse update(Long id, EmployeRequest request);

    void deleteById(Long id);
}
