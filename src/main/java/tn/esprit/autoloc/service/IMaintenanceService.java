package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;

import java.util.List;

public interface IMaintenanceService {

    MaintenanceResponse create(MaintenanceRequest request);

    MaintenanceResponse findById(Long id);

    List<MaintenanceResponse> findAll();

    MaintenanceResponse update(Long id, MaintenanceRequest request);

    void deleteById(Long id);
}
