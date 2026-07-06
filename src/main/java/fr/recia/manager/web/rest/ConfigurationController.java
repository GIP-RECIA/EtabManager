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

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.services.db.StructureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/config")
public class ConfigurationController {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private StructureService structureService;

    @GetMapping()
    public ResponseEntity<Object> getConfiguration() {
        Map<String, Object> data = new HashMap<>();
        data.put("front", appProperties.getFront());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/principal")
    public ResponseEntity<List<AppRole>> getPrincipalRights(@AuthenticationPrincipal AppUser principal) {
        List<AppRole> rolesForFront = new ArrayList<>();
        for(AppRole appRole : principal.getRightsForEtabs().keySet()){
            if(!principal.getRightsForEtabs().get(appRole).isEmpty()){
                rolesForFront.add(appRole);
            }
        }
        rolesForFront.addAll(principal.getGlobalRights());
        return new ResponseEntity<>(rolesForFront, HttpStatus.OK);
    }

    @GetMapping("/principal/{id}")
    public ResponseEntity<List<AppRole>> getPrincipalRightsForEtab(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        AStructure structure = structureService.getStructureDBFromId(id);
        List<AppRole> rolesForFront = new ArrayList<>();
        for(AppRole appRole : principal.getRightsForEtabs().keySet()){
            if(principal.getRightsForEtabs().get(appRole).contains(structure.getSiren())){
                rolesForFront.add(appRole);
            }
        }
        rolesForFront.addAll(principal.getGlobalRights());
        return new ResponseEntity<>(rolesForFront, HttpStatus.OK);
    }
}
