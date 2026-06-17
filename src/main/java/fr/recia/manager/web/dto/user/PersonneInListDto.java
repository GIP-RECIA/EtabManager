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
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class PersonneInListDto {

    private Long id;
    private Etat etat;
    private String nom;
    private String prenom;
    private String uid;
    private CategoriePersonne categoriePersonne;
    private String login;
    private String email;
    private LocalDateTime dateModificationSource;
    private LocalDateTime dateSuppression;
    private String guichet;
    private boolean local;

    public PersonneInListDto(DatabasePersonneDto databasePersonneDto, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties) {
        this.id = databasePersonneDto.getId();
        this.etat = databasePersonneDto.getEtat();
        this.uid = databasePersonneDto.getUid();
        this.nom = databasePersonneDto.getSn();
        this.prenom = databasePersonneDto.getGivenName();
        this.uid = databasePersonneDto.getUid();
        this.categoriePersonne = databasePersonneDto.getCategorie();
        this.login = databasePersonneDto.getLogin();
        this.email = databasePersonneDto.getEmail();
        this.dateModificationSource = databasePersonneDto.getDateModificationSource();
        this.dateSuppression = databasePersonneDto.getDateSuppression();
        this.local = databasePersonneDto.getSource().startsWith("SarapisUi_");
        this.guichet = null;
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(databasePersonneDto.getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(databasePersonneDto.getCategorie())){
                        this.guichet = guichetProperty.getNom();
                    }
                }
            }
        }
    }

}
