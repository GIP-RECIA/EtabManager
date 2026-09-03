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
import fr.recia.manager.utils.ListUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.recia.manager.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_SUFFIX;

@Data
@Slf4j
public class CustomConfigProperties {

    private String ldapResetPassword;
    private String pronoteGroupRegex;
    private Integer suppressDays;
    private List<AlertProperties> alerts;
    private List<FonctionsProperties> fonctions;
    private Map<String, Set<String>> adminFonctionsBySource;
    private List<LoginOfficeProperties> loginOffices;
    private List<PartitionedSourcesProperties> partitionedSources;

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

    public void loadAdminFonctions(){
        adminFonctionsBySource = new HashMap<>();
        for(FonctionsProperties fonctionsProperties : fonctions){
            final String source = fonctionsProperties.source;
            if(!adminFonctionsBySource.containsKey(source)){
                adminFonctionsBySource.put(source, new HashSet<>());
            }
            for(FonctionsProperties.FiliereProperties filiereProperties : fonctionsProperties.filieres){
                if(filiereProperties.admin){
                    adminFonctionsBySource.get(source).add(filiereProperties.code);
                }
            }
        }
    }

}
