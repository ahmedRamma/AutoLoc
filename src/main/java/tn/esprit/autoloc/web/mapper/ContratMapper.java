package tn.esprit.autoloc.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.ContratUpdateRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;

import java.util.List;

/**
 * La réservation est résolue par le service ; les paiements sont convertis par {@link PaiementMapper}.
 */
@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final PaiementMapper paiementMapper;

    public Contrat toEntity(ContratRequest request) {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(request.dateSignature());
        contrat.setMontantTotal(request.montantTotal());
        contrat.setValide(request.valide());
        if (request.paiements() != null) {
            request.paiements().forEach(p -> {
                Paiement paiement = paiementMapper.toEntity(p);
                paiement.setContrat(contrat);
                contrat.getPaiements().add(paiement);
            });
        }
        return contrat;
    }

    public void updateEntity(ContratUpdateRequest request, Contrat contrat) {
        contrat.setDateSignature(request.dateSignature());
        contrat.setMontantTotal(request.montantTotal());
        contrat.setValide(request.valide());
    }

    public ContratResponse toResponse(Contrat contrat) {
        List<PaiementResponse> paiements = contrat.getPaiements().stream()
                .map(paiementMapper::toResponse)
                .toList();
        return new ContratResponse(contrat.getIdContrat(), contrat.getDateSignature(), contrat.getMontantTotal(),
                contrat.isValide(), contrat.getReservation().getIdReservation(), paiements);
    }
}
