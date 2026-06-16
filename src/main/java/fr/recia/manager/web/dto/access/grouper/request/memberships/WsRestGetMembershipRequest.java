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

package fr.recia.manager.web.dto.access.grouper.request.memberships;

import fr.recia.manager.web.dto.access.grouper.request.WsGroupLookup;
import lombok.Data;

import java.util.List;

@Data
public class WsRestGetMembershipRequest {
    private List<WsGroupLookup> wsGroupLookups;
    private String memberFilter;
    private String includeSubjectDetail;
    private List<String> sourceIds;

    public WsRestGetMembershipRequest(String subjectId, String memberFilter, String includeSubjectDetail, List<String> sourceIds) {
        this.wsGroupLookups = List.of(new WsGroupLookup(subjectId));
        this.memberFilter = memberFilter;
        this.includeSubjectDetail = includeSubjectDetail;
        this.sourceIds = sourceIds;
    }
}
