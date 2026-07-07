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

import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.entities.structure.ServiceAcademique;
import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.web.dto.function.DisciplinesInFilliereDisplayDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StructureForUserDto {

    private Long id;
    private String uai;
    private String type;
    private String nom;
    private String nomCourt;
    private String siren;
    private List<DisciplinesInFilliereDisplayDto> fonctions;
    private List<DisciplinesInFilliereDisplayDto> additionalFonctions;
    private boolean structureRattachement;
    private boolean structureCourante;
    private List<String> classes;
    private List<String> groupesPedagogiques;
    private List<String> enseignements;
    private boolean authorizedForPrincipal;

    public StructureForUserDto(AStructure aStructure) {
        String[] split = aStructure.getNom().split("\\$");
        if (split.length > 2) {
            this.type = split[0];
            this.nom = split[1];
        } else {
            this.nom = aStructure.getNom();
        }
        this.id = aStructure.getId();
        if(aStructure.getCategorie().equals(CategorieStructure.Etablissement)){
            this.uai = ((Etablissement) aStructure).getUai();
        }
        else if(aStructure.getCategorie().equals(CategorieStructure.Service_academique)){
            this.uai = ((ServiceAcademique) aStructure).getUai();
        }
        this.nomCourt = aStructure.getNomCourt();
        this.siren = aStructure.getSiren();
        this.fonctions = new ArrayList<>();
        this.additionalFonctions = new ArrayList<>();
        this.structureCourante = false;
        this.structureRattachement = false;
        this.classes = new ArrayList<>();
        this.groupesPedagogiques = new ArrayList<>();
        this.authorizedForPrincipal = false;
    }

}
