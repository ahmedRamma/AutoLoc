package tn.esprit.autoloc.service;

import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.ContratUpdateRequest;
import tn.esprit.autoloc.web.dto.PaiementRequest;

import java.util.List;

public interface IContratService {

    ContratResponse create(ContratRequest request);

    ContratResponse findById(Long id);

    List<ContratResponse> findAll();

    ContratResponse update(Long id, ContratUpdateRequest request);

    void deleteById(Long id);

    ContratResponse addPaiement(Long idContrat, PaiementRequest request);

    ContratResponse removePaiement(Long idContrat, Long idPaiement);
}
