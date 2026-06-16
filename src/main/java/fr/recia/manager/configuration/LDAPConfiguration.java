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

import fr.recia.manager.configuration.bean.CustomLdapProperties;
import fr.recia.manager.ldap.ExternalGroupHelper;
import fr.recia.manager.ldap.mappers.PeopleResponseAttributesMapper;
import fr.recia.manager.ldap.mappers.StructureSirenDomainAttributesMapper;
import fr.recia.manager.ldap.repository.LdapGroupDao;
import fr.recia.manager.ldap.repository.LdapPeopleDao;
import fr.recia.manager.ldap.repository.LdapStructureDao;
import fr.recia.manager.services.structure.StructureLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.util.Assert;

@Configuration
@Slf4j
public class LDAPConfiguration {

    private final CustomLdapProperties ldapProperties;

    public LDAPConfiguration(AppProperties appProperties) {
        this.ldapProperties = appProperties.getLdap();
    }

    @Bean
    public LdapContextSource contextSource() {
        log.debug("Configuring LdapContextSource");
        final LdapContextSource ldapCtx = new LdapContextSource();
        ldapCtx.setAnonymousReadOnly(ldapProperties.getContextSource().isAnonymousReadOnly());
        ldapCtx.setBase(ldapProperties.getContextSource().getBase());
        ldapCtx.setUrls(ldapProperties.getContextSource().getUrls());
        ldapCtx.setUserDn(ldapProperties.getContextSource().getUsername());
        ldapCtx.setPassword(ldapProperties.getContextSource().getPassword());
        ldapCtx.setPooled(ldapProperties.getContextSource().isNativePooling());
        log.debug("LDAPContext is configured with properties {}", ldapProperties.getContextSource());

        return ldapCtx;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        final LdapTemplate ldapTpl = new LdapTemplate();
        ldapTpl.setContextSource(contextSource());
        ldapTpl.setDefaultCountLimit(ldapProperties.getLdapTemplate().getCountLimit());
        ldapTpl.setDefaultTimeLimit(ldapProperties.getLdapTemplate().getTimeLimit());
        ldapTpl.setDefaultSearchScope(ldapProperties.getLdapTemplate().getSearchScope());
        ldapTpl.setIgnoreNameNotFoundException(ldapProperties.getLdapTemplate().isIgnoreNameNotFoundException());
        ldapTpl.setIgnorePartialResultException(ldapProperties.getLdapTemplate().isIgnorePartialResultException());
        ldapTpl.setIgnoreSizeLimitExceededException(ldapProperties.getLdapTemplate().isIgnoreSizeLimitExceededException());

        return ldapTpl;
    }

    @Bean
    public ExternalGroupHelper externalGroupHelper() {
        log.debug("Configure bean ExternalGroupHelper with LDAP attributes");
        Assert.notNull(
            ldapProperties.getGroupBranch(),
            "Use of LDAP group branch require 'app.ldap.groupBranch.*' properties configured !"
        );
        ExternalGroupHelper ldapUh = new ExternalGroupHelper(
            ldapProperties.getGroupBranch().getIdAttribute(),
            ldapProperties.getGroupBranch().getDisplayNameAttribute(),
            ldapProperties.getGroupBranch().getSearchAttribute(),
            ldapProperties.getGroupBranch().getGroupAttribute(),
            ldapProperties.getGroupBranch().getGroupMemberKeyPattern(),
            ldapProperties.getGroupBranch().getGroupMemberKeyPatternIndex(),
            ldapProperties.getGroupBranch().getUserMemberKeyPattern(),
            ldapProperties.getGroupBranch().getUserMemberKeyPatternIndex(),
            ldapProperties.getGroupBranch().getGroupDisplayNamePattern(),
            ldapProperties.getGroupBranch().isDNContainsDisplayName(),
            ldapProperties.getGroupBranch().isResolveUserMembers(),
            ldapProperties.getGroupBranch().isResolveUserMembersByUserAttributes(),
            ldapProperties.getGroupBranch().getDontResolveMembersWithGroupPattern(),
            ldapProperties.getGroupBranch().getOtherDisplayedAttributes(),
            ldapProperties.getGroupBranch().getBaseDN()
        );
        log.debug("LdapAttributes for Groups configured : {}", ldapUh);

        return ldapUh;
    }

    @Bean
    public LdapGroupDao ldapExternalGroupDao(LdapTemplate ldapTemplate) {
        log.debug("Configuring IExternalGroupDao with LDAP DAO");
        Assert.notNull(
            ldapProperties.getGroupBranch(),
            "Use of Group Branch require 'app.ldap.groupBranch.*' properties configured !"
        );
        return new LdapGroupDao(ldapTemplate, externalGroupHelper(), ldapProperties);
    }

    @Bean
    public LdapStructureDao ldapStructureDao(LdapTemplate ldapTemplate, StructureSirenDomainAttributesMapper structureSirenDomainAttributesMapper) {
        return new LdapStructureDao(ldapTemplate, structureSirenDomainAttributesMapper, ldapProperties);
    }

    @Bean
    public LdapPeopleDao ldapPeopleDao(LdapTemplate ldapTemplate, PeopleResponseAttributesMapper peopleResponseAttributesMapper) {
        return new LdapPeopleDao(ldapTemplate, ldapProperties, peopleResponseAttributesMapper);
    }

    @Bean
    public StructureLoader loadedStructure(LdapGroupDao ldapGroupDao, LdapStructureDao ldapStructureDao) {
        log.debug("Loading Structure extracted from groups");
        return new StructureLoader(ldapGroupDao, ldapStructureDao);
    }

}
