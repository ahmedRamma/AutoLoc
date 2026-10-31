package tn.esprit.autoloc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vehicule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule implements Prototype<Vehicule> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(nullable = false, length = 50)
    private String marque;

    @Column(nullable = false, length = 50)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieVehicule categorie;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutVehicule statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement")
    )
    @Builder.Default
    private Set<Equipement> equipements = new HashSet<>();

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    /**
     * Patron Prototype : nouveau véhicule de même modèle, prêt à recevoir sa propre immatriculation.
     * <ul>
     *   <li>copiés : marque, modèle, catégorie, tarif journalier, agence et équipements ;</li>
     *   <li>non copiés : identifiant (attribué par la base), immatriculation (unique),
     *       réservations (historique propre au véhicule d'origine) ;</li>
     *   <li>le statut repart à {@link StatutVehicule#DISPONIBLE}.</li>
     * </ul>
     * L'agence et les équipements sont partagés (copie superficielle : ce sont des entités de référence),
     * mais l'ensemble des équipements est un nouvel objet {@link HashSet} : modifier les équipements de la copie
     * ne modifie pas ceux du véhicule d'origine.
     */
    @Override
    public Vehicule copier() {
        Vehicule copie = new Vehicule();
        copie.setMarque(marque);
        copie.setModele(modele);
        copie.setCategorie(categorie);
        copie.setTarifJournalier(tarifJournalier);
        copie.setStatut(StatutVehicule.DISPONIBLE);
        copie.setAgence(agence);
        copie.setEquipements(new HashSet<>(equipements));
        return copie;
    }
}