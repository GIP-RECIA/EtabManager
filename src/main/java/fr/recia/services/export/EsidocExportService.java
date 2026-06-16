package fr.recia.services.export;

import fr.recia.configuration.AppProperties;
import fr.recia.web.dto.export.EsidocWSResponse;
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
