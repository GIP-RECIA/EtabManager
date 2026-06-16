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

package fr.recia.manager.ldap.repository;

import com.google.common.collect.Sets;
import fr.recia.manager.configuration.bean.CustomLdapProperties;
import fr.recia.manager.ldap.ExternalGroupHelper;
import fr.recia.manager.ldap.StructureFromGroup;
import fr.recia.manager.ldap.mappers.LdapGroupStructureContextMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;

import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Data
@AllArgsConstructor
@Slf4j
public class LdapGroupDao {

    private LdapTemplate ldapTemplate;
    private ExternalGroupHelper externalGroupHelper;
    private CustomLdapProperties ldapProperties;

    public Set<StructureFromGroup> getStructuresFromGroups() {
        HardcodedFilter filter = new HardcodedFilter(ldapProperties.getGroupBranch().getStructureProperties().getFilterGroupsOfStructure());
        if (log.isDebugEnabled()) {
            log.debug("LDAP filter applied {} ", filter.encode());
        }
        ContextMapper<StructureFromGroup> mapper = new LdapGroupStructureContextMapper(this.externalGroupHelper, ldapProperties);
        LdapQuery query = LdapQueryBuilder.query()
            .attributes(externalGroupHelper.getAttributes().toArray(new String[0]))
            .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
        return Sets.newHashSet(ldapTemplate.search(query, mapper));
    }

}
