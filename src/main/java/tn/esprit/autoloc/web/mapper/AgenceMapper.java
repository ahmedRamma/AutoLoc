package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;

@Component
public class AgenceMapper {

    public Agence toEntity(AgenceRequest request) {
        Agence agence = new Agence();
        updateEntity(request, agence);
        return agence;
    }

    public void updateEntity(AgenceRequest request, Agence agence) {
        agence.setNom(request.nom());
        agence.setVille(request.ville());
        agence.setAdresse(request.adresse());
        agence.setTelephone(request.telephone());
    }

    public AgenceResponse toResponse(Agence agence) {
        return new AgenceResponse(agence.getIdAgence(), agence.getNom(), agence.getVille(),
                agence.getAdresse(), agence.getTelephone());
    }
}
