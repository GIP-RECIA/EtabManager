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

package fr.recia.manager.web.rest;

import fr.recia.manager.audit.AuditEvent;
import fr.recia.manager.audit.AuditService;
import fr.recia.manager.audit.EventType;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.services.access.RightsService;
import fr.recia.manager.services.db.StructureService;
import fr.recia.manager.web.dto.access.rights.AddOrDeleteMemberRequest;
import fr.recia.manager.web.dto.access.rights.ServiceAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/rights")
public class RightsController {

    @Autowired
    private RightsService rightsService;
    @Autowired
    private StructureService structureService;
    @Autowired
    private AuditService auditService;

    @GetMapping("/{id}")
    public ResponseEntity<List<ServiceAccess>> listRights(@AuthenticationPrincipal AppUser principal, @PathVariable Long id,
                                                          @RequestParam(required = false, defaultValue = "false") boolean showExternal,
                                                          @RequestParam(required = false, defaultValue = "true") boolean showAdmin) {
        log.debug("Listing rights for structure {}", id);
        final AStructure aStructure = structureService.getStructureDBFromId(id);
        final Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ_GROUP);
        if (allowedSiren.contains(aStructure.getSiren())) {
            final String etabGroup = rightsService.deductGroupNameFromStructure(aStructure);
            final String branch = rightsService.deductBranchFromStructure(aStructure);
            final List<ServiceAccess> rights = rightsService.getRights(branch, etabGroup, showExternal, showAdmin);
            log.debug("Rights for structure {} are {}", id, rights);
            return ResponseEntity.ok(rights);
        } else {
            log.warn("User {} is not authorized to list rights in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/services/{service}/roles/{role}/members")
    public ResponseEntity<Void> updateRights(@AuthenticationPrincipal AppUser principal, @PathVariable Long id,
                                             @PathVariable String service, @PathVariable String role,
                                             @RequestBody AddOrDeleteMemberRequest request) {
        log.debug("Updating rights for structure {}", id);
        final AStructure aStructure = structureService.getStructureDBFromId(id);
        final Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE_GROUP);
        if (allowedSiren.contains(aStructure.getSiren())) {
            final String etabGroup = rightsService.deductGroupNameFromStructure(aStructure);
            final String branch = rightsService.deductBranchFromStructure(aStructure);
            for (String memberToAdd : request.getMembersToAdd()) {
                log.debug("Adding right for member {} in structure {} for service {} for role {}", memberToAdd, id, service, role);
                rightsService.addRight(service, role, memberToAdd, branch, etabGroup, principal.getUsername());
            }
            for (String memberToRemove : request.getMembersToRemove()) {
                log.debug("Removing right for member {} in structure {} for service {} for role {}", memberToRemove, id, service, role);
                rightsService.removeRight(service, role, memberToRemove, branch, etabGroup, principal.getUsername());
            }
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.UPDATE_RIGHTS)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "service", service,
                        "role", role,
                        "request", request
                    ))
                    .build()
            );
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} is not authorized to update rights in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
