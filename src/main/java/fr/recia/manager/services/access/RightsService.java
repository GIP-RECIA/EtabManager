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

package fr.recia.manager.services.access;

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.configuration.Constants;
import fr.recia.manager.configuration.bean.RoleProperties;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.personne.QAPersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.repositories.personne.APersonneRepository;
import fr.recia.manager.services.exceptions.UnauthorizedGroupModificationException;
import fr.recia.manager.services.exceptions.UnauthorizedPeopleModificationException;
import fr.recia.manager.services.structure.StructureLoader;
import fr.recia.manager.utils.GroupPathGenerator;
import fr.recia.manager.web.dto.access.grouper.response.WsSubject;
import fr.recia.manager.web.dto.access.grouper.response.add.WsAddMemberResponse;
import fr.recia.manager.web.dto.access.grouper.response.memberships.WsGetMembershipsResponse;
import fr.recia.manager.web.dto.access.grouper.response.remove.WsDeleteMemberResponse;
import fr.recia.manager.web.dto.access.rights.Member;
import fr.recia.manager.web.dto.access.rights.Right;
import fr.recia.manager.web.dto.access.rights.ServiceAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RightsService {

    private final AppProperties appProperties;
    private final GrouperService grouperService;
    private final Map<String, String> invertedTemplateCache;
    private final APersonneRepository<APersonne> aPersonneRepository;
    private final StructureLoader structureLoader;

    public RightsService(AppProperties appProperties, GrouperService grouperService, APersonneRepository<APersonne> aPersonneRepository, StructureLoader structureLoader) {
        this.appProperties = appProperties;
        this.grouperService = grouperService;
        this.aPersonneRepository = aPersonneRepository;
        this.structureLoader = structureLoader;
        this.invertedTemplateCache = new HashMap<>();
    }

    private List<Member> getGroupsFullPathAndCache(List<String> memberList, String branch, String etabName) {
        List<Member> groups = new ArrayList<>();
        for (String canonicalName : memberList) {
            String resolvedName = GroupPathGenerator.groupPathFromTemplate(canonicalName, branch, etabName);
            groups.add(new Member(resolvedName, appProperties.getRights().getDeclaredGroupsMap().get(canonicalName), "", false, false));
            // Add to inverted cache
            invertedTemplateCache.put(resolvedName, canonicalName);
        }
        return groups;
    }

    private boolean isGroup(String memberName) {
        return !(memberName.startsWith("F") && memberName.length() == 8);
    }

    /**
     * Donne la branche d'une structure (pour former un groupe grouper)
     */
    public String deductBranchFromStructure(AStructure aStructure) {
        log.debug("Retrieving branch for structure {}", aStructure.getId());
        final String branch = structureLoader.getBranchOfStructure(((Etablissement) aStructure).getUai());
        log.debug("Branch for structure {} is {}", aStructure.getId(), branch);
        return branch;
    }

    /**
     * Donne le nom de grouper d'une structure (pour former un groupe grouper)
     */
    public String deductGroupNameFromStructure(AStructure aStructure) {
        log.debug("Retrieving group name for structure {}", aStructure.getId());
        final String etabGroupLeft = structureLoader.getGroupNameOfStructure(((Etablissement) aStructure).getUai());
        final String etabGroupRight = ((Etablissement) aStructure).getUai();
        String groupName = etabGroupLeft + "_" + etabGroupRight;
        log.debug("Group name for structure {} is {}", aStructure.getId(), groupName);
        return groupName;
    }


    /**
     * Donne la liste de tous les droits des services pour le front d'après la configuration
     */
    public List<ServiceAccess> getRights(String branch, String etabGroup, boolean showExternal, boolean showAdmin) {
        List<ServiceAccess> serviceAccessList = new ArrayList<>();
        for (String service : appProperties.getRights().getServices().keySet()) {
            ServiceAccess serviceAccess = new ServiceAccess();
            serviceAccess.setService(service);
            serviceAccess.setName(appProperties.getRights().getServices().get(service).getName());
            List<Right> rights = new ArrayList<>();
            final Map<String, RoleProperties> rolesByService = appProperties.getRights().getServices().get(service).getRoles();
            for (String role : rolesByService.keySet()) {
                try {
                    final RoleProperties roleProperties = rolesByService.get(role);
                    Right right = new Right();
                    right.setDescription(roleProperties.getDescription());
                    right.setRole(role);
                    // Ajout des groupes par configuration
                    List<Member> possibleGroups = new ArrayList<>();
                    if(roleProperties.getPossibleGroups() != null){
                        possibleGroups.addAll(getGroupsFullPathAndCache(roleProperties.getPossibleGroups(), branch, etabGroup));
                    }
                    right.setPossibleGroups(possibleGroups);
                    List<Member> mandatoryGroups = new ArrayList<>();
                    if(roleProperties.getMandatoryGroups() != null){
                        mandatoryGroups.addAll(getGroupsFullPathAndCache(roleProperties.getMandatoryGroups(), branch, etabGroup));
                    }
                    right.setMandatoryGroups(mandatoryGroups);
                    // Ajout des membres déjà présents via grouper
                    List<Member> currentMembers = new ArrayList<>();
                    WsGetMembershipsResponse wsGetMembershipsResponse = grouperService.listMemberships(GroupPathGenerator.groupPathFromTemplate(
                        roleProperties.getTargetGroup(), branch, etabGroup), showExternal, true, true).getBody();
                    List<WsSubject> wsSubjectList = wsGetMembershipsResponse.getResults().getWsSubjects();
                    log.info("Subjets retrieved for group {} are : {}", roleProperties.getTargetGroup(), wsSubjectList);
                    // On récupère toutes les infos sur les users avant pour des raisons d'optimisation et éviter de faire une requête par user
                    List<String> userIds = wsSubjectList.stream()
                        .filter(wsSubject -> !Constants.GROUPER_SOURCEID_GROUP.equals(wsSubject.getSourceId()))
                        .map(WsSubject::getId)
                        .distinct()
                        .collect(Collectors.toList());
                    Map<String, APersonne> personneMap = new HashMap<>();
                    if (!userIds.isEmpty()) {
                        List<APersonne> personnes = (List<APersonne>) aPersonneRepository.findAll(QAPersonne.aPersonne.uid.in(userIds));
                        personneMap = personnes.stream().collect(Collectors.toMap(APersonne::getUid, Function.identity()));
                    }
                    for (WsSubject wsSubject : wsSubjectList) {
                        boolean isUser = !wsSubject.getSourceId().equals(Constants.GROUPER_SOURCEID_GROUP);
                        Member member;
                        if (isUser) {
                            APersonne aPersonne = personneMap.get(wsSubject.getId());
                            member = new Member(wsSubject.getId(), aPersonne.getDisplayName(), aPersonne.getEmail(), true,true);
                        } else {
                            if (invertedTemplateCache.containsKey(wsSubject.getName())) {
                                member = new Member(wsSubject.getName(), appProperties.getRights().getDeclaredGroupsMap().get(invertedTemplateCache.get(wsSubject.getName())), "", false, false);
                            } else {
                                member = new Member(wsSubject.getName(), wsSubject.getName(), "", false, true);
                            }
                        }
                        currentMembers.add(member);
                    }
                    right.setCurrentMembers(currentMembers);
                    right.setAllowPeople(rolesByService.get(role).isAllowPeople());
                    right.setAdmin(rolesByService.get(role).isAdmin());
                    if (right.isAdmin() && !showAdmin) {
                        log.debug("Admin right but do not show admin rights, skipping...");
                    } else {
                        rights.add(right);
                    }
                } catch (Exception e) {
                    log.warn("Could not get right because an error occured", e);
                }
            }
            serviceAccess.setRights(rights);
            if (!serviceAccess.getRights().isEmpty()) {
                serviceAccessList.add(serviceAccess);
            }
        }
        return serviceAccessList;
    }

    /**
     * Ajoute un droit sur un service pour un membre donné
     */
    public ResponseEntity<WsAddMemberResponse> addRight(String service, String role, String memberToAdd, String branch, String etabGroup, String actAs) {
        // Récupération du targetGroup depuis la conf pour être sûr qu'on ajoute sur le bon
        String targetGroup = GroupPathGenerator.groupPathFromTemplate(appProperties.getRights().getServices().get(service).getRoles().get(role).getTargetGroup(), branch, etabGroup);
        // Vérification qu'on a bien le droit d'ajouter le membre en question
        // Le défaut de ce sytème de vérification c'est qu'il faut les avoir chargé dans le cache d'abord (donc avec un GET) mais normalement
        // on est pas censé faire des POST directement si on passe par l'UI
        if (isGroup(memberToAdd)) {
            if (!appProperties.getRights().getServices().get(service).getRoles().get(role).getPossibleGroups().contains(invertedTemplateCache.get(memberToAdd))
                && !appProperties.getRights().getServices().get(service).getRoles().get(role).getMandatoryGroups().contains(invertedTemplateCache.get(memberToAdd))) {
                throw new UnauthorizedGroupModificationException("Can't add member " + memberToAdd + " for role " + role + " in service " + service);
            }

        } else {
            if (!appProperties.getRights().getServices().get(service).getRoles().get(role).isAllowPeople()) {
                throw new UnauthorizedPeopleModificationException("Can't add member " + memberToAdd + " for role" + role + " in service " + service);
            }
        }
        return grouperService.addMember(targetGroup, memberToAdd, isGroup(memberToAdd), actAs);
    }

    /**
     * Retire un droit sur un service pour un membre donné
     */
    public ResponseEntity<WsDeleteMemberResponse> removeRight(String service, String role, String memberToRemove, String branch, String etabGroup, String actAs) {
        // Récupération du targetGroup depuis la conf pour être sûr qu'on ajoute sur le bon
        String targetGroup = GroupPathGenerator.groupPathFromTemplate(appProperties.getRights().getServices().get(service).getRoles().get(role).getTargetGroup(), branch, etabGroup);
        // Vérification qu'on a bien le droit de supprimer le membre en question
        if (isGroup(memberToRemove)) {
            if (!appProperties.getRights().getServices().get(service).getRoles().get(role).getPossibleGroups().contains(invertedTemplateCache.get(memberToRemove))) {
                throw new UnauthorizedGroupModificationException("Can't remove member " + memberToRemove + " for role" + role + " in service " + service);
            }
        } else {
            if (!appProperties.getRights().getServices().get(service).getRoles().get(role).isAllowPeople()) {
                throw new UnauthorizedPeopleModificationException("Can't remove member " + memberToRemove + " for role" + role + " in service " + service);
            }
        }
        return grouperService.removeMember(targetGroup, memberToRemove, isGroup(memberToRemove), actAs);
    }
}
