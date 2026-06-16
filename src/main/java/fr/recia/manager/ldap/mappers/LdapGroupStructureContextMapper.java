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

import fr.recia.manager.configuration.bean.CustomLdapProperties;
import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.ldap.ExternalGroupHelper;
import fr.recia.manager.ldap.StructureFromGroup;
import fr.recia.manager.ldap.StructureKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author GIP RECIA - Julien Gribonvald 3 juin. 2015
 */
@Slf4j
public class LdapGroupStructureContextMapper implements ContextMapper<StructureFromGroup> {

    private ExternalGroupHelper externalGroupHelper;
    private final CustomLdapProperties ldapProperties;

    public LdapGroupStructureContextMapper(ExternalGroupHelper externalGroupHelper, CustomLdapProperties ldapProperties) {
        super();
        this.externalGroupHelper = externalGroupHelper;
        this.ldapProperties = ldapProperties;
    }

    @Override
    public StructureFromGroup mapFromContext(Object ctx) {
        Assert.notNull(externalGroupHelper, "The externalGroupHelper should not be null !");
        DirContextAdapter context = (DirContextAdapter) ctx;
        final String groupId = context.getStringAttribute(externalGroupHelper.getGroupIdAttribute());
        StructureFromGroup structure = new StructureFromGroup();
        structure.setStructureKey(new StructureKey(groupId, getCategorieStructure(groupId)));
        // extract infos from pattern : récupération du groupe 1 pour la branche, le groupe 3 à split pour le displayName et l'UAI, le groupe 3 pour avoir le nom du groupe de l'établissement
        Matcher matcher = ldapProperties.getGroupBranch().getStructureProperties().getStructureFromGroupPattern().matcher(groupId);
        if (matcher.find() && matcher.groupCount() >= 4) {
            structure.setGroupBranch(matcher.group(1));
            structure.setDisplayName(matcher.group(4));
            // test Branche == coll car groupe 6 vide dans ce cas, et renseigné obligatoirement sinon
            if (matcher.groupCount() == 6) {
                structure.setUAI(matcher.group(6));
            }
        }

        return structure;
    }

    private CategorieStructure getCategorieStructure(final String group) {
        for (Map.Entry<CategorieStructure, Pattern> entry : ldapProperties.getGroupBranch().getStructureProperties().getStructureCategoriesPatterns().entrySet()) {
            if (entry.getValue().matcher(group).matches()) return entry.getKey();
        }
        throw new IllegalArgumentException(String.format("Extracted Structure type unknown from %s", group));
    }

}
