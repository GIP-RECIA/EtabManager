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

import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.utils.ListUtil;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FrontProperties {

    private String defaultStructureImage;
    private String defaultUserImage;
    private Long endFunctionWarning;
    private List<HomeLinkProperties> homeLinks;
    private List<Etat> editAllowedStates;
    private List<Etat> filterAccountStates;
    private ExtendedUportalProperties extendedUportal;

    @Data
    public static class HomeLinkProperties{

        private String fname;
        private String url;


        @Override
        public String toString() {
            return "{" +
                "\n\t\t\"fname\": \n" + fname + "\n," +
                "\n\t\t\"url\": \n" + url + "\"" +
                "\n\t}";
        }
    }

    @Data
    public static class ExtendedUportalProperties {

        private ComponentProperties header;
        private ComponentProperties footer;

        @Override
        public String toString() {
            return "{" +
                "\n\t\t\"header\": " + header + "," +
                "\n\t\t\"footer\": " + footer +
                "\n\t}";
        }

        @Data
        public static class ComponentProperties {

            private String componentPath;
            private Map<String, String> props;

            @Override
            public String toString() {
                return "{" +
                    "\n\t\t\t\"componentPath\": \"" + componentPath + "\"," +
                    "\n\t\t\t\"props\": " + props +
                    "\n\t\t}";
            }

        }

    }

    @Override
    public String toString() {
        return "\"FrontProperties\": {" +
            "\n\t\"defaultStructureImage\": \"" + defaultStructureImage + "\"," +
            "\n\t\"defaultUserImage\": \"" + defaultUserImage + "\"," +
            "\n\t\"endFunctionWarning\": " + endFunctionWarning + "," +
            "\n\t\"homeLinks\": " + ListUtil.toStringList(homeLinks) + "," +
            "\n\t\"editAllowedStates\": " + ListUtil.toStringList(editAllowedStates) + "," +
            "\n\t\"filterAccountStates\": " + ListUtil.toStringList(filterAccountStates) + "," +
            "\n\t\"extendedUportal\": " + extendedUportal +
            "\n}";
    }

}
