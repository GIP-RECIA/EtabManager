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

package fr.recia.manager.configuration;

import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.util.List;

@Configuration
public class JacksonConfiguration {

    @Bean
    public JacksonJsonHttpMessageConverter jacksonMessageConverter(JsonMapper objectMapper) {
        JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(objectMapper);
        // On a besoin d'un converteur spécifique car grouper renvoie des réponses en text/x-json et pas en application/json
        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.valueOf(Constants.GROUPER_MEDIA_TYPE)));
        return converter;
    }

    @Bean
    public JsonMapper objectMapper() {
        return JsonMapper.builder().build();
    }

}
