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

package fr.recia.manager.utils;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Permet de parser les dates renvoyées par le front en LocalDateTime
 * Permissif dans la mesure ou il peut prendre plusieurs types de date en entrée
 */
public class CustomLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) {
        String value = parser.getString();
        // Cas LocalDateTime
        if (value.contains("T")) {
            return LocalDateTime.parse(value);
        }
        // Cas LocalDate
        return LocalDate.parse(value).atStartOfDay();
    }
}
