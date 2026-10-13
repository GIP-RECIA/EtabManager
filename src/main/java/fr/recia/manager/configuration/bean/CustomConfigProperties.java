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

package fr.recia.manager.configuration.bean;

import fr.recia.manager.db.dto.AlertType;
import fr.recia.manager.db.enums.CategoriePersonne;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
public class CustomConfigProperties {

    private String ldapResetPassword;
    private String pronoteGroupRegex;
    private Integer suppressDays;
    private Integer undoSuppressDays;
    private List<AlertProperties> alerts;
    private List<FonctionsProperties> fonctions;
    private Map<String, Set<String>> adminFonctionsBySource;
    private List<LoginOfficeProperties> loginOffices;
    private List<PartitionedSourcesProperties> partitionedSources;
    private List<ProfilsByCategoryProperties> profilsByCategory;
    private List<FonctionsByProfilProperties> fonctionsByProfil;
    private Map<String, Set<String>> profilsByCategoryMap;
    private Map<String, String> profilByFonctionMap;
    private Map<String, List<String>> categoryByProfilMap;

    @Data
    public static class ProfilsByCategoryProperties {
        private String category;
        private Set<String> profils;
    }

    @Data
    public static class FonctionsByProfilProperties {
        private String profil;
        private Set<String> codesFiliere;
    }

    @Data
    public static class PartitionedSourcesProperties {
        private String name;
        private Set<String> sources;
    }

    @Data
    public static class AlertProperties {

        private String source;
        private List<FonctionAlertProperties> fonctionAlerts;

        @Data
        public static class FonctionAlertProperties {

            private String filiere;
            private String discipline;
            private ValueProperties min;
            private ValueProperties max;

            @Data
            public static class ValueProperties {
                private int value;
                private AlertType type;
                private boolean action;
            }

        }

    }

    @Data
    public static class LoginOfficeProperties {

        private String source;
        private List<GuichetProperties> guichets;

        @Data
        public static class GuichetProperties {
            private String nom;
            private List<CategoriePersonne> categoriesPersonne;
        }

    }

    @Data
    public static class FonctionsProperties {

        private String source;
        private List<FiliereProperties> filieres = new ArrayList<>();
        private List<String> disciplines = new ArrayList<>();

        @Data
        public static class FiliereProperties {
            private String code;
            private List<String> disciplines;
            private boolean admin;
        }

    }

    /**
     * Charge les properties des fonctions admin dans une Map pratique à interroger
     */
    public void loadAdminFonctions(){
        this.adminFonctionsBySource = new HashMap<>();
        for(FonctionsProperties fonctionsProperties : this.fonctions){
            final String source = fonctionsProperties.source;
            if(!this.adminFonctionsBySource.containsKey(source)){
                this.adminFonctionsBySource.put(source, new HashSet<>());
            }
            for(FonctionsProperties.FiliereProperties filiereProperties : fonctionsProperties.filieres){
                if(filiereProperties.admin){
                    this.adminFonctionsBySource.get(source).add(filiereProperties.code);
                }
            }
        }
    }

    /**
     * Charge les properties au sujet des liens profils->catégories<->fonctions dans des Map pratiques à interroger
     */
    public void loadProfileMapper(){
        this.profilsByCategoryMap = new HashMap<>();
        this.categoryByProfilMap = new HashMap<>();
        for(ProfilsByCategoryProperties profilsByCategoryProperties : this.profilsByCategory){
            this.profilsByCategoryMap.put(profilsByCategoryProperties.getCategory(), profilsByCategoryProperties.getProfils());
            for(String profil : profilsByCategoryProperties.getProfils()){
                if(this.categoryByProfilMap.containsKey(profil)){
                    log.debug("Multiple categories for profil {} in configuration. Resolution for these conflicts will be handed by hand when adding functions", profil);
                } else {
                    this.categoryByProfilMap.put(profil, new ArrayList<>());
                }
                this.categoryByProfilMap.get(profil).add(profilsByCategoryProperties.getCategory());
            }
        }
        this.profilByFonctionMap = new HashMap<>();
        for(FonctionsByProfilProperties fonctionsByProfilProperties : this.fonctionsByProfil){
            for(String codeFiliere : fonctionsByProfilProperties.getCodesFiliere()){
                if(this.profilByFonctionMap.containsKey(codeFiliere)){
                    log.warn("Duplicate profil for filiere {} in configuration !", codeFiliere);
                }
                this.profilByFonctionMap.put(codeFiliere, fonctionsByProfilProperties.getProfil());
            }
        }
    }

}
