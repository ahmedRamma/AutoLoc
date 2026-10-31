package tn.esprit.autoloc.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;

import java.util.Comparator;
import java.util.List;

/**
 * L'agence et les équipements sont résolus par le service à partir de leurs identifiants.
 */
@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final EquipementMapper equipementMapper;

    public Vehicule toEntity(VehiculeRequest request) {
        Vehicule vehicule = new Vehicule();
        updateEntity(request, vehicule);
        return vehicule;
    }

    public void updateEntity(VehiculeRequest request, Vehicule vehicule) {
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setCategorie(request.categorie());
        vehicule.setTarifJournalier(request.tarifJournalier());
        vehicule.setStatut(request.statut());
    }

    public VehiculeResponse toResponse(Vehicule vehicule) {
        List<EquipementResponse> equipements = vehicule.getEquipements().stream()
                .map(equipementMapper::toResponse)
                .sorted(Comparator.comparing(EquipementResponse::idEquipement))
                .toList();
        return new VehiculeResponse(vehicule.getIdVehicule(), vehicule.getImmatriculation(), vehicule.getMarque(),
                vehicule.getModele(), vehicule.getCategorie(), vehicule.getTarifJournalier(), vehicule.getStatut(),
                vehicule.getAgence().getIdAgence(), vehicule.getAgence().getNom(), equipements);
    }
}
