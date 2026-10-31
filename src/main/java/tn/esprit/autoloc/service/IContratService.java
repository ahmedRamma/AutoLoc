package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;

import java.util.List;

public interface IContratService {

    List<Contrat> retrieveAllContrats();

    Contrat retrieveContrat(Long idContrat);

    Contrat addContrat(Contrat contrat);

    Contrat updateContrat(Long idContrat, Contrat contrat);

    void removeContrat(Long idContrat);

    Contrat addPaiementToContrat(Long idContrat, Paiement paiement);

    Contrat removePaiementFromContrat(Long idContrat, Long idPaiement);
}
