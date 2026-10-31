package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;

/**
 * Le véhicule est résolu par le service à partir de {@code idVehicule}.
 */
@Component
public class MaintenanceMapper {

    public Maintenance toEntity(MaintenanceRequest request) {
        Maintenance maintenance = new Maintenance();
        updateEntity(request, maintenance);
        return maintenance;
    }

    public void updateEntity(MaintenanceRequest request, Maintenance maintenance) {
        maintenance.setDateDebut(request.dateDebut());
        maintenance.setDateFin(request.dateFin());
        maintenance.setDescription(request.description());
    }

    public MaintenanceResponse toResponse(Maintenance maintenance) {
        return new MaintenanceResponse(maintenance.getIdMaintenance(), maintenance.getDateDebut(),
                maintenance.getDateFin(), maintenance.getDescription(),
                maintenance.getVehicule().getIdVehicule(), maintenance.getVehicule().getImmatriculation());
    }
}
