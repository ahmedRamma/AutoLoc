package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrat est la racine de la composition Contrat <>-- Paiement :
 * les paiements sont créés et retirés uniquement à travers leur contrat.
 */
@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private static final String RESSOURCE = "Contrat";

    private final IContratRepository contratRepository;

    @Override
    @Transactional
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        if (contrat.getReservation() == null) {
            throw new IllegalArgumentException("La réservation du contrat est obligatoire");
        }
        validerMontant(contrat.getMontantTotal());
        contrat.getPaiements().forEach(paiement -> paiement.setContrat(contrat));
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return findOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional
    public Contrat update(Long id, Contrat contrat) {
        validerMontant(contrat.getMontantTotal());
        Contrat existant = findOrThrow(id);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        // ni l'identifiant, ni la réservation, ni la collection de paiements ne sont recopiés
        return contratRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESSOURCE, id);
        }
        // deleteById passe par le contexte de persistance : la cascade supprime aussi les paiements
        contratRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Contrat addPaiement(Long idContrat, Paiement paiement) {
        if (paiement.getIdPaiement() != null) {
            throw new IllegalArgumentException("Un nouveau paiement ne doit pas avoir d'identifiant");
        }
        validerMontant(paiement.getMontant());
        Contrat contrat = findOrThrow(idContrat);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional
    public Contrat removePaiement(Long idContrat, Long idPaiement) {
        Contrat contrat = findOrThrow(idContrat);
        boolean retire = contrat.getPaiements()
                .removeIf(paiement -> idPaiement.equals(paiement.getIdPaiement()));
        if (!retire) {
            throw new ResourceNotFoundException("Paiement", idPaiement);
        }
        // orphanRemoval = true : le paiement retiré de la liste est supprimé en base
        return contratRepository.save(contrat);
    }

    private Contrat findOrThrow(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESSOURCE, id));
    }

    private void validerMontant(BigDecimal montant) {
        if (montant != null && montant.signum() < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        }
    }
}
