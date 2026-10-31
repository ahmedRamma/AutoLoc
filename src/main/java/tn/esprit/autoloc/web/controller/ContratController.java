package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.ContratUpdateRequest;
import tn.esprit.autoloc.web.dto.PaiementRequest;

import java.util.List;

@Tag(name = "Contrats", description = "Gestion des contrats")
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Lister les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats")
    @GetMapping
    public List<ContratResponse> findAll() {
        return contratService.findAll();
    }

    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ContratResponse findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @Operation(summary = "Créer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides : montant manquant ou négatif, paiement invalide"),
            @ApiResponse(responseCode = "404", description = "Ressource liée introuvable")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse create(@Valid @RequestBody ContratRequest request) {
        return contratService.create(request);
    }

    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable, ou ressource liée introuvable")
    })
    @PutMapping("/{id}")
    public ContratResponse update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
                         @Valid @RequestBody ContratUpdateRequest request) {
        return contratService.update(id, request);
    }

    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        contratService.deleteById(id);
    }

    @Operation(summary = "Ajouter un paiement à un contrat",
            description = "Le paiement est créé par cascade : c'est le contrat qui porte ses paiements.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement ajouté, contrat mis à jour"),
            @ApiResponse(responseCode = "400", description = "Paiement invalide"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @PostMapping("/{idContrat}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse addPaiement(@Parameter(description = "Identifiant du contrat") @PathVariable Long idContrat,
                                       @Valid @RequestBody PaiementRequest request) {
        return contratService.addPaiement(idContrat, request);
    }

    @Operation(summary = "Retirer un paiement d'un contrat",
            description = "Le paiement retiré est supprimé en base (orphanRemoval).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement retiré, contrat mis à jour"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable")
    })
    @DeleteMapping("/{idContrat}/paiements/{idPaiement}")
    public ContratResponse removePaiement(@Parameter(description = "Identifiant du contrat") @PathVariable Long idContrat,
                                          @Parameter(description = "Identifiant du paiement") @PathVariable Long idPaiement) {
        return contratService.removePaiement(idContrat, idPaiement);
    }
}
