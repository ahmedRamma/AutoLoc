package tn.esprit.autoloc.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> retrieveAllVehicules() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule retrieveVehicule(Long idVehicule) {
        return findVehiculeOrThrow(idVehicule);
    }

    private Vehicule findVehiculeOrThrow(Long idVehicule) {
        return vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new EntityNotFoundException("Véhicule introuvable : " + idVehicule));
    }

    @Override
    @Transactional
    public Vehicule addVehicule(Vehicule vehicule) {
        vehicule.setIdVehicule(null);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    @Transactional
    public Vehicule updateVehicule(Long idVehicule, Vehicule vehicule) {
        Vehicule existant = findVehiculeOrThrow(idVehicule);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        if (vehicule.getAgence() != null) {
            existant.setAgence(vehicule.getAgence());
        }
        return vehiculeRepository.save(existant);
    }

    @Override
    @Transactional
    public void removeVehicule(Long idVehicule) {
        if (!vehiculeRepository.existsById(idVehicule)) {
            throw new EntityNotFoundException("Véhicule introuvable : " + idVehicule);
        }
        vehiculeRepository.deleteById(idVehicule);
    }
}
