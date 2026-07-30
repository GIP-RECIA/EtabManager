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

package fr.recia.manager.services.db;

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.dto.structure.SimpleStructureDto;
import fr.recia.manager.db.dto.structure.StructureDto;
import fr.recia.manager.db.entities.education.Discipline;
import fr.recia.manager.db.entities.fonction.Fonction;
import fr.recia.manager.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.entities.structure.QAStructure;
import fr.recia.manager.db.entities.structure.QEtablissement;
import fr.recia.manager.db.entities.structure.TypeStructure;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.repositories.education.DisciplineRepository;
import fr.recia.manager.db.repositories.fonction.FonctionRepository;
import fr.recia.manager.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.manager.db.repositories.structure.AStructureRepository;
import fr.recia.manager.db.repositories.structure.EtablissementRepository;
import fr.recia.manager.web.dto.function.DisciplinePossibleDto;
import fr.recia.manager.web.dto.function.DisciplinesInFillierePossiblesDto;
import fr.recia.manager.web.dto.function.FiliereDisplayDto;
import fr.recia.manager.web.dto.function.FonctionPossibleDto;
import fr.recia.manager.web.dto.structure.StructureConfigDto;
import fr.recia.manager.web.dto.user.CardPersonneDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StructureService {

    @Autowired
    private AStructureRepository<AStructure> structureRepository;

    @Autowired
    private EtablissementRepository<Etablissement> etablissementRepository;

    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;

    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;

    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;

    @Autowired
    private FonctionService fonctionService;

    @Autowired
    private AppProperties appProperties;

    @Cacheable(value = "stuctureDBById")
    public AStructure getStructureDBFromId(Long id){
        return structureRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "etablissement")
    public StructureDto getStructureDTOFromId(Long id) {
        log.trace("getEtablissement for {}", id);
        AStructure aStructure = structureRepository.findById(id).orElse(null);
        return aStructure != null ? new StructureDto(aStructure) : null;
    }

    public StructureConfigDto getConfig(Long id){
        StructureConfigDto structureConfigDto = new StructureConfigDto();
        AStructure aStructure = structureRepository.findById(id).orElse(null);
        structureConfigDto.setCategoriesPersonne(appProperties.getCreate().getLibelleTypeStructureToCategories().get(aStructure.getType().getLibelle()));
        return structureConfigDto;
    }

    public List<SimpleStructureDto> getEtablissements(Set<String> allowedSiren) {
        return IteratorUtils.toList(etablissementRepository.findAll(QEtablissement.etablissement.siren.isNotNull().and(QEtablissement.etablissement.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    public List<SimpleStructureDto> getStructures(Set<String> allowedSiren) {
        return IteratorUtils.toList(structureRepository.findAll(QAStructure.aStructure.siren.isNotNull().and(QAStructure.aStructure.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    public List<DisciplinesInFillierePossiblesDto> getPossibleFonctions(String source){
        List<FonctionPossibleDto> fonctionPossibleDtos;
        // Cas spécial pour le GIP
        if(source.equals("GIP-RECIA")){
            fonctionPossibleDtos = fonctionRepository.findPossibleFonctionsBySource("SarapisUI_GIP-RECIA");
        } else {
            fonctionPossibleDtos = fonctionRepository.findPossibleFonctionsBySource(source);
        }
        Map<Long, DisciplinesInFillierePossiblesDto> dtoListMap = new HashMap<>();

        // Fonctions déjà existantes sur cette source
        for(FonctionPossibleDto fonctionPossibleDto : fonctionPossibleDtos){
            FiliereDisplayDto filiereDisplayDto = fonctionPossibleDto.getFiliere();
            if(!dtoListMap.containsKey(filiereDisplayDto.getId())){
                dtoListMap.put(filiereDisplayDto.getId(), new DisciplinesInFillierePossiblesDto(filiereDisplayDto.getId(), filiereDisplayDto.getLibelle()));
            }
            // Vérification du null cas spécial pour les CFA
            if(fonctionPossibleDto.getDiscipline() != null){
                dtoListMap.get(filiereDisplayDto.getId()).getDisciplines().add(fonctionPossibleDto.getDiscipline());
            }
        }

        // Fonctions ajoutées via le mapping custom
        CustomConfigProperties.FonctionsProperties fonctionsProperties = appProperties.getCustomConfig().getFonctions().stream()
            .filter(f -> Objects.equals(f.getSource(), source))
            .findAny()
            .orElse(null);

        // TODO : filières sans disciplines, pour ça il faut modifier la structure du DisciplinesInFillierePossiblesDto
        if(fonctionsProperties != null){
            for(CustomConfigProperties.FonctionsProperties.FiliereProperties filiereProperties : fonctionsProperties.getFilieres()){
                TypeFonctionFiliereDto typeFonctionFiliere = typeFonctionFiliereRepository.findByCodeAndSourceSarapis(filiereProperties.getCode(), source);
                if(typeFonctionFiliere != null){
                    for(String disciplineCode : filiereProperties.getDisciplines()){
                        DisciplineDto discipline = disciplineRepository.findByCodeAndSourceSarapis(disciplineCode, source);
                        if(discipline != null){
                            if(!dtoListMap.containsKey(typeFonctionFiliere.getId())){
                                dtoListMap.put(typeFonctionFiliere.getId(), new DisciplinesInFillierePossiblesDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelle()));
                            }
                            dtoListMap.get(typeFonctionFiliere.getId()).getDisciplines().add(new DisciplinePossibleDto(discipline.getId(), discipline.getLibelle()));
                        } else {
                            log.warn("Discipline is null for {}", disciplineCode);
                        }
                    }
                } else {
                    log.warn("Filiere is null for {}", filiereProperties.getCode());
                }
            }
        }

        return dtoListMap.values().stream().sorted(Comparator.comparing(DisciplinesInFillierePossiblesDto::getLibelle)).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<TypeFonctionFiliereDto> getComposition(StructureDto etablissement, List<DatabasePersonneDto> etabPersonnes) {

        // Récupération des filières (fonctions, typesFonctionFiliere et disciplines)
        List<FonctionDto> fonctions = fonctionService.getStructureFonctions(etablissement.getId());

        // On créé des maps pour pouvoir récupérer les objets par leur id en O(1)
        List<TypeFonctionFiliereDto> typesFonctionFiliereList = fonctionService.getTypesFonctionFiliere(etablissement.getSource());
        Map<Long, TypeFonctionFiliereDto> typesFonctionFiliere = typesFonctionFiliereList.stream()
            .collect(Collectors.toMap(
                TypeFonctionFiliereDto::getId,
                Function.identity()
            ));
        List<DisciplineDto> disciplinesList = fonctionService.getDisciplines(etablissement.getSource());
        Map<Long, DisciplineDto> disciplines = disciplinesList.stream()
            .collect(Collectors.toMap(
                DisciplineDto::getId,
                Function.identity()
            ));
        Map<Long, DatabasePersonneDto> personnesMap = etabPersonnes.stream()
            .collect(Collectors.toMap(
                DatabasePersonneDto::getId,
                Function.identity()
            ));

        // Si pas de fonctions dans l'établissement on retourne une map vide
        if (fonctions.isEmpty()) {
            return List.of();
        }

        // Maps indexées qu'on garde à côté pour pouvoir accéder facilement aux objets
        Map<Long, TypeFonctionFiliereDto> filieresMap = new HashMap<>();
        Map<Long, Map<Long, DisciplineDto>> disciplinesMap = new HashMap<>();

        // Liste finale qu'on va retourner au front
        List<TypeFonctionFiliereDto> filieresWithDisciplines = new ArrayList<>();

        for (FonctionDto fonctionDto : fonctions) {
            // Ajout de la filière si elle n'existe pas
            Long filiereId = fonctionDto.getFiliere();
            if(typesFonctionFiliere.containsKey(filiereId)){
                TypeFonctionFiliereDto typeFonctionFiliereDto;
                if (!filieresMap.containsKey(filiereId)) {
                    typeFonctionFiliereDto = new TypeFonctionFiliereDto(typesFonctionFiliere.get(filiereId));
                    filieresMap.put(filiereId, typeFonctionFiliereDto);
                    disciplinesMap.put(filiereId, new HashMap<>());
                    filieresWithDisciplines.add(typeFonctionFiliereDto);
                } else {
                    typeFonctionFiliereDto = filieresMap.get(filiereId);
                }

                // Ajout de la discipline si elle n'existe pas
                Long disciplineId = fonctionDto.getDiscipline();
                // Cas spécial pour les CFA
                if (disciplineId == null) {
                    DatabasePersonneDto databasePersonneDto = personnesMap.get(fonctionDto.getPersonne());
                    typeFonctionFiliereDto.getPersonnesWithoutDiscipline().add(databasePersonneDto);
                } else {
                    if (disciplines.containsKey(disciplineId)) {
                        DisciplineDto disciplineDto;
                        if (!disciplinesMap.get(filiereId).containsKey(disciplineId)) {
                            disciplineDto = new DisciplineDto(disciplines.get(disciplineId));
                            disciplinesMap.get(filiereId).put(disciplineId, disciplineDto);
                            typeFonctionFiliereDto.getDisciplines().add(disciplineDto);
                        } else {
                            disciplineDto = disciplinesMap.get(filiereId).get(disciplineId);
                        }
                        // Ajout de la personne dans la discipline
                        if (personnesMap.containsKey(fonctionDto.getPersonne())) {
                            DatabasePersonneDto databasePersonneDto = personnesMap.get(fonctionDto.getPersonne());
                            disciplineDto.getPersonnes().add(new CardPersonneDto(databasePersonneDto));
                            disciplineDto.getCategories().add(databasePersonneDto.getCategorie());
                        } else {
                            // TODO : que faire dans ce cas si on se retrouve avec une discipline sans personne dedans ?
                            log.warn("person in functions but not in structure for {} : {}", disciplineDto.getId(), fonctionDto.getPersonne());
                        }
                    } else {
                        log.warn("discipline {} is not in known in disciplines for {}", disciplineId, etablissement.getSource());
                    }
                }
            } else {
                log.warn("filiere {} is not in known in filieres for {}", filiereId, etablissement.getSource());
            }
        }

        return filieresWithDisciplines;
    }
}
