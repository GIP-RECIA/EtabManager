/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.recia.manager.db.entities.personne;

import fr.recia.manager.db.entities.common.CleJointure;
import fr.recia.manager.db.entities.education.MEF;
import fr.recia.manager.db.entities.education.MappingEleveEnseignement;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {
    @Index(name = "siecleId", columnList = "siecleId"),
    @Index(name = "INE", columnList = "INE")
})
@Getter
@Setter
public class Eleve extends APersonne {

    /**
     * Identifiant sconet.
     */
    @Column(length = IntConst.I15)
    private String siecleId;
    /**
     * Identifiant National.
     */
    @Column(length = IntConst.I11)
    private String INE;
    /**
     * Vrai si l'élève est majeur, Faux sinon.
     */
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean majeur;
    /**
     * Vrai si l'élève est majeur anticipé, Faux sinon, null si non renseigné.
     */
    @Column(columnDefinition = "BIT")
    private Boolean majeurAnticipe;
    /**
     * Régime de l'élève.
     */
    @Column(length = IntConst.I128)
    private String regime;
    /**
     * Statut de l'élève.
     */
    @Column(length = IntConst.I128)
    private String statut;
    /**
     * Vrai si l'élève utilise les transports scolaire, Faux sinon, null si non renseigné.
     */
    @Column(columnDefinition = "BIT")
    private Boolean transport;
    /**
     * Vrai si l'élève est boursier, Faux sinon, null si non renseigné.
     */
    @Column(columnDefinition = "BIT")
    private Boolean boursier;

    /**
     * Relation unidirectionnelle.
     * MEF associé à l'élève.
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "mef_fk")
    private MEF mef;
    /**
     * Relation unidirectionnelle.
     * Liste des enseignements suivis par l'élève.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name = "eleves_enseignements", joinColumns = @JoinColumn(name = "ELEVE_ID", referencedColumnName = "ID"))
    private Set<MappingEleveEnseignement> enseignements = new HashSet<>();

    /**
     * Constructeur de l'objet Eleve.java.
     */
    public Eleve() {
        super();
        this.setCategorie(CategoriePersonne.Eleve);
    }

    /**
     * Constructeur de l'objet Eleve.java.
     *
     * @param anneeScolaire Année scolaire de validité de l'individu. Année à la rentrée de septembre.
     * @param cleJointure   Clé de jointure, identifiant unique fourni par les différentes sources,
     *                      mais unique uniquement pour le périmètre de la source.
     * @param cn            Nom canonique de la personne : NOM + Prénom usuels.
     * @param givenName     Prénom usuel.
     * @param sn            Nom d'usage.
     */
    public Eleve(final LocalDate anneeScolaire, final CleJointure cleJointure,
                 final String cn, final String givenName, final String sn) {
        super(anneeScolaire, CategoriePersonne.Eleve, cleJointure, cn, givenName, sn);
    }

}
