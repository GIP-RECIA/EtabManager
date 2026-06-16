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

package fr.recia.manager.ldap.mappers;

import fr.recia.manager.ldap.StructureSirenDomain;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

@Component
public class StructureSirenDomainAttributesMapper implements AttributesMapper<StructureSirenDomain> {

    @Override
    public StructureSirenDomain mapFromAttributes(Attributes attrs) throws NamingException {
        StructureSirenDomain entry = new StructureSirenDomain();
        Attribute sirenAttr = attrs.get("ENTStructureSIREN");
        if (sirenAttr != null) {
            entry.setSiren((String) sirenAttr.get());
        }
        Attribute uaiAttr = attrs.get("ENTStructureUAI");
        if (uaiAttr != null) {
            entry.setUai((String) uaiAttr.get());
        }
        Attribute domainesAttr = attrs.get("ESCODomaines");
        if (domainesAttr != null) {
            List<String> domaines = new ArrayList<>();
            NamingEnumeration<?> allValues = domainesAttr.getAll();
            while (allValues.hasMore()) {
                domaines.add((String) allValues.next());
            }
            entry.setDomains(domaines);
        }

        return entry;
    }
}
