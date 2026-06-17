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

package fr.recia.manager.web.dto.user;

import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Civilite;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.web.dto.relation.RelationDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Slf4j
public class PersonneDetailDto {

    private Long id;
    private Etat etat;
    private LocalDate anneeScolaire;
    private CategoriePersonne categorie;
    private Civilite civilite;
    private String source;
    private String cn;
    private LocalDate dateNaissance;
    private String email;
    private String givenName;
    private String patronyme;
    private String sn;
    private String uid;
    private String password;
    private LocalDateTime dateFin;
    private LocalDateTime dateSourceModification;
    private String login;
    private LocalDateTime dateModification;
    private LocalDateTime dateAcquittement;
    private LocalDateTime dateSuppression;
    private String photo;
    private String idPronote;
    private boolean listeRouge;
    private Set<StructureForUserDto> listeStructures;
    private List<RelationDto> relations;
    private String guichet;
    private boolean local;

    public PersonneDetailDto(APersonne aPersonne, boolean showUid, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties) {
        this.id = aPersonne.getId();
        this.etat = aPersonne.getEtat();
        this.anneeScolaire = aPersonne.getAnneeScolaire();
        this.categorie = aPersonne.getCategorie();
        this.civilite = aPersonne.getCivilite();
        this.source = aPersonne.getCleJointure().getSource();
        this.cn = aPersonne.getCn();
        this.dateNaissance = aPersonne.getDateNaissance();
        this.email = aPersonne.getEmail();
        this.givenName = aPersonne.getGivenName();
        this.patronyme = aPersonne.getPatronyme();
        this.sn = aPersonne.getSn();
        if (showUid) {
            this.uid = aPersonne.getUid();
            // TODO : dans quel cas on affiche le mot de passe ?
            if(aPersonne.getCleJointure().getSource().startsWith("SarapisUi_") && aPersonne.getEtat().equals(Etat.Invalide)){
                this.password = aPersonne.getPassword();
            }
        }
        this.login = aPersonne.getLogin().getNom();
        this.dateFin = aPersonne.getDateFin();
        this.dateSourceModification = aPersonne.getDateSourceModification();
        if (aPersonne.getEtat() == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())) {
            this.etat = Etat.Deleting;
            this.dateSuppression = aPersonne.getDateModification().plusDays(20);
        }
        this.photo = aPersonne.getPhoto();
        this.idPronote = null;
        this.listeRouge = aPersonne.isListeRouge();
        this.listeStructures = new TreeSet<>(Comparator.comparing(StructureForUserDto::getNom));
        this.relations = new ArrayList<>();
        this.dateModification = aPersonne.getDateModification();
        this.dateAcquittement = aPersonne.getDateAcquittement();
        this.guichet = null;
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(aPersonne.getCleJointure().getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(aPersonne.getCategorie())){
                        this.guichet = guichetProperty.getNom();
                    }
                }
            }
        }
        this.local = aPersonne.getCleJointure().getSource().startsWith("SarapisUi_");
    }

}
