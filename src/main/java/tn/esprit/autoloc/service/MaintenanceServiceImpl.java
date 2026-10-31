package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;
import tn.esprit.autoloc.web.mapper.MaintenanceMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private static final String RESSOURCE = "Maintenance";

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Override
    @Transactional
    public MaintenanceResponse create(MaintenanceRequest request) {
        if (request.dateFin() != null && request.dateFin().isBefore(request.dateDebut())) {
            throw new IllegalArgumentException("La date de fin de la maintenance ne peut pas être antérieure à la date de début");
        }
        Maintenance maintenance = maintenanceMapper.toEntity(request);
        maintenance.setVehicule(findVehicule(request.idVehicule()));
        return maintenanceMapper.toResponse(maintenanceRepository.save(maintenance));
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceResponse findById(Long id) {
        return maintenanceMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceResponse> findAll() {
        return maintenanceRepository.findAll().stream()
                .map(maintenanceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public MaintenanceResponse update(Long id, MaintenanceRequest request) {
        if (request.dateFin() != null && request.dateFin().isBefore(request.dateDebut())) {
            throw new IllegalArgumentException("La date de fin de la maintenance ne peut pas être antérieure à la date de début");
        }
        Maintenance maintenance = findOrThrow(id);
        maintenanceMapper.updateEntity(request, maintenance);
        maintenance.setVehicule(findVehicule(request.idVehicule()));
        return maintenanceMapper.toResponse(maintenanceRepository.save(maintenance));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESSOURCE, id);
        }
        maintenanceRepository.deleteById(id);
    }

    private Maintenance findOrThrow(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESSOURCE, id));
    }

    private Vehicule findVehicule(Long idVehicule) {
        return vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));
    }
}
