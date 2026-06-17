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

package fr.recia.manager.db.entities.structure;

import fr.recia.manager.db.entities.common.AbstractTimedEntity;
import fr.recia.manager.db.entities.common.Adresse;
import fr.recia.manager.db.entities.common.CleJointure;
import fr.recia.manager.db.entities.common.Mail;
import fr.recia.manager.db.entities.common.Telephone;
import fr.recia.manager.db.entities.gestion.Incertain;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.enums.EtatAlim;
import fr.recia.manager.db.utils.IntConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "astructure", indexes = {
    @Index(name = "AStructCleJointure", columnList = "cle, source")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cle", "source"})
})
@Getter
@Setter
@NoArgsConstructor
public abstract class AStructure extends AbstractTimedEntity {

    /**
     * Etat de l'entité valide, bloqué ou supprimé.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I25)
    private Etat etat;
    /**
     * Etat d'alimentation de l'entité Non_alimenté, Bascule, Alimenté.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I15)
    private EtatAlim etatAlim;
    /**
     * Année scolaire de validité de l'individu.
     * Année à la rentrée de septembre.
     */
    @Temporal(TemporalType.DATE)
    private Date anneeScolaire;
    /**
     * Adresse de la structure.
     */
    @Embedded
    private Adresse adresse;
    /**
     * Catégorie de la structure. Etablissement, Entreprise ...
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I20)
    private CategorieStructure categorie;
    /**
     * Clé de jointure, identifiant unique généré par les différentes sources,
     * mais unique uniquement pour le périmètre de la source.
     */
    @Embedded
    @Column(unique = true, nullable = false)
    private CleJointure cleJointure;
    /**
     * Adresse email de la structure.
     */
    private String mail;
    /**
     * Nom obligatoire de la structure.
     */
    @Column(nullable = false)
    private String nom;
    /**
     * Nom court.
     */
    private String nomCourt;
    /**
     * Numéro de SIRET/SIREN de la structure.
     */
    @Column(unique = true, length = IntConst.I20)
    private String siren;
    /**
     * Adresse du site web de la structure.
     */
    private String siteWeb;
    /**
     * Nom du layout owner de l'établissement permettant de définir le skin.
     */
    private String modeleLogin;
    /**
     * Url du logo.
     */
    private String logo;

    /**
     * Relation unidirectionnelle.
     * Personne étant le contact de la structure.
     */
    @Fetch(FetchMode.JOIN)
    @OneToOne(fetch = FetchType.LAZY)
    private APersonne contact;
    /**
     * Relation unidirectionnelle.
     * Personne étant le responsable de la structure.
     */
    @Fetch(FetchMode.JOIN)
    @OneToOne(fetch = FetchType.LAZY)
    private APersonne responsable;
    /**
     * Relation bidirectionnelle.
     * Type de la structure (Lycée Agricole , L.P., ... connus de MENESR)
     */
    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "type_structure_fk")
    private TypeStructure type;
    /**
     * Relation bidirectionnelle.
     * Liste des personnes rattachées à cette structure.
     */
    @OneToMany(mappedBy = "structRattachement", fetch = FetchType.LAZY)
    private Set<APersonne> personnesRattachement = new HashSet<>();
    /**
     * Relation unidirectionnelle.
     * Liste des numéros de téléphones de la structure.
     */
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinColumn(name = "astructure_fk")
    private Set<Telephone> telephones = new HashSet<>();
    /**
     * Relation unidirectionnelle.
     * Liste des adresses mail de la structure.
     */
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "astructure_fk")
    private Set<Mail> mails = new HashSet<>();
    /**
     * Relation bidirectionnelle.
     * Liste des attributs incertains.
     */
    @OneToMany(mappedBy = "incertainStruct", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Incertain> incertains = new HashSet<>();

    /**
     * Constructeur de l'objet AStructure.java.
     *
     * @param categorie   Categorie de la structure. Etablissement, Entreprise ...
     * @param nom         Nom unique de la stucture.
     * @param siren       Numéro de SIRET/SIREN unique de la structure.
     * @param cleJointure Clé de jointure unique de la structure.
     */
    public AStructure(final CategorieStructure categorie, final String nom,
                      final String siren, final CleJointure cleJointure) {
        super();
        this.categorie = categorie;
        this.nom = nom;
        this.siren = siren;
        this.cleJointure = cleJointure;
        this.etat = Etat.Valide;
    }

}
