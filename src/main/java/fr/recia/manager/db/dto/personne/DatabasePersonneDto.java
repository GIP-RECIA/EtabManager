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

package fr.recia.manager.db.dto.personne;

import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class DatabasePersonneDto {

    private Long id;
    private Etat etat;
    private CategoriePersonne categorie;
    private String source;
    private String cn;
    private String email;
    private String givenName;
    private String sn;
    private String login;
    private String uid;
    private LocalDateTime dateModification;
    private LocalDateTime dateAcquittement;
    private LocalDateTime dateSuppression;
    private LocalDateTime dateModificationSource;

    public DatabasePersonneDto(Long id, Etat etat, CategoriePersonne categorie, String source, String cn, String email,
                               String sn, String uid, LocalDateTime dateModification, LocalDateTime dateAcquittement, LocalDateTime dateModificationSource) {
        this.id = id;
        this.etat = etat;
        this.categorie = categorie;
        this.source = source;
        this.cn = cn;
        this.email = email;
        this.sn = sn;
        if (etat == Etat.Delete && dateModification.equals(dateAcquittement)) {
            this.etat = Etat.Deleting;
            this.dateSuppression = dateModification;
        }
        this.uid = uid;
        this.dateModification = dateModification;
        this.dateAcquittement = dateAcquittement;
        this.dateModificationSource = dateModificationSource;
    }

    public DatabasePersonneDto(Long id, Etat etat, CategoriePersonne categorie, String source, String cn, String email,
                               String givenName, String sn, String login, String uid, LocalDateTime dateModification, LocalDateTime dateAcquittement, LocalDateTime dateModificationSource) {
        this.id = id;
        this.etat = etat;
        this.categorie = categorie;
        this.source = source;
        this.cn = cn;
        this.email = email;
        this.givenName = givenName;
        this.sn = sn;
        this.login = login;
        if (etat == Etat.Delete && dateModification.equals(dateAcquittement)) {
            this.etat = Etat.Deleting;
            this.dateSuppression = dateModification;
        }
        this.uid = uid;
        this.dateModification = dateModification;
        this.dateAcquittement = dateAcquittement;
        this.dateModificationSource = dateModificationSource;
    }


    public DatabasePersonneDto(Long id, Etat etat, CategoriePersonne categorie, String source, String cn, String email,
                               String sn, LocalDateTime dateModification, LocalDateTime dateAcquittement, LocalDateTime dateModificationSource) {
        this.id = id;
        this.etat = etat;
        this.categorie = categorie;
        this.source = source;
        this.cn = cn;
        this.email = email;
        this.sn = sn;
        if (etat == Etat.Delete && dateModification.equals(dateAcquittement)) {
            this.etat = Etat.Deleting;
            this.dateSuppression = dateModification;
        }
        this.uid = "";
        this.dateModification = dateModification;
        this.dateAcquittement = dateAcquittement;
        this.dateModificationSource = dateModificationSource;
    }

    public DatabasePersonneDto(Long id, Etat etat, CategoriePersonne categorie, String source, String cn, String email,
                               String givenName, String sn, String login, LocalDateTime dateModification, LocalDateTime dateAcquittement, LocalDateTime dateModificationSource) {
        this.id = id;
        this.etat = etat;
        this.categorie = categorie;
        this.source = source;
        this.cn = cn;
        this.email = email;
        this.givenName = givenName;
        this.sn = sn;
        this.login = login;
        if (etat == Etat.Delete && dateModification.equals(dateAcquittement)) {
            this.etat = Etat.Deleting;
            this.dateSuppression = dateModification;
        }
        this.uid = "";
        this.dateModification = dateModification;
        this.dateAcquittement = dateAcquittement;
        this.dateModificationSource = dateModificationSource;
    }

    public DatabasePersonneDto(APersonne aPersonne) {
        this.id = aPersonne.getId();
        this.etat = aPersonne.getEtat();
        this.categorie = aPersonne.getCategorie();
        this.source = aPersonne.getCleJointure().getSource();
        this.cn = aPersonne.getCn();
        this.sn = aPersonne.getSn();
        this.login = aPersonne.getLogin().getNom();
        this.givenName = aPersonne.getGivenName();
        if (aPersonne.getEtat() == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())) {
            this.etat = Etat.Deleting;
            this.dateSuppression = aPersonne.getDateModification();
        }
        this.dateModification = aPersonne.getDateModification();
        this.dateAcquittement = aPersonne.getDateAcquittement();
        this.dateModificationSource = aPersonne.getDateSourceModification();
    }
}
