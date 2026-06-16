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
import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.dto.structure.SimpleStructureDto;
import fr.recia.manager.db.dto.structure.StructureDto;
import fr.recia.manager.db.entities.gestion.AnneeScolaire;
import fr.recia.manager.db.repositories.gestion.AnneeScolaireRepository;
import fr.recia.manager.security.AppUser;
import fr.recia.manager.security.AppRole;
import fr.recia.manager.services.alert.AlertService;
import fr.recia.manager.services.db.FonctionService;
import fr.recia.manager.services.db.IncertainService;
import fr.recia.manager.services.db.PersonneService;
import fr.recia.manager.services.db.StructureService;
import fr.recia.manager.web.dto.function.DisciplinesInFillierePossiblesDto;
import fr.recia.manager.web.dto.function.ListFonctionPossibleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/structure")
public class StructureController {

    @Autowired
    private StructureService structureService;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private PersonneService personneService;
    @Autowired
    private AlertService alertService;
    @Autowired
    private IncertainService incertainService;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private AnneeScolaireRepository<AnneeScolaire> anneeScolaireRepository;

    /**
     * Récupère la liste de toutes les structures que l'utilisateur à le droit d'administrer
     */
    @GetMapping()
    public ResponseEntity<List<SimpleStructureDto>> getStructures(@AuthenticationPrincipal AppUser principal) {
        // Ne retourner que les établissements que la personne a le droit de lire
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        List<SimpleStructureDto> etablissements = structureService.getStructures(allowedSiren);
        if (etablissements.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(etablissements, HttpStatus.OK);
    }

    /**
     * Récupère la liste de toutes les structures que l'utilisateur à le droit d'administrer (sauf les collectivités)
     */
    @GetMapping("/etablissement")
    public ResponseEntity<List<SimpleStructureDto>> getEtablissements(@AuthenticationPrincipal AppUser principal) {
        // Ne retourner que les établissements que la personne a le droit de lire
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        List<SimpleStructureDto> etablissements = structureService.getEtablissements(allowedSiren);
        if (etablissements.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(etablissements, HttpStatus.OK);
    }

    /**
     * Récupère la liste de toutes les fonctions qu'on peut ajouter à des personnes dans l'établissement
     */
    @GetMapping("/{id}/fonctions")
    public ResponseEntity<ListFonctionPossibleDto> getPossibleFonctions(@PathVariable Long id){
        String source = structureService.getStructureDBFromId(id).getCleJointure().getSource();
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findFirstByOrderByDateCreationDesc();
        List<DisciplinesInFillierePossiblesDto> fonctionPossibles = structureService.getPossibleFonctions(source);
        return new ResponseEntity<>(new ListFonctionPossibleDto(anneeScolaire.getPassageAnneeSuivante(), fonctionPossibles), HttpStatus.OK);
    }

    /**
     * Récupère les informations sur une structure ainsi que toutes les personnes dedans
     */
    @GetMapping(value = "/etablissement/{id}")
    public ResponseEntity<StructureDto> getEtablissement(@AuthenticationPrincipal AppUser principal, @PathVariable Long id) {
        StructureDto etablissement = structureService.getStructureDTOFromId(id);
        if (etablissement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Check si la personne à le droit de lire sur l'établissement
        Set<String> allowedSiren = principal.getRightsForEtabs().get(AppRole.READ);
        if (!allowedSiren.contains(etablissement.getSiren())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Donner les rôles au front pour affichage correct des actions faisables par l'utilisateur
        if(etablissement.getPermissions().isEmpty()){
            for(AppRole appRole : principal.getRightsForEtabs().keySet()){
                if(principal.getRightsForEtabs().get(appRole).contains(etablissement.getSiren())){
                    etablissement.addPermission(appRole);
                }
            }
            for(AppRole appRole : principal.getGlobalRights()){
                etablissement.addPermission(appRole);
            }
        }

        // Booléen qui indique si on affiche l'uid ou non
        boolean showUid = principal.getRightsForEtabs().get(AppRole.VIEW_UID).contains(etablissement.getSiren());

        // Récupération de la liste des personnes depuis la database
        List<DatabasePersonneDto> etabPersonnes = personneService.getPersonnes(id, showUid);
        List<DatabasePersonneDto> withoutFunction = fonctionService.getPersonnesWithoutFunctions(id, showUid);

        // Récupération des filières (fonctions, typesFonctionFiliere et disciplines)
        List<FonctionDto> fonctions = fonctionService.getStructureFonctions(id);

        // Liste des personnes de l'établissement
        // TODO : externaliser la logique dans un service
        etablissement.setListePersonnes(etabPersonnes, appProperties.getCustomConfig().getLoginOffices());

        // Liste des personnes sans fonction
        etablissement.setPersonnesWithoutFonctions(withoutFunction);

        // Récupération des alertes
        etablissement.setAlerts(alertService.getFonctionAlerts(etablissement.getId(), etablissement.getSource()));

        // Récupération des incertains
        etablissement.setIncertains(incertainService.getIncertains(etablissement.getId()));

        // On créé des maps pour pouvoir récupérer les objets par leur id en O(1)
        List<TypeFonctionFiliereDto> typesFonctionFiliereList = fonctionService.getTypesFonctionFiliere(etablissement.getSource());
        Map<Long, TypeFonctionFiliereDto> typesFonctionFiliereMap = typesFonctionFiliereList.stream()
            .collect(Collectors.toMap(
                TypeFonctionFiliereDto::getId,
                Function.identity()
            ));
        List<DisciplineDto> disciplinesList = fonctionService.getDisciplines(etablissement.getSource());
        Map<Long, DisciplineDto> disciplinesMap = disciplinesList.stream()
            .collect(Collectors.toMap(
                DisciplineDto::getId,
                Function.identity()
            ));
        Map<Long, DatabasePersonneDto> personnesMap = etabPersonnes.stream()
            .collect(Collectors.toMap(
                DatabasePersonneDto::getId,
                Function.identity()
            ));

        // Logique d'assemblage de l'objet à envoyer au font pour les fillières
        etablissement.setComposition(fonctions, typesFonctionFiliereMap, disciplinesMap, personnesMap);

        return new ResponseEntity<>(etablissement, HttpStatus.OK);
    }

}
