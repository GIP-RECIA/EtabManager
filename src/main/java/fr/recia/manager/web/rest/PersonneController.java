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

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import fr.recia.manager.audit.AuditEvent;
import fr.recia.manager.audit.AuditService;
import fr.recia.manager.audit.EventType;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.services.db.AddPersonneService;
import fr.recia.manager.services.db.FonctionService;
import fr.recia.manager.services.db.PersonneService;
import fr.recia.manager.services.db.StructureService;
import fr.recia.manager.web.dto.enseignement.EnseignementModifyRequest;
import fr.recia.manager.web.dto.enseignement.FormationModifyRequest;
import fr.recia.manager.web.dto.function.FonctionAction;
import fr.recia.manager.web.dto.function.JsonAdditionalFonctionBody;
import fr.recia.manager.web.dto.user.PersonneDetailDto;
import fr.recia.manager.web.dto.user.PersonneExportDto;
import fr.recia.manager.web.dto.user.SearchedPersonneDto;
import fr.recia.manager.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/api/personne")
public class PersonneController {

    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private PersonneService personneService;
    @Autowired
    private AddPersonneService addPersonneService;
    @Autowired
    private StructureService structureService;
    @Autowired
    private AuditService auditService;

    @GetMapping
    public ResponseEntity<List<SearchedPersonneDto>> searchPersonne(@AuthenticationPrincipal AppUser principal,
                                                                    @RequestParam(value = "name") String name,
                                                                    @RequestParam(value = "etab", required = false) Long etabId,
                                                                    @RequestParam(value = "not_in_etab", required = false) Long notInEtabId,
                                                                    @RequestParam(value = "staff", required = false, defaultValue = "False") boolean staff,
                                                                    @RequestParam(value = "check_rights", required = false, defaultValue = "True") boolean checkRights) {
        List<DatabasePersonneDto> personnes;
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        boolean canSearchByUid = principal.getGlobalRights().contains(AppRole.SEARCH_UID);

        // Cas de la recherche dans un établissement
        if(etabId != null){
            AStructure aStructure = structureService.getStructureDBFromId(etabId);
            if(allowedSiren.contains(aStructure.getSiren())){
                // Ici pas besoin de revérifier les droits car ils sont déjà véirifiés implicitement comme on recherche uniquement dans l'établissement
                if(staff){
                    personnes = personneService.searchPersonneInEtabInStaffCategories(name, Set.of(etabId), canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneInEtab(name, Set.of(etabId), canSearchByUid);
                }
            } else {
                log.warn("User {} is not authorized to view etab {}", principal.getUsername(), etabId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        // Cas de la recherche ou on évite un établissement
        else if(notInEtabId != null){
            // recherche hors d'un établissement sans vérifier les droits = recherche utilisée pour le rattachement
            if(!checkRights){
                if(staff){
                    personnes = personneService.searchPersonneNotInEtabInStaffCategories(name, Set.of(notInEtabId), canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneNotInEtab(name, Set.of(notInEtabId), canSearchByUid);
                }
            } else {
                if(staff){
                    personnes = personneService.searchPersonneNotInEtabButInSirenInStaffCategories(name, Set.of(notInEtabId), allowedSiren, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneNotInEtabButInSiren(name, Set.of(notInEtabId), allowedSiren, canSearchByUid);
                }
            }
        }

        // Cas de la recherche globale (pas dans un établissement, et on évite pas non plus un établissement)
        else {
            if(!checkRights){
                if(staff){
                    personnes = personneService.searchPersonneInStaffCategories(name, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonne(name, canSearchByUid);
                }
            } else {
                // On ne cherche que les personnes dans les sirens autorisés
                if(staff){
                    personnes = personneService.searchPersonneInEtabBySirenInStaffCategories(name, allowedSiren, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneInEtabBySiren(name, allowedSiren, canSearchByUid);
                }
            }
        }

        List<SearchedPersonneDto> searchedPersonnes = new ArrayList<>();
        for(DatabasePersonneDto databasePersonneDto : personnes){
            searchedPersonnes.add(new SearchedPersonneDto(databasePersonneDto));
        }
        return new ResponseEntity<>(searchedPersonnes, HttpStatus.OK);
    }

    @GetMapping(value = "/export")
    public ResponseEntity<MappingJacksonValue> exportPersonnes(@AuthenticationPrincipal AppUser principal,
                                                             @RequestParam List<Long> ids,
                                                             @RequestParam Long etab,
                                                             @RequestParam List<String> columns){
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        List<PersonneExportDto> personnesExport = new ArrayList<>();
        for(Long id : ids){
            APersonne personne = personneService.getPersonne(id);
            PersonneDetailDto personneDetailDto = personneService.getFullPersonne(id, personne, true, allowedSiren);
            PersonneExportDto personneExportDto = new PersonneExportDto(personneDetailDto, etab);
            personnesExport.add(personneExportDto);
        }
        // Projection pour garder seulement certaines colonnes
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<>(columns));
        FilterProvider filters = new SimpleFilterProvider().addFilter("personneExportFilter", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(personnesExport);
        mapping.setFilters(filters);
        return new ResponseEntity<>(mapping, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonneDetailDto> getPersonne(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        // TODO : la route est aussi appellée lors du rattachement sauf qu'on a pas forcément les droits sur l'étab duquel la personne est de base
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de voir la personne = que sur une des structures dans laquelle est la personne on a les droits de visualisation
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        boolean canRead = false;
        // Booléen qui indique si on affiche l'uid ou non
        boolean showUid = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
            if (principal.getRightsForEtabs().get(AppRole.VIEW_UID).contains(aStructure.getSiren())) {
                showUid = true;
            }
        }
        if (canRead) {
            PersonneDetailDto personneDetailDto = personneService.getFullPersonne(id, personne, showUid, allowedSiren);
            return new ResponseEntity<>(personneDetailDto, HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to view person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/{id}/reset")
    public ResponseEntity<PersonneDetailDto> resetPersonne(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
                break;
            }
        }
        if (canModify) {
            boolean ok = personneService.resetPersonne(personne);
            if(ok){
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.RESET_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to reset person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockPerson(@AuthenticationPrincipal AppUser principal, @PathVariable Long id){
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        boolean canRead = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
        }
        if (canRead) {
            boolean ok = personneService.lockPerson(personne);
            if(ok){
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.LOCK_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to lock person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<Etat> unlockPerson(@AuthenticationPrincipal AppUser principal, @PathVariable Long id){
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        boolean canRead = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
        }
        if (canRead) {
            Optional<Etat> restoredEtat = personneService.unlockPerson(personne);
            if(restoredEtat.isPresent()){
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.UNLOCK_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(restoredEtat.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to unlock person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/unlock")
    public ResponseEntity<Void> unlockPersons(@AuthenticationPrincipal AppUser principal, @RequestParam List<Long> ids){
        // TODO : débloquage de masse en une seule requête pour être plus efficace
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        for(Long id : ids){
            APersonne personne = personneService.getPersonne(id);
            if (personne == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
            boolean canRead = false;
            // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
            for (AStructure aStructure : personne.getListeStructures()) {
                if (allowedSiren.contains(aStructure.getSiren())) {
                    canRead = true;
                }
            }
            if (canRead) {
                personneService.unlockPerson(personne);
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.UNLOCK_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
            } else {
                log.warn("User {} is not authorized to unlock person {}", principal.getUsername(), id);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> putInDeleteState(@AuthenticationPrincipal AppUser principal, @PathVariable Long id){
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            boolean ok = personneService.putInDeleteState(personne);
            if (ok) {
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.DELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to put person {} in delete state", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<Void> forceDelete(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            boolean ok = personneService.forceDelete(personne);
            if (ok) {
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.FORCEDELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to force delete person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}/undo")
    public ResponseEntity<Etat> undoDelete(@AuthenticationPrincipal AppUser principal, @PathVariable Long id){
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            Optional<Etat> restoredEtat = personneService.undoDelete(personne);
            if(restoredEtat.isPresent()){
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.UNDODELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(restoredEtat.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to undo delete for person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<String> addPersonne(@AuthenticationPrincipal AppUser principal, @RequestBody UserCreation userCreation) {
        // Vérifier qu'on a les droits d'ajouter la personne = que sur la structure sur laquelle on veut l'ajouter on a les droits d'écriture
        AStructure aStructure = structureService.getStructureDBFromId(userCreation.getStructureRattachement());
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            try {
                APersonne apersonne = addPersonneService.addPersonne(userCreation);
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.CREATE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(apersonne.getId()))
                        .payload(Map.of(
                            "uid", apersonne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(Long.toString(apersonne.getId()), HttpStatus.CREATED);
            } catch (Exception e){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.warn("User {} is not authorized to add person in {}", principal.getUsername(), userCreation.getStructureRattachement());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/{id}/fonction")
    public ResponseEntity<Void> setPersonneAdditionalFonctions(@AuthenticationPrincipal AppUser principal, @PathVariable Long id, @RequestBody JsonAdditionalFonctionBody body) {
        // Vérifier qu'on a les droits de modifier la personne = que sur la structure dans laquelle on veut modifier la fonction on a les droits d'écriture
        AStructure aStructure = structureService.getStructureDBFromId(body.getStructureId());
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            if(body.getRequiredAction().equals(FonctionAction.attach) && !principal.getGlobalRights().contains(AppRole.ATTACH)){
                log.warn("User {} is not authorized to attach funtion for user {} in {}", principal.getUsername(), id, body.getStructureId());
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            boolean success = fonctionService.saveAdditionalFonctions(
                id,
                body.getStructureId(),
                body.getToAddFunctions(),
                body.getToDeleteFunctions(),
                body.getRequiredAction(),
                principal.getRightsForEtabs().get(AppRole.ADMIN_FONCTIONS).contains(aStructure.getSiren())
            );
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.MODIFY_FONCTION)
                    .actor(principal.getUsername())
                    .target(id.toString())
                    .payload(Map.of(
                        "body", body,
                        "success", success)
                    )
                    .build()
            );
            if (!success) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to modify funtion for user {} in {}", principal.getUsername(), id, body.getStructureId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/{id}/enseignement")
    public ResponseEntity<Void> setPersonneEnseignements(@AuthenticationPrincipal AppUser principal, @PathVariable Long id, @RequestBody EnseignementModifyRequest body) {
        AStructure aStructure = structureService.getStructureDBFromId(body.getStructure());
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            boolean success = addPersonneService.modifyEnseignements(id, body);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.MODIFY_ENSEIGNEMENT)
                    .actor(principal.getUsername())
                    .target(id.toString())
                    .payload(Map.of(
                        "body", body,
                        "success", success)
                    )
                    .build()
            );
            if (!success) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to modify enseignements for user {} in {}", principal.getUsername(), id, body.getStructure());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/{id}/formation")
    public ResponseEntity<Void> setFormations(@AuthenticationPrincipal AppUser principal, @PathVariable Long id, @RequestBody FormationModifyRequest body) {
        AStructure aStructure = structureService.getStructureDBFromId(body.getStructure());
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.WRITE);
        if (allowedSiren.contains(aStructure.getSiren())) {
            boolean success = addPersonneService.modifyFormation(id, body);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.MODIFY_FORMATION)
                    .actor(principal.getUsername())
                    .target(id.toString())
                    .payload(Map.of(
                        "body", body,
                        "success", success)
                    )
                    .build()
            );
            if (!success) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to modify formation for user {} in {}", principal.getUsername(), id, body.getStructure());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
