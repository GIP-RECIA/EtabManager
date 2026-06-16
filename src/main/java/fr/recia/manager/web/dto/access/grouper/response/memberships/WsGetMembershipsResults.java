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

package fr.recia.manager.web.dto.access.grouper.response.memberships;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import fr.recia.manager.web.dto.access.grouper.response.ResponseMetadata;
import fr.recia.manager.web.dto.access.grouper.response.ResultMetadata;
import fr.recia.manager.web.dto.access.grouper.response.WsGroup;
import fr.recia.manager.web.dto.access.grouper.response.WsMembership;
import fr.recia.manager.web.dto.access.grouper.response.WsSubject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WsGetMembershipsResults {
    private ResponseMetadata responseMetadata;
    private ResultMetadata resultMetadata;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<WsGroup> wsGroups = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<WsMembership> wsMemberships = new ArrayList<>();
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<WsSubject> wsSubjects = new ArrayList<>();
}
