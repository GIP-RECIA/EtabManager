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

import fr.recia.manager.configuration.bean.CustomLdapProperties;
import fr.recia.manager.ldap.StructureSirenDomain;
import fr.recia.manager.ldap.mappers.StructureSirenDomainAttributesMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.LdapTemplate;

import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class LdapStructureDao {

    private LdapTemplate ldapTemplate;
    private StructureSirenDomainAttributesMapper structureSirenDomainAttributesMapper;
    private CustomLdapProperties ldapProperties;

    public List<StructureSirenDomain> structuresFromSiren() {
        return ldapTemplate.search(
            ldapProperties.getStructureBranch().getBase(),
            ldapProperties.getStructureBranch().getAllStructuresBySirenFilter(),
            structureSirenDomainAttributesMapper
        );
    }

    public List<StructureSirenDomain> structuresCollectivites() {
        return ldapTemplate.search(
            ldapProperties.getStructureBranch().getBase(),
            ldapProperties.getStructureBranch().getStructuresCollFilter(),
            structureSirenDomainAttributesMapper
        );
    }

}
