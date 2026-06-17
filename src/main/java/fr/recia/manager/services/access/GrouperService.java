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
import fr.recia.manager.web.dto.access.grouper.request.add.WsRestAddMemberRequestWrapper;
import fr.recia.manager.web.dto.access.grouper.request.delete.WsRestDeleteMemberRequestWrapper;
import fr.recia.manager.web.dto.access.grouper.request.find.WsRestFindGroupsRequestWrapper;
import fr.recia.manager.web.dto.access.grouper.request.memberships.WsRestGetMembershipRequestWrapper;
import fr.recia.manager.web.dto.access.grouper.response.add.WsAddMemberResponse;
import fr.recia.manager.web.dto.access.grouper.response.find.WsFindGroupsResponse;
import fr.recia.manager.web.dto.access.grouper.response.memberships.WsGetMembershipsResponse;
import fr.recia.manager.web.dto.access.grouper.response.remove.WsDeleteMemberResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GrouperService {

    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    public GrouperService(RestTemplate restTemplateGrouper, AppProperties appProperties) {
        this.restTemplate = restTemplateGrouper;
        this.appProperties = appProperties;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Constants.GROUPER_MEDIA_TYPE));
        headers.setAccept(Collections.singletonList(MediaType.valueOf(Constants.GROUPER_MEDIA_TYPE)));
        headers.setBasicAuth(appProperties.getGrouper().getUsername(), appProperties.getGrouper().getPassword());
        return headers;
    }

    /**
     * Récupération de l'id d'un groupe par son chemin ID
     */
    private String findGroupId(String groupName) {
        String url = appProperties.getGrouper().getBaseUrl() + "/groups";
        WsRestFindGroupsRequestWrapper payload = new WsRestFindGroupsRequestWrapper(groupName);
        HttpEntity<?> entity = new HttpEntity<>(payload, createHeaders());
        ResponseEntity<WsFindGroupsResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, WsFindGroupsResponse.class);
        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().getWsFindGroupsResults().getGroupResults().getFirst().getUuid();
        }
        return null;
    }

    /**
     * Lister les membres d'un groupe en détail
     */
    public ResponseEntity<WsGetMembershipsResponse> listMemberships(String groupName, boolean includeIndirect, boolean includeSubjectNames, boolean includeUsers) {
        String includeSubjectDetail = "F";
        if (includeSubjectNames) {
            includeSubjectDetail = "T";
        }
        String memberFilter = "Immediate";
        if (includeIndirect) {
            memberFilter = "All";
        }
        List<String> sourceIds = new ArrayList<>(List.of("g:gsa"));
        if (includeUsers) {
            sourceIds.add("esco:ldap");
        }
        String url = appProperties.getGrouper().getBaseUrl() + "/memberships";
        WsRestGetMembershipRequestWrapper payload = new WsRestGetMembershipRequestWrapper(groupName, memberFilter, includeSubjectDetail, sourceIds);
        HttpEntity<?> entity = new HttpEntity<>(payload, createHeaders());
        return restTemplate.exchange(url, HttpMethod.POST, entity, WsGetMembershipsResponse.class);
    }

    /**
     * Ajouter un membre dans un groupe
     *
     * @param groupName Le nom du groupe dans lequel on veut ajouter le membre
     * @param subjectId Soit l'uid de la personne à ajouter, soit le chemin ID du groupe à ajouter
     * @param isGroup   Un booléen qui indique si c'est un groupe ou une personne qui est à ajouter
     * @return
     */
    public ResponseEntity<WsAddMemberResponse> addMember(String groupName, String subjectId, boolean isGroup, String actAs) {
        String url = appProperties.getGrouper().getBaseUrl() + "/groups/" + groupName + "/members";
        HttpHeaders headers = createHeaders();
        String sourceId = Constants.GROUPER_SOURCEID_USER;
        if (isGroup) {
            sourceId = Constants.GROUPER_SOURCEID_GROUP;
            subjectId = findGroupId(subjectId);
        }
        WsRestAddMemberRequestWrapper payload = new WsRestAddMemberRequestWrapper(subjectId, sourceId, actAs);
        HttpEntity<?> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, WsAddMemberResponse.class);
    }


    /**
     * Supprimer un membre d'un groupe
     *
     * @param groupName Le nom du groupe duquel on veut supprimer le membre
     * @param subjectId Soit l'uid de la personne à ajouter, soit le chemin ID du groupe à supprimer
     * @param isGroup   Un booléen qui indique si c'est un groupe ou une personne qui est à supprimer
     * @return
     */
    public ResponseEntity<WsDeleteMemberResponse> removeMember(String groupName, String subjectId, boolean isGroup, String actAs) {
        String url = appProperties.getGrouper().getBaseUrl() + "/groups/" + groupName + "/members";
        HttpHeaders headers = createHeaders();
        String sourceId = Constants.GROUPER_SOURCEID_USER;
        if (isGroup) {
            sourceId = Constants.GROUPER_SOURCEID_GROUP;
            subjectId = findGroupId(subjectId);
        }
        WsRestDeleteMemberRequestWrapper payload = new WsRestDeleteMemberRequestWrapper(subjectId, sourceId, actAs);
        HttpEntity<?> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, WsDeleteMemberResponse.class);
    }

}
