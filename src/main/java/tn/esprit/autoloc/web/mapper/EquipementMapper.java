package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

@Component
public class EquipementMapper {

    public Equipement toEntity(EquipementRequest request) {
        Equipement equipement = new Equipement();
        updateEntity(request, equipement);
        return equipement;
    }

    public void updateEntity(EquipementRequest request, Equipement equipement) {
        equipement.setLibelle(request.libelle());
    }

    public EquipementResponse toResponse(Equipement equipement) {
        return new EquipementResponse(equipement.getIdEquipement(), equipement.getLibelle());
    }
}
