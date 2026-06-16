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

import fr.recia.manager.ldap.LdapUser;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

@Component
public class PeopleResponseAttributesMapper implements AttributesMapper<LdapUser> {

    @Override
    public LdapUser mapFromAttributes(Attributes attrs) throws NamingException {
        LdapUser entry = new LdapUser();
        Attribute sirenAttr = attrs.get("ESCOSIRENCourant");
        if (sirenAttr != null) {
            entry.setSirenCourant((String) sirenAttr.get());
        }
        Attribute groupsAttr = attrs.get("isMemberOf");
        if (groupsAttr != null) {
            List<String> groups = new ArrayList<>();
            NamingEnumeration<?> allValues = groupsAttr.getAll();
            while (allValues.hasMore()) {
                groups.add((String) allValues.next());
            }
            entry.setGroups(groups);
        }

        return entry;
    }
}
