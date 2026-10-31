package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.ContratUpdateRequest;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.web.mapper.PaiementMapper;

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
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional
    public ContratResponse create(ContratRequest request) {
        Contrat contrat = contratMapper.toEntity(request);
        contrat.setReservation(reservationRepository.findById(request.idReservation())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", request.idReservation())));
        // cascade PERSIST : les paiements du contrat sont enregistrés avec lui
        return contratMapper.toResponse(contratRepository.save(contrat));
    }

    @Override
    @Transactional(readOnly = true)
    public ContratResponse findById(Long id) {
        return contratMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratResponse> findAll() {
        return contratRepository.findAll().stream()
                .map(contratMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ContratResponse update(Long id, ContratUpdateRequest request) {
        Contrat contrat = findOrThrow(id);
        // ni la réservation, ni la collection de paiements ne sont modifiées ici
        contratMapper.updateEntity(request, contrat);
        return contratMapper.toResponse(contratRepository.save(contrat));
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
    public ContratResponse addPaiement(Long idContrat, PaiementRequest request) {
        Contrat contrat = findOrThrow(idContrat);
        Paiement paiement = paiementMapper.toEntity(request);
        paiement.setContrat(contrat);
        contrat.getPaiements().add(paiement);
        // flush : la cascade PERSIST insère le paiement et lui attribue son identifiant
        contratRepository.flush();
        return contratMapper.toResponse(contrat);
    }

    @Override
    @Transactional
    public ContratResponse removePaiement(Long idContrat, Long idPaiement) {
        Contrat contrat = findOrThrow(idContrat);
        boolean retire = contrat.getPaiements()
                .removeIf(paiement -> idPaiement.equals(paiement.getIdPaiement()));
        if (!retire) {
            throw new ResourceNotFoundException("Paiement", idPaiement);
        }
        // orphanRemoval = true : le paiement retiré de la liste est supprimé en base
        contratRepository.flush();
        return contratMapper.toResponse(contrat);
    }

    private Contrat findOrThrow(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESSOURCE, id));
    }
}
