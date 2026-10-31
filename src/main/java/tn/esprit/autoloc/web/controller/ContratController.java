package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;

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
    public List<Contrat> findAll() {
        return contratService.findAll();
    }

    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public Contrat findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @Operation(summary = "Créer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides : réservation manquante ou montant négatif, ou identifiant fourni")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contrat create(@RequestBody Contrat contrat) {
        return contratService.create(contrat);
    }

    @Operation(summary = "Modifier un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @PutMapping("/{id}")
    public Contrat update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
                         @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
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
            @ApiResponse(responseCode = "400", description = "Montant négatif ou identifiant fourni"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @PostMapping("/{idContrat}/paiements")
    @ResponseStatus(HttpStatus.CREATED)
    public Contrat addPaiement(@Parameter(description = "Identifiant du contrat") @PathVariable Long idContrat,
                               @RequestBody Paiement paiement) {
        return contratService.addPaiement(idContrat, paiement);
    }

    @Operation(summary = "Retirer un paiement d'un contrat",
            description = "Le paiement retiré est supprimé en base (orphanRemoval).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement retiré, contrat mis à jour"),
            @ApiResponse(responseCode = "404", description = "Contrat ou paiement introuvable")
    })
    @DeleteMapping("/{idContrat}/paiements/{idPaiement}")
    public Contrat removePaiement(@Parameter(description = "Identifiant du contrat") @PathVariable Long idContrat,
                                  @Parameter(description = "Identifiant du paiement") @PathVariable Long idPaiement) {
        return contratService.removePaiement(idContrat, idPaiement);
    }
}
