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

import fr.recia.manager.utils.ListUtil;
import lombok.Data;

import java.util.List;

import static fr.recia.manager.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.manager.configuration.Constants.JSON_ARRAY_SUFFIX;

@Data
public class CorsProperties {

    private boolean enable;
    private boolean allowCredentials;
    private List<String> allowedOrigins;
    private List<String> exposedHeaders;
    private List<String> allowedHeaders;
    private List<String> allowedMethods;

    @Override
    public String toString() {
        return "CorsProperties\": {" +
            "\n\t\"enable\": " + enable + "," +
            "\n\t\"allowCredentials\": " + allowCredentials + "," +
            "\n\t\"allowedOrigins\": " + ListUtil.toStringList(allowedOrigins, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) + "," +
            "\n\t\"exposedHeaders\": " + ListUtil.toStringList(exposedHeaders, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) + "," +
            "\n\t\"allowedHeaders\": " + ListUtil.toStringList(allowedHeaders, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) + "," +
            "\n\t\"allowedMethods\": " + ListUtil.toStringList(allowedMethods, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) +
            "\n}";
    }

}
