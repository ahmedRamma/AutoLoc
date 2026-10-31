package tn.esprit.autoloc.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.repository.IContratRepository;

import java.util.List;

/**
 * Contrat est la racine de la composition Contrat <>-- Paiement :
 * les paiements sont créés et retirés uniquement à travers leur contrat.
 */
@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> retrieveAllContrats() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat retrieveContrat(Long idContrat) {
        return contratRepository.findById(idContrat)
                .orElseThrow(() -> new EntityNotFoundException("Contrat introuvable : " + idContrat));
    }

    @Override
    @Transactional
    public Contrat addContrat(Contrat contrat) {
        contrat.setIdContrat(null);
        contrat.getPaiements().forEach(paiement -> paiement.setContrat(contrat));
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional
    public Contrat updateContrat(Long idContrat, Contrat contrat) {
        Contrat existant = retrieveContrat(idContrat);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return contratRepository.save(existant);
    }

    @Override
    @Transactional
    public void removeContrat(Long idContrat) {
        // delete() passe par le contexte de persistance : la cascade supprime aussi les paiements
        contratRepository.delete(retrieveContrat(idContrat));
    }

    @Override
    @Transactional
    public Contrat addPaiementToContrat(Long idContrat, Paiement paiement) {
        Contrat contrat = retrieveContrat(idContrat);
        paiement.setIdPaiement(null);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional
    public Contrat removePaiementFromContrat(Long idContrat, Long idPaiement) {
        Contrat contrat = retrieveContrat(idContrat);
        boolean retire = contrat.getPaiements()
                .removeIf(paiement -> idPaiement.equals(paiement.getIdPaiement()));
        if (!retire) {
            throw new EntityNotFoundException(
                    "Paiement " + idPaiement + " introuvable dans le contrat " + idContrat);
        }
        // orphanRemoval = true : le paiement retiré de la liste est supprimé en base
        return contratRepository.save(contrat);
    }
}
