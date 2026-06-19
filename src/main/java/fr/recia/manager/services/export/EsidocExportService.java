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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.web.dto.export.EsidocWSResponse;
import fr.recia.manager.web.dto.export.EsidocWSResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class EsidocExportService {

    private final RestTemplate restTemplate;
    private final AppProperties appProperties;
    private final static String API_KEY_HEADER = "x-api-key";

    @Autowired
    ObjectMapper objectMapper;

    public EsidocExportService(RestTemplate restTemplateEsidoc, AppProperties appProperties) {
        this.restTemplate = restTemplateEsidoc;
        this.appProperties = appProperties;
    }

    public EsidocWSResponseInfo exportForEtab(String uai) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, appProperties.getExportEsidoc().getApiKey());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
           ResponseEntity<EsidocWSResponse> esidocWSResponseResponseEntity = restTemplate.exchange(appProperties.getExportEsidoc().getUrl() + "/" + uai, HttpMethod.GET, entity, EsidocWSResponse.class);
            return new EsidocWSResponseInfo(esidocWSResponseResponseEntity.getBody(),esidocWSResponseResponseEntity.getStatusCode());
        } catch (HttpStatusCodeException e) {
            EsidocWSResponse esidocWSResponse = null;
            HttpStatus httpStatus = e.getStatusCode();
                try {
                    esidocWSResponse = objectMapper.readValue(
                        e.getResponseBodyAsString(),
                        EsidocWSResponse.class
                    );
                } catch (JsonProcessingException ex) {
                    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                return new EsidocWSResponseInfo(esidocWSResponse, httpStatus);
        }
    }

}
