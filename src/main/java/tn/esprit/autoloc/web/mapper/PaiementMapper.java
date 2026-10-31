package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;

@Component
public class PaiementMapper {

    public Paiement toEntity(PaiementRequest request) {
        Paiement paiement = new Paiement();
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setModePaiement(request.modePaiement());
        return paiement;
    }

    public PaiementResponse toResponse(Paiement paiement) {
        return new PaiementResponse(paiement.getIdPaiement(), paiement.getMontant(), paiement.getDatePaiement(),
                paiement.getModePaiement(), paiement.getContrat().getIdContrat());
    }
}
