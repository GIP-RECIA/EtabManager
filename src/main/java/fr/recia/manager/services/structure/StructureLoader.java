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

package fr.recia.manager.services.structure;

import com.google.common.collect.Sets;
import fr.recia.manager.ldap.StructureFromGroup;
import fr.recia.manager.ldap.StructureSirenDomain;
import fr.recia.manager.ldap.repository.LdapGroupDao;
import fr.recia.manager.ldap.repository.LdapStructureDao;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe qui charge au démarrage de l'app des infos sur les structures pour les retrouver efficacement par la suite :
 * - Retrouver la branche d'une structure par son UAI (pas pour les collectivités)
 * - Trouver la liste de toutes les structures d'une branche
 * - Retrouver le nom de groupe d'une structure par son UAI
 * - Retrouver les domaines d'une structure depuis son SIREN
 */
@NoArgsConstructor
@Slf4j
public class StructureLoader implements InitializingBean {

    private Map<String, Set<StructureFromGroup>> loadedStructuresByBranch;
    private Map<String, String> branchForLoadedStructures;
    private Map<String, String> groupNameForLoadedStructures;
    private Map<String, String> uaiToSirenForStructures;
    private List<StructureSirenDomain> loadedCollectivites;
    private Map<String, List<String>> domainsBySirenForStructures;
    private LdapGroupDao groupDao;
    private LdapStructureDao ldapStructureDao;

    public StructureLoader(LdapGroupDao groupDao, LdapStructureDao ldapStructureDao) {
        this.groupDao = groupDao;
        this.ldapStructureDao = ldapStructureDao;
        this.loadedStructuresByBranch = new HashMap<>();
        this.branchForLoadedStructures = new HashMap<>();
        this.uaiToSirenForStructures = new HashMap<>();
        this.groupNameForLoadedStructures = new HashMap<>();
        this.domainsBySirenForStructures = new HashMap<>();
        this.loadedCollectivites = new ArrayList<>();
    }

    @PostConstruct
    private void loadingStructures() {
        init();
    }

    private void init() {
        loadedCollectivites = ldapStructureDao.structuresCollectivites();
        Set<StructureFromGroup> loadedStructures = groupDao.getStructuresFromGroups();
        loadedStructures.forEach(structure -> {
            branchForLoadedStructures.put(structure.getUAI(), structure.getGroupBranch());
            groupNameForLoadedStructures.put(structure.getUAI(), structure.getDisplayName());
            if (loadedStructuresByBranch.containsKey(structure.getGroupBranch())) {
                loadedStructuresByBranch.get(structure.getGroupBranch()).add(structure);
            } else {
                loadedStructuresByBranch.put(structure.getGroupBranch(), Sets.newHashSet(structure));
            }
        });
        List<StructureSirenDomain> structureSirenDomains = ldapStructureDao.structuresFromSiren();
        for (StructureSirenDomain structureSirenDomain : structureSirenDomains) {
            domainsBySirenForStructures.put(structureSirenDomain.getSiren(), structureSirenDomain.getDomains());
            if(structureSirenDomain.getUai()!=null){
                uaiToSirenForStructures.put(structureSirenDomain.getUai(), structureSirenDomain.getSiren());
            }
        }
        log.debug(
            "loadedStructuresByBranch recap: {}",
            loadedStructuresByBranch.keySet().stream()
                .map(key -> "\t{ \"" + key + "\": " + loadedStructuresByBranch.get(key).size() + " }")
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
        );
        log.debug(
            "branchForLoadedStructures recap: {}",
            branchForLoadedStructures.keySet().stream()
                .map(key -> "\t{ \"" + key + "\": " + branchForLoadedStructures.get(key) + " }")
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
        );
        log.debug(
            "groupNameForLoadedStructures recap: {}",
            groupNameForLoadedStructures.keySet().stream()
                .map(key -> "\t{ \"" + key + "\": " + groupNameForLoadedStructures.get(key) + " }")
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
        );
        log.debug(
            "domainsBySirenForStructures recap: {}",
            domainsBySirenForStructures.keySet().stream()
                .map(key -> "\t{ \"" + key + "\": " + domainsBySirenForStructures.get(key) + " }")
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
        );
        log.debug(
            "uaiToSirenForStructures recap: {}",
            uaiToSirenForStructures.keySet().stream()
                .map(key -> "\t{ \"" + key + "\": " + uaiToSirenForStructures.get(key) + " }")
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
        );
    }

    public Set<StructureFromGroup> getStructuresOfBranch(String branchGroup) {
        return loadedStructuresByBranch.getOrDefault(branchGroup, Collections.emptySet());
    }

    public String getBranchOfStructure(String uai) {
        return branchForLoadedStructures.get(uai);
    }

    public String getGroupNameOfStructure(String uai) {
        return groupNameForLoadedStructures.get(uai);
    }

    public List<String> getDomainsOfStructure(String siren) {
        return domainsBySirenForStructures.get(siren);
    }

    public String getSirenByUai(String uai) {
        return uaiToSirenForStructures.get(uai);
    }

    public List<StructureSirenDomain> getAllCollectivites(){
        return this.loadedCollectivites;
    }

    @Override
    public void afterPropertiesSet() throws Exception {}

}
