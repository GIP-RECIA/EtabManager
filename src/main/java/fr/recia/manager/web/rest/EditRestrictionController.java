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
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.repositories.structure.AStructureRepository;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.services.restriction.RestrictionService;
import fr.recia.manager.web.dto.restriction.RestrictionEtab;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/restriction")
public class EditRestrictionController {

    private final RestrictionService restrictionService;
    private final AStructureRepository<AStructure> aStructureRepository;
    private final AuditService auditService;

    public EditRestrictionController(RestrictionService restrictionService, AStructureRepository<AStructure> aStructureRepository, AuditService auditService) {
        this.restrictionService = restrictionService;
        this.aStructureRepository = aStructureRepository;
        this.auditService = auditService;
    }

    @PostMapping("/etab/{id}")
    public ResponseEntity<Void> editRestriction(@AuthenticationPrincipal AppUser principal, @PathVariable Long id, @RequestBody RestrictionEtab request) {
        final AStructure aStructure = aStructureRepository.findById(id).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE_RENTREE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            // TODO : gérer le cas des collectivités
            restrictionService.setNewRestriction(((Etablissement) aStructure).getUai(), request);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.UPDATE_RESTRICTIONS)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "request", request
                    ))
                    .build()
            );
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} is not authorized to edit restriction in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/etab/{id}")
    public ResponseEntity<RestrictionEtab> listRestrictions(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        final AStructure aStructure = aStructureRepository.findById(id).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ_RENTREE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            // TODO : gérer le cas des collectivités
            return ResponseEntity.ok(restrictionService.getRestrictions(((Etablissement) aStructure).getUai()));
        } else {
            log.warn("User {} is not authorized to list restrictions in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
