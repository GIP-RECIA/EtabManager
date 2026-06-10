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

package fr.recia.configuration.bean;

import fr.recia.db.dto.AlertType;
import fr.recia.db.enums.CategoriePersonne;
import fr.recia.utils.ListUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.recia.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.configuration.Constants.JSON_ARRAY_SUFFIX;

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

                @Override
                public String toString() {
                    return "{ " +
                        "\"value\": " + value + ", " +
                        "\"type\": \"" + type + "\", " +
                        "\"action\": \"" + action +
                        "\" }";
                }
            }

            @Override
            public String toString() {
                return "{" +
                    "\n\t\t\t\t\t\"filiere\": \"" + filiere + "\"," +
                    "\n\t\t\t\t\t\"discipline\": \"" + discipline + "\"," +
                    "\n\t\t\t\t\t\"min\": " + min + "," +
                    "\n\t\t\t\t\t\"max\": " + max +
                    "\n\t\t\t\t}";
            }

        }

        @Override
        public String toString() {
            return "{" +
                "\n\t\t\t\"source\": \"" + source + "\"," +
                "\n\t\t\t\"fonctionAlerts\": " + ListUtil.toStringList(fonctionAlerts, ",\n\t\t\t\t", "[\n\t\t\t\t", "\n\t\t\t]") +
                "\n\t\t}";
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

            @Override
            public String toString() {
                return "\t\t\t\t{" +
                    "\n\t\t\t\t\t\"nom\": \"" + nom + "\"," +
                    "\n\t\t\t\t\t\"categoriesPersonne\": " + ListUtil.toStringList(categoriesPersonne) +
                    "\n\t\t\t\t}";
            }

        }

        @Override
        public String toString() {
            return "\t\t{" +
                "\n\t\t\t\"source\": \"" + source + "\"," +
                "\n\t\t\t\"guichets\": " + ListUtil.toStringList(guichets, ",\n", "[\n", "\n\t\t\t]") +
                "\n\t\t}";
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

            @Override
            public String toString() {
                return "{" +
                    "\n\t\t\t\t\t\"code\": \"" + code + "\"," +
                    "\n\t\t\t\t\t\"admin\": \"" + admin + "\"," +
                    "\n\t\t\t\t\t\"disciplines\": " + ListUtil.toStringList(disciplines, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) +
                    "\n\t\t\t\t}";
            }

        }

        @Override
        public String toString() {
            return "{" +
                "\n\t\t\t\"source\": \"" + source + "\"," +
                "\n\t\t\t\"filieres\": " + ListUtil.toStringList(filieres, ",\n\t\t\t\t", "[\n\t\t\t\t", "\n\t\t\t]") + "," +
                "\n\t\t\t\"disciplines\": " + ListUtil.toStringList(disciplines, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) +
                "\n\t\t}";
        }

    }

    @Override
    public String toString() {
        return "CustomConfigProperties\": {" +
            "\n\t\"suppressDays\": " + suppressDays + "," +
            "\n\t\"alerts\": " + ListUtil.toStringList(alerts, ",\n\t\t", "[\n\t\t", "\n\t]") +
            "\n\t\"fonctions\": " + ListUtil.toStringList(fonctions, ",\n\t\t", "[\n\t\t", "\n\t]") +
            "\n\t\"adminFonctionsBySource\": " + adminFonctionsBySource.toString() +
            "\n}";
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
