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

import fr.recia.manager.db.entities.common.AbstractTimedEntity;
import fr.recia.manager.db.entities.common.Adresse;
import fr.recia.manager.db.entities.common.CleJointure;
import fr.recia.manager.db.entities.common.ExternalId;
import fr.recia.manager.db.entities.common.Mail;
import fr.recia.manager.db.entities.common.Telephone;
import fr.recia.manager.db.entities.fonction.AFonction;
import fr.recia.manager.db.entities.gestion.Incertain;
import fr.recia.manager.db.entities.groupe.MappingAGroupeAPersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Civilite;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.enums.ForceEtat;
import fr.recia.manager.db.enums.Sexe;
import fr.recia.manager.db.utils.IntConst;
import fr.recia.manager.ldap.SubjectKey;
import fr.recia.manager.ldap.enums.SubjectType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "apersonne", indexes = {
    @Index(name = "APersCleJointure", columnList = "cle, source"),
    @Index(name = "uid_index", columnList = "uid"),
    @Index(name = "uuid_index", columnList = "uuid")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cle", "source"})
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public abstract class APersonne extends AbstractTimedEntity {

    /**
     * Etat de l'entité valide, bloqué ou supprimé.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I25)
    private Etat etat;
    /**
     * Année scolaire de validité de l'individu. Année à la rentrée de septembre.
     */
    @Temporal(TemporalType.DATE)
    private Date anneeScolaire;
    /**
     * Catégorie de la personne : Eleve, Enseignant ...
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I40)
    private CategoriePersonne categorie;
    /**
     * Adresse de la personne.
     */
    @Embedded
    private Adresse adresse;
    /**
     * Civilité : M, Mme, Mlle.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I4)
    private Civilite civilite;
    /**
     * Clé de jointure, identifiant unique généré par les différentes sources,
     * mais unique uniquement pour le périmètre de la source.
     */
    @Embedded
    @Column(unique = true, nullable = false)
    private CleJointure cleJointure;
    /**
     * Nom canonique de la personne : NOM + Prénom usuels.
     */
    private String cn;
    /**
     * Date de naissance.
     */
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
    /**
     * Nom et prénom accentué.
     */
    private String displayName;
    /**
     * Adresse e-mail.
     */
    private String email;
    /**
     * Prénom usuel.
     */
    private String givenName;
    /**
     * Numéro de bureau.
     */
    @Column(length = IntConst.I40)
    private String numBureau;
    /**
     * Password généré.
     */
    private String password;
    /**
     * Password SambaNT.
     */
    private String sambaNTPassword;
    /**
     * Nom patronymique.
     */
    private String patronyme;
    /**
     * Photo de l'individu, décrit par un url.
     */
    private String photo;
    /**
     * Sexe de la personne : F, M.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "CHAR")
    private Sexe sexe;
    /**
     * Nom d'usage.
     */
    private String sn;
    /**
     * Titre de la personne.
     */
    @Column(length = IntConst.I80)
    private String titre;
    /**
     * Identifiant interne à l'ENT.
     */
    @Column(unique = true, length = IntConst.I8)
    // INDEX
    private String uid;
    /**
     * Identifiant externe GARPersonIdentifiant.
     */
    @Column(unique = true, nullable = false, length = IntConst.I36)
    // INDEX
    private String uuid;
    // ajout spécifique à la gestion des comptes
    /**
     * Adresse e-mail personnelle.
     */
    private String emailPersonnel;
    /**
     * Flag pour indiquer la préférence du mail personnel. Par défaut à false.
     */
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean doForward;
    /**
     * Sauvegarde de la date de validation de la charte.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date validationCharte;
    /**
     * Flag pour indiquer que le compte est placé en liste rouge. Par défaut à false.
     */
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean listeRouge;
    /**
     * Pour forcer l'état d'un compte : ex suppression avant les 20 jours ou réactivation d'un compte supprimé (à voir).
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I25)
    private ForceEtat forceEtat;
    /**
     * Clé EduConnect pour forcer l'association.
     */
    @Column(length = IntConst.I100)
    private String idEduConnect;
    /**
     *
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;
    /**
     *
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSourceModification;
    /**
     * Relation bidirectionnelle.
     * Login de l'individu généré par l'ENT.
     */
    @OneToOne(mappedBy = "apersonneLogin")
    private Login login;
    /**
     * Relation bidirectionnelle.
     * Alias remplaçant le login, personnalisé par l'individu.
     */
    @OneToOne(mappedBy = "apersonneAlias")
    private Login alias;
    /**
     * Relation bidirectionnelle.
     * Liste des anciens alias de l'individu qui ne sont plus utilisés.
     */
    @OneToMany(mappedBy = "apersonneOldAlias", fetch = FetchType.LAZY)
    private Collection<Login> oldAlias;
    /**
     * Relation bidirectionnelle.
     * Liste des fonctions d'une personne, uniquement les fonctions dans une structure, pas les fonctions de groupes.
     * Profils, classes, groupes, role applicatif
     */
    @OneToMany(mappedBy = "personne", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<AFonction> fonctions = new HashSet<>();
    /**
     * Relation bidirectionnelle.
     */
    @OneToMany(mappedBy = "pk.personne", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<MappingAGroupeAPersonne> groupes = new HashSet<>();
    /**
     * Relation avec type primitif.
     * Liste des prénoms.
     */
    @ElementCollection
    @Column(length = 70)
    private Set<String> prenoms = new HashSet<>();
    /**
     * Relation unidirectionnelle.
     * Structure à laquelle la personne est rattachée administrativement.
     */
    @Fetch(FetchMode.JOIN)
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "structure_rattachement_fk")
    private AStructure structRattachement;
    /**
     * Relation unidirectionnelle.
     */
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "apersonne_fk")
    private Set<Telephone> telephones = new HashSet<>();
    /**
     * Relation unidirectionnelle.
     * Liste des adresses mails autres.
     */
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "apersonne_fk")
    private Set<Mail> autresMails = new HashSet<>();
    /**
     * Relation bidirectionnelle. Liste des attributs incertains.
     */
    @OneToMany(mappedBy = "incertainPers", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<Incertain> incertains = new HashSet<>();
    /**
     * Relation bidirectionnelle.
     * Liste des structures dans lesquelles la personne intervient.
     */
    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "apersonnes_astructures",
        joinColumns = @JoinColumn(name = "APERSONNE_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "ASTRUCTURE_ID", referencedColumnName = "ID")
    )
    private Set<AStructure> listeStructures = new HashSet<>();
    /**
     * Relation avec type primitif.
     * Liste des UAI où le compte ENT est répliqué sur le réseau local.
     */
    @ElementCollection
    @CollectionTable(
        name = "externalId",
        joinColumns = @JoinColumn(name = "APersonne_id"),
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"id"}),
            @UniqueConstraint(name = "UK_externalid_userid_destinataire", columnNames = {"APersonne_id", "destinataire"})
        }
    )
    private Set<ExternalId> externalIds = new HashSet<>();

    /**
     * Setter automatique du membre dateSourceModification lors de la creation de l'objet.
     */
    @Override
    @PrePersist
    public void prePersistOps() {
        super.prePersistOps();
        Date d = new Date();
        d.setTime(Calendar.getInstance().getTimeInMillis());
        if (this.dateSourceModification == null) {
            this.dateSourceModification = d;
        }
    }

    /**
     * Setter automatique du membre dateModification lors de la modification de l'objet. On désactive la fonction automatique pour le delete des
     * personne. Cela est à gérer dans la partie métier.
     */
    @Override
    @PreUpdate
    public void preUpdateOps() {
        if(!this.getEtat().equals(Etat.Delete)){
            this.setDateModification(new Date());
        }
    }

    /**
     * Constructeur de l'objet APersonne.java.
     */
    public APersonne() {
        super();
        this.forceEtat = ForceEtat.NONE;
    }

    /**
     * Constructeur de l'objet APersonne.java.
     *
     * @param anneeScolaire Année scolaire de validité de l'individu. Année à la rentrée de septembre.
     * @param categorie     Catégorie de la personne : Eleve, Enseignant ...
     * @param cleJointure   Clé de jointure, identifiant unique fourni par les différentes sources,
     *                      mais unique uniquement pour le périmètre de la source.
     * @param cn            Nom canonique de la personne : NOM + Prénom usuels.
     * @param givenName     Prénom usuel.
     * @param sn            Nom d'usage.
     */
    public APersonne(final Date anneeScolaire, final CategoriePersonne categorie, final CleJointure cleJointure,
                     final String cn, final String givenName, final String sn) {
        super();
        this.anneeScolaire = anneeScolaire;
        this.categorie = categorie;
        this.cleJointure = cleJointure;
        this.cn = cn;
        this.givenName = givenName;
        this.sn = sn;
        this.etat = Etat.Valide;
        this.forceEtat = ForceEtat.NONE;
    }

    public SubjectKey getSubject() {
        return new SubjectKey(this.getUid(), SubjectType.PERSON);
    }

}
