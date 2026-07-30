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

import fr.recia.manager.db.enums.Etat;
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
    }

    @Data
    public static class ExtendedUportalProperties {

        private ComponentProperties header;
        private ComponentProperties footer;

        @Data
        public static class ComponentProperties {
            private String componentPath;
            private Map<String, String> props;
        }

    }

}
