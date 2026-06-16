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
import fr.recia.manager.security.AppRole;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.services.db.StructureService;
import fr.recia.manager.services.export.EsidocExportService;
import fr.recia.manager.web.dto.export.EsidocWSResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/api/export")
public class ExportController {

    @Autowired
    private StructureService structureService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private EsidocExportService esidocExportService;

    @GetMapping("/esidoc/{id}")
    public ResponseEntity<EsidocWSResponse> exportEsidoc(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        final AStructure aStructure = structureService.getStructureDBFromId(id);
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        if (allowedSiren.contains(aStructure.getSiren())) {
            final EsidocWSResponse esidocWSResponse = esidocExportService.exportForEtab(((Etablissement) aStructure).getUai());
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.EXPORT_ESIDOC)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "esidocWSResponse", esidocWSResponse
                    ))
                    .build());
            if(!esidocWSResponse.getSuccess().equals("fail")){
                return ResponseEntity.ok(esidocWSResponse);
            } else {
                return ResponseEntity.internalServerError().body(esidocWSResponse);
            }
        } else {
            log.warn("User {} is not authorized to export esidoc in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
