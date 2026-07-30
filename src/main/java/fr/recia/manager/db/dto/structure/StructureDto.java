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

package fr.recia.manager.db.dto.structure;

import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.dto.AlertDto;
import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.manager.db.dto.gestion.IncertainPersonneDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.common.Adresse;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.entities.structure.ServiceAcademique;
import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.enums.EtatAlim;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.web.dto.user.CardPersonneDto;
import fr.recia.manager.web.dto.user.PersonneInListDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class StructureDto {

    private Long id;
    private String uai;
    private Etat etat;
    private EtatAlim etatAlim;
    private String source;
    private Date anneeScolaire;
    private Adresse adresse;
    private CategorieStructure categorie;
    private String type;
    private String nom;
    private String nomCourt;
    private String siren;
    private String siteWeb;
    private String logo;

    private List<TypeFonctionFiliereDto> filieres;
    private List<PersonneInListDto> personnes;
    private List<CardPersonneDto> withoutFunctions;
    private List<AlertDto> alerts;
    private Collection<IncertainPersonneDto> incertains;
    private List<AppRole> permissions;

    public StructureDto(AStructure aStructure) {
        this.id = aStructure.getId();
        if(aStructure.getCategorie().equals(CategorieStructure.Etablissement)){
            this.uai = ((Etablissement) aStructure).getUai();
        }
        else if(aStructure.getCategorie().equals(CategorieStructure.Service_academique)){
            this.uai = ((ServiceAcademique) aStructure).getUai();
        }
        this.etat = aStructure.getEtat();
        this.etatAlim = aStructure.getEtatAlim();
        this.source = aStructure.getCleJointure().getSource();
        this.anneeScolaire = aStructure.getAnneeScolaire();
        this.adresse = aStructure.getAdresse();
        this.categorie = aStructure.getCategorie();
        this.nomCourt = aStructure.getNomCourt();
        this.siren = aStructure.getSiren();
        this.siteWeb = aStructure.getSiteWeb();
        this.logo = aStructure.getLogo();
        this.permissions = new ArrayList<>();
        String[] split = aStructure.getNom().split("\\$");
        if (split.length > 1) {
            this.type = split[0];
            this.nom = split[1];
        } else {
            this.nom = aStructure.getNom();
        }
    }

    public void addPermission(AppRole appRole){
        this.permissions.add(appRole);
    }

    public void setListePersonnes(List<DatabasePersonneDto> personnes, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties){
        this.personnes = new ArrayList<>();
        for(DatabasePersonneDto personneDto : personnes){
            this.personnes.add(new PersonneInListDto(personneDto, loginOfficeProperties));
        }
    }

    public void setPersonnesWithoutFonctions(List<DatabasePersonneDto> personnes){
        this.withoutFunctions = new ArrayList<>();
        for(DatabasePersonneDto personneDto : personnes){
            this.withoutFunctions.add(new CardPersonneDto(personneDto));
        }
    }

}
