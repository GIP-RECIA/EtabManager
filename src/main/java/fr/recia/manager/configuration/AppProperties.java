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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.recia.manager.configuration.bean.AdminProperties;
import fr.recia.manager.configuration.bean.CASProperties;
import fr.recia.manager.configuration.bean.CorsProperties;
import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.configuration.bean.CustomLdapProperties;
import fr.recia.manager.configuration.bean.CustomMailProperties;
import fr.recia.manager.configuration.bean.CustomMetricsProperties;
import fr.recia.manager.configuration.bean.ExportEsidocProperties;
import fr.recia.manager.configuration.bean.FrontProperties;
import fr.recia.manager.configuration.bean.GroupProperties;
import fr.recia.manager.configuration.bean.GrouperProperties;
import fr.recia.manager.configuration.bean.IpRangeProperties;
import fr.recia.manager.configuration.bean.RedisProperties;
import fr.recia.manager.configuration.bean.RedisSessionCleanupProperties;
import fr.recia.manager.configuration.bean.RestrictionRentreeProperties;
import fr.recia.manager.configuration.bean.RightsProperties;
import fr.recia.manager.configuration.bean.ServiceConfigProperties;
import fr.recia.manager.configuration.bean.UIDFactoryProperties;
import fr.recia.manager.security.AdminRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(
    prefix = "app",
    ignoreUnknownFields = false
)
@Data
@Validated
@Slf4j
public class AppProperties {

    private CASProperties cas;
    private CorsProperties cors;
    private CustomConfigProperties customConfig;
    private CustomLdapProperties ldap;
    private CustomMailProperties mail;
    private CustomMetricsProperties metrics;
    private FrontProperties front;
    private IpRangeProperties authorizedServices;
    private RightsProperties rights;
    private AdminProperties admin;
    private GrouperProperties grouper;
    private RestrictionRentreeProperties restrictionRentree;
    private ExportEsidocProperties exportEsidoc;
    private UIDFactoryProperties uidFactory;
    private RedisProperties redis;
    private RedisSessionCleanupProperties session;

    @PostConstruct
    private void init() {
        for(AdminRule rule : admin.getRules()){
            rule.setCompiledPattern(Pattern.compile(rule.getPattern()));
        }
        rights.setDeclaredGroupsMap(rights.getDeclaredGroups().stream()
            .collect(Collectors.toUnmodifiableMap(
                GroupProperties::getGrouperPath,
                GroupProperties::getDisplayName
            ))
        );
        // Chargement des properties des services pour les droits d'accès
        Map<String, ServiceConfigProperties> services = new HashMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(rights.getServicesPath());
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            for (Resource resource : resources) {
                String serviceId = resource.getFilename().replace(".yml", "");
                ServiceConfigProperties service = mapper.readValue(resource.getInputStream(), ServiceConfigProperties.class);
                services.put(serviceId, service);
            }
            rights.setServices(services);
        } catch (IOException e) {
            log.error("Couldn't load service rights properties !", e);
        }
        customConfig.loadAdminFonctions();
        log.info("Loaded properties: {}", this);
    }

    @Override
    public String toString() {
        return "{\n" +
            cas + ",\n" +
            cors + ",\n" +
            customConfig + ",\n" +
            ldap + ",\n" +
            mail + ",\n" +
            metrics + ",\n" +
            front + ",\n" +
            authorizedServices + ",\n" +
            rights + ",\n" +
            restrictionRentree + ",\n" +
            exportEsidoc + ",\n" +
            admin + ",\n" +
            grouper + ",\n" +
            redis + ",\n" +
            session + ",\n" +
            "\n}";
    }

}
