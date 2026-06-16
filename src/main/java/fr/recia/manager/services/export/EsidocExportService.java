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

package fr.recia.manager.services.export;

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.web.dto.export.EsidocWSResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EsidocExportService {

    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final static String API_KEY_HEADER = "x-api-key";

    public EsidocExportService(RestTemplate restTemplateEsidoc, AppProperties appProperties) {
        this.restTemplate = restTemplateEsidoc;
        this.appProperties = appProperties;
    }

    public EsidocWSResponse exportForEtab(String uai){
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, appProperties.getExportEsidoc().getApiKey());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(appProperties.getExportEsidoc().getUrl() + "/" + uai, HttpMethod.GET, entity, EsidocWSResponse.class).getBody();
    }

}
