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

import fr.recia.manager.configuration.cas.CustomCas30ServiceTicketValidator;
import fr.recia.manager.configuration.cas.CustomCasAuthenticationEntryPoint;
import fr.recia.manager.configuration.cas.CustomCasSuccessHandler;
import fr.recia.manager.configuration.cas.CustomSessionMappingStorage;
import fr.recia.manager.ldap.StructureFromGroup;
import fr.recia.manager.ldap.StructureSirenDomain;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.services.structure.StructureLoader;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Assertion;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final AppProperties appProperties;
    private final StructureLoader structureLoader;
    private final CustomSessionMappingStorage customSessionMappingStorage;
    private final CustomCasSuccessHandler customCasSuccessHandler;

    public SecurityConfiguration(AppProperties appProperties, StructureLoader structureLoader,
                                 CustomSessionMappingStorage customSessionMappingStorage, CustomCasSuccessHandler customCasSuccessHandler) {
        this.appProperties = appProperties;
        this.structureLoader = structureLoader;
        this.customSessionMappingStorage = customSessionMappingStorage;
        this.customCasSuccessHandler = customCasSuccessHandler;
    }

    /**
     * Propriétés du service CAS, avec notamment serviceID associé côté CAS
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(appProperties.getCas().getCasServiceId());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    /**
     * Redirection vers le CAS sur une route protégée
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint entryPoint = new CustomCasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(appProperties.getCas().getCasServerUrl() + "/login");
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }

    /**
     * Filtre qui intercepte la requête vers le service depuis le CAS (en passant par le navigateur) avec le ticket dans l'URL
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl(appProperties.getCas().getCasTicketCallback());
        filter.setAuthenticationSuccessHandler(customCasSuccessHandler);
        return filter;
    }

    /**
     * Fournisseur d'authentitication qui va faire la requête au CAS pour valider le ticket reçu
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties serviceProperties, AppProperties appProperties) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator(new CustomCas30ServiceTicketValidator(appProperties.getCas().getCasServerUrl(), false, appProperties));
        provider.setAuthenticationUserDetailsService(customUserDetailsService());
        provider.setKey(appProperties.getCas().getCasProviderKey());
        return provider;
    }

    /**
     * Transformer l’utilisateur CAS en utilisateur Spring Security pour remplir les User Details
     * Calcule et stocke les droits de l'utilisateur pour les réutiliser plus tard sans avoir à tout recalculer
     */
    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return (CasAssertionAuthenticationToken token) -> {
            Assertion assertion = token.getAssertion();
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            List<String> groups = (List<String>) attributes.get(appProperties.getAdmin().getGroupsAttribute());
            String username = assertion.getPrincipal().getName();
            Pattern patternAdminLocal = Pattern.compile(appProperties.getAdmin().getLocal());
            Pattern patternAdminSarapisLocal = Pattern.compile(appProperties.getAdmin().getSarapisLocal());
            Pattern patternAdminCentral = Pattern.compile(appProperties.getAdmin().getCentral());
            Pattern patternEscolan = Pattern.compile(appProperties.getAdmin().getEscolan());
            Pattern patternAdminCentralColl = Pattern.compile(appProperties.getAdmin().getCentralColl());
            Pattern patternDirection = Pattern.compile(appProperties.getAdmin().getDirection());
            // Calcul dynamique des authorities en fonction des groupes
            // TODO : rôle dans le nom du groupe
            Map<AppRole, Set<String>> rightsForEtabs = new HashMap<>();
            rightsForEtabs.put(AppRole.WRITE, new HashSet<>());
            rightsForEtabs.put(AppRole.READ, new HashSet<>());
            rightsForEtabs.put(AppRole.VIEW_UID, new HashSet<>());
            rightsForEtabs.put(AppRole.ADMIN_FONCTIONS, new HashSet<>());
            Set<AppRole> globalRights = new HashSet<>();
            if(groups != null){
                for (String group : groups) {
                    Matcher matcherAdminLocal = patternAdminLocal.matcher(group);
                    Matcher matcherAdminSarapisLocal = patternAdminSarapisLocal.matcher(group);
                    Matcher matcherAdminCentral = patternAdminCentral.matcher(group);
                    Matcher matcherEscolan = patternEscolan.matcher(group);
                    Matcher matcherAdminCentralColl = patternAdminCentralColl.matcher(group);
                    Matcher matcherDirection = patternDirection.matcher(group);
                    // Droits sur les établissements
                    if (matcherAdminLocal.matches()) {
                        final String uai = matcherAdminLocal.group(2);
                        final String siren = structureLoader.getSirenByUai(uai);
                        rightsForEtabs.get(AppRole.WRITE).add(siren);
                        rightsForEtabs.get(AppRole.READ).add(siren);
                    }
                    if (matcherAdminSarapisLocal.matches()) {
                        final String uai = matcherAdminSarapisLocal.group(2);
                        final String siren = structureLoader.getSirenByUai(uai);
                        rightsForEtabs.get(AppRole.WRITE).add(siren);
                        rightsForEtabs.get(AppRole.READ).add(siren);
                    }
                    // Droits sur les branches
                    if (matcherAdminCentral.matches()) {
                        for (StructureFromGroup structureFromGroup : structureLoader.getStructuresOfBranch(matcherAdminCentral.group(1))) {
                            final String uai = structureFromGroup.getUAI();
                            final String siren = structureLoader.getSirenByUai(uai);
                            rightsForEtabs.get(AppRole.WRITE).add(siren);
                            rightsForEtabs.get(AppRole.READ).add(siren);
                            rightsForEtabs.get(AppRole.VIEW_UID).add(siren);
                            rightsForEtabs.get(AppRole.ADMIN_FONCTIONS).add(siren);
                        }
                        // Si admin de branche = autorisation à faire de la recherche par UID
                        globalRights.add(AppRole.SEARCH_UID);
                        // Si admin de branche = autorisation à faire des rattachements
                        globalRights.add(AppRole.ATTACH);
                    }
                    // Droits sur les branches
                    if (matcherEscolan.matches()) {
                        for (StructureFromGroup structureFromGroup : structureLoader.getStructuresOfBranch(matcherEscolan.group(1))) {
                            final String uai = structureFromGroup.getUAI();
                            final String siren = structureLoader.getSirenByUai(uai);
                            rightsForEtabs.get(AppRole.WRITE).add(siren);
                            rightsForEtabs.get(AppRole.READ).add(siren);
                            rightsForEtabs.get(AppRole.VIEW_UID).add(siren);
                            rightsForEtabs.get(AppRole.ADMIN_FONCTIONS).add(siren);
                        }
                        // Si admin de branche = autorisation à faire de la recherche par UID
                        globalRights.add(AppRole.SEARCH_UID);
                        // Si admin de branche = autorisation à faire des rattachements
                        globalRights.add(AppRole.ATTACH);
                    }
                    // Droits sur les collectivités
                    if (matcherAdminCentralColl.matches()) {
                        for(StructureSirenDomain collectivite : structureLoader.getAllCollectivites()){
                            rightsForEtabs.get(AppRole.WRITE).add(collectivite.getSiren());
                            rightsForEtabs.get(AppRole.READ).add(collectivite.getSiren());
                            rightsForEtabs.get(AppRole.VIEW_UID).add(collectivite.getSiren());
                        }
                        // Si admin de branche = autorisation à faire de la recherche par UID
                        globalRights.add(AppRole.SEARCH_UID);
                        // Si admin de branche = autorisation à faire des rattachements
                        globalRights.add(AppRole.ATTACH);
                    }
                    // Droits spécifiques sur certaines fonctions
                    if (matcherDirection.matches()) {
                        final String uai = matcherDirection.group(1);
                        final String siren = structureLoader.getSirenByUai(uai);
                        rightsForEtabs.get(AppRole.ADMIN_FONCTIONS).add(siren);
                    }
                }
            } else {
                log.warn("No groups for user {} !", username);
            }

            return new AppUser(username, "", new ArrayList<>(), rightsForEtabs, globalRights);
        };
    }

    /**
     * Transfert la demande d'auth depuis le filter vers le provider
     */
    @Bean
    public AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        return new ProviderManager(casAuthenticationProvider);
    }

    /**
     * Gérer les requêtes de SLO qui arrivent du CAS pour détruire la session applicative
     */
    @Bean
    public Filter singleSignOutFilter() {
        SingleSignOutFilter delegate = new SingleSignOutFilter();
        delegate.setIgnoreInitConfiguration(true);
        delegate.setArtifactParameterName("ticket");
        delegate.setLogoutParameterName("logoutRequest");

        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
                String logoutRequest = request.getParameter("logoutRequest");
                String ip = request.getRemoteAddr();
                String uri = request.getRequestURI();
                String method = request.getMethod();

                log.debug("[SLO] Requête entrante : {} {} depuis IP={}", method, uri, ip);

                if (logoutRequest != null) {
                    log.trace("[SLO] URI appelée : {}", uri);
                    log.trace("[SLO] Adresse IP appelante : {}", ip);
                    log.trace("[SLO] XML logoutRequest brut :\n{}", logoutRequest);

                    // Parsing XML SAML pour extraire le ticket (SessionIndex)
                    try {
                        var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                        var builder = factory.newDocumentBuilder();
                        var doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(logoutRequest)));
                        doc.getDocumentElement().normalize();

                        var nameIdNode = doc.getElementsByTagName("saml:NameID").item(0);
                        var sessionIndexNode = doc.getElementsByTagName("samlp:SessionIndex").item(0);

                        String nameId = nameIdNode != null ? nameIdNode.getTextContent() : "inconnu";
                        String ticket = sessionIndexNode != null ? sessionIndexNode.getTextContent() : "inconnu";

                        // Lors du logout, le CAS envoie aussi des messages pour invalider les PGT, mais ici on ne traite que les
                        // SessionTicket, qui commencent par ST

                        int index = ticket.indexOf('-');
                        boolean isSessionTicket = false;

                        if (index != -1) {
                            String beforeDash = ticket.substring(0, index + 1);
                            if("ST-".equals(beforeDash)){
                                isSessionTicket = true;
                            }
                        }

                        if(isSessionTicket){
                            log.debug("[SLO] Ticket Invalidation Request will be handled: {}", ticket);
                        }else {
                            log.debug("[SLO] Ticket Invalidation Request will be ignored: {}", ticket);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        String sessionId = customSessionMappingStorage.getSessionIdFromSessionTicket(ticket);

                        log.debug("[SLO] Utilisateur CAS (NameID) : {}", nameId);
                        log.debug("[SLO] Session id: {}", sessionId);

                        customSessionMappingStorage.removeSessionTicket(ticket);
                        log.debug("[SLO] Le cache associé au mappage ticket-sessionID [{}:{}] a été supprimé avec succès.", ticket, sessionId);
                        customSessionMappingStorage.deleteSessionContext(sessionId);
                        log.debug("[SLO] Invalidation réussie de la session [{}].", sessionId);

                    } catch (Exception e) {
                        log.error("[SLO] Erreur de parsing XML logoutRequest", e);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .addFilterAfter(csrfCookieFilter(), CsrfFilter.class)
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .requestMatchers("/api/config").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e
                .authenticationEntryPoint(
                    (request, response, authException) ->
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                )
            );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .addFilterAfter(csrfCookieFilter(), CsrfFilter.class)
            // Filtre pour le logout à mettre avant tout
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
            // La partie exceptionHandling permet de rediriger sur le CAS si on a un 403
            .exceptionHandling(e -> e
                .authenticationEntryPoint(casAuthenticationEntryPoint(serviceProperties()))
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health-check").permitAll()
                .requestMatchers("/", "/ui/**").authenticated()
                // Cet endpoint doit être accessible car c'est le callback du CAS vers l'appli spring pour faire valider le ticket
                .requestMatchers(appProperties.getCas().getCasTicketCallback()).permitAll()
                .anyRequest().denyAll()
            );

        return http.build();
    }

    @Bean
    public Filter csrfCookieFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                            @NonNull FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        };
    }

}
