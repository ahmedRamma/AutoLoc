package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;

/**
 * L'agence est résolue par le service à partir de {@code idAgence}.
 */
@Component
public class EmployeMapper {

    public Employe toEntity(EmployeRequest request) {
        Employe employe = new Employe();
        updateEntity(request, employe);
        return employe;
    }

    public void updateEntity(EmployeRequest request, Employe employe) {
        employe.setNom(request.nom());
        employe.setPrenom(request.prenom());
        employe.setRole(request.role());
    }

    public EmployeResponse toResponse(Employe employe) {
        return new EmployeResponse(employe.getIdEmploye(), employe.getNom(), employe.getPrenom(), employe.getRole(),
                employe.getAgence().getIdAgence(), employe.getAgence().getNom());
    }
}
