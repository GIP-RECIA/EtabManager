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
import fr.recia.manager.configuration.Constants;
import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.APersonneAStructure;
import fr.recia.manager.db.entities.education.Discipline;
import fr.recia.manager.db.entities.fonction.Fonction;
import fr.recia.manager.db.entities.fonction.QFonction;
import fr.recia.manager.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.repositories.education.DisciplineRepository;
import fr.recia.manager.db.repositories.fonction.FonctionRepository;
import fr.recia.manager.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.manager.db.repositories.personne.APersonneAStructureRepository;
import fr.recia.manager.db.repositories.personne.APersonneRepository;
import fr.recia.manager.db.repositories.personne.impl.APersonneAStructureRepositoryCustom;
import fr.recia.manager.db.repositories.personne.impl.APersonneCategoryRepository;
import fr.recia.manager.db.repositories.structure.AStructureRepository;
import fr.recia.manager.services.cache.CacheInvalidationService;
import fr.recia.manager.utils.SourceUtils;
import fr.recia.manager.web.dto.function.FonctionAction;
import fr.recia.manager.web.dto.function.FonctionToModify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FonctionService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneAStructureRepositoryCustom aPersonneAStructureRepositoryCustom;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;
    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;
    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
    @Autowired
    private APersonneCategoryRepository aPersonneCategoryRepository;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private CacheInvalidationService cacheInvalidationService;

    @Cacheable(value = "typeFonctionFiliere")
    public TypeFonctionFiliere getTypeFonctionFiliere(Long id){
        return typeFonctionFiliereRepository.getReferenceById(id);
    }

    @Cacheable(value = "discipline")
    public Discipline getDiscipline(Long id){
        return disciplineRepository.getReferenceById(id);
    }

    @Cacheable(value = "personneFonctions")
    public List<FonctionDto> getPersonneFonctions(Long personneId) {
        log.trace("getPersonneFonctions for {}", personneId);
        return fonctionRepository.findByPersonne(personneId);
    }

    @Cacheable(value = "structureFonctions")
    public List<FonctionDto> getStructureFonctions(Long structureId) {
        log.trace("getStructureFonctions for {}", structureId);
        return fonctionRepository.findByStructureId(structureId);
    }

    @Cacheable(value = "typesFonctionFiliere")
    public List<TypeFonctionFiliereDto> getTypesFonctionFiliere(String source) {
        log.trace("getTypesFonctionFiliere for {}", source);
        return typeFonctionFiliereRepository.findByAnySource();
    }

    @Cacheable(value = "typeFonctionFiliereByCode")
    public TypeFonctionFiliereDto getTypeFonctionFiliereByCode(String code, String source) {
        log.trace("getTypeFonctionFiliereByCode for {}", source);
        return typeFonctionFiliereRepository.findByCodeAndSourceSarapis(code, source);
    }

    @Cacheable(value = "disciplineByCode")
    public DisciplineDto getDisciplineByCode(String code, String source) {
        log.trace("getDisciplineByCode for {}", source);
        return disciplineRepository.findByCodeAndSourceSarapis(code, source);
    }

    @Cacheable(value = "disciplines")
    public List<DisciplineDto> getDisciplines(String source) {
        log.trace("getDisciplines for {}", source);
        return disciplineRepository.findByAnySource();
    }

    @Cacheable(value = "personnesWithoutFunctions")
    public List<DatabasePersonneDto> getPersonnesWithoutFunctions(Long structureId, boolean showUid, Set<String> sources) {
        log.trace("getPersonnesWithoutFunctions for {}", structureId);
        final List<Long> personnesIds = fonctionRepository.findPersonnesWithoutFunctions(structureId);
        if (showUid) {
            return aPersonneRepository.findByPersonneIdsWithUid(new HashSet<>(personnesIds), sources);
        } else {
            return aPersonneRepository.findByPersonneIdsWithoutUid(new HashSet<>(personnesIds), sources);
        }
    }

    /**
     * Modifie les fonctions d'une personne sur une stucture
     */
    public boolean saveAdditionalFonctions(Long personneId, Long structureId, List<FonctionToModify> toAddFunctions, List<FonctionToModify> toDeleteFunctions, FonctionAction requiredAction, boolean isAdmin) {
        // Permet de savoir si tous les ajouts ont échoué ou si il y a au moins 1 ajout qui a réussi
        boolean ok = false;
        APersonne aPersonne = aPersonneRepository.findById(personneId).orElse(null);
        final AStructure aStructure = aStructureRepository.findById(structureId).orElse(null);
        if (aPersonne == null || aStructure == null || !List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(aPersonne.getEtat())
            || List.of(CategoriePersonne.Eleve, CategoriePersonne.Personne_relation_eleve, CategoriePersonne.Tuteur_stage, CategoriePersonne.Responsable_Entreprise).contains(aPersonne.getCategorie())) {
            log.warn("Etat or Categorie is invalid for personne {} !", personneId);
            return false;
        }

        boolean isInStructure = aPersonneAStructureRepository.isInStructure(personneId, structureId) > 0;

        final String sourceOrig = SourceUtils.getOfficialSource(aStructure.getCleJointure().getSource());
        final String source = Constants.SARAPISUI_ + sourceOrig;

        List<FonctionDto> toAddAdditional = new ArrayList<>();
        for (FonctionToModify fonctionToAdd : toAddFunctions) {
            // Si c'est un ajout, on vérifie que la personne est bien rattachée à l'établissemt si on ne demande pas le rattachement
            if(!isInStructure && !requiredAction.equals(FonctionAction.attach)){
                log.warn("Try to add function in an etab {} the user {} does not belong !", structureId, personneId);
            } else {
                // On récupère le code à partir de l'id pour pouvoir comparer à ce qu'on a dans la config pour les fonctions limitées aux admins
                TypeFonctionFiliere typeFonctionFiliere = this.getTypeFonctionFiliere(fonctionToAdd.getFiliere());
                final Set<String> adminFilieres  = appProperties.getCustomConfig().getAdminFonctionsBySource().get(sourceOrig);
                if(adminFilieres != null && adminFilieres.contains(typeFonctionFiliere.getCodeFiliere())){
                    log.debug("Admin fonction add : check user rights");
                    if(isAdmin){
                        toAddAdditional.add(new FonctionDto(personneId, fonctionToAdd.getFiliere(), fonctionToAdd.getDiscipline(), source, structureId, fonctionToAdd.getDateFin(), fonctionToAdd.getDateDebut()));
                    } else {
                        log.warn("Can't add filiere {} because user is not authorized", typeFonctionFiliere.getLibelleFiliere());
                    }
                } else {
                    toAddAdditional.add(new FonctionDto(personneId, fonctionToAdd.getFiliere(), fonctionToAdd.getDiscipline(), source, structureId, fonctionToAdd.getDateFin(), fonctionToAdd.getDateDebut()));
                }
            }
        }

        List<FonctionDto> toDeleteAdditional = new ArrayList<>();
        for (FonctionToModify fonctionToDelete : toDeleteFunctions) {
            // On récupère le code à partir de l'id pour pouvoir comparer à ce qu'on a dans la config pour les fonctions limitées aux admins
            TypeFonctionFiliere typeFonctionFiliere = this.getTypeFonctionFiliere(fonctionToDelete.getFiliere());
            final Set<String> adminFilieres  = appProperties.getCustomConfig().getAdminFonctionsBySource().get(sourceOrig);
            if(adminFilieres != null && adminFilieres.contains(typeFonctionFiliere.getCodeFiliere())){
                log.debug("Admin fonction delete : check user rights");
                if(isAdmin){
                    toDeleteAdditional.add(new FonctionDto(personneId, fonctionToDelete.getFiliere(), fonctionToDelete.getDiscipline(), source, structureId));
                } else {
                    log.warn("Can't remove filiere {} because user is not authorized", typeFonctionFiliere.getLibelleFiliere());
                }
            } else {
                toDeleteAdditional.add(new FonctionDto(personneId, fonctionToDelete.getFiliere(), fonctionToDelete.getDiscipline(), source, structureId));
            }
        }

        if (!toAddAdditional.isEmpty()) {
            final Map<String, Set<String>> profilsByCategory = appProperties.getCustomConfig().getProfilsByCategoryMap();
            final Map<String, String> profilByFonction = appProperties.getCustomConfig().getProfilByFonctionMap();
            final Map<String, List<String>> categoryByProfil = appProperties.getCustomConfig().getCategoryByProfilMap();
            List<Fonction> fonctions = new ArrayList<>();
            for(FonctionDto fonctionDto : toAddAdditional){
                // Si on a déjà la fonction qu'on veut ajouter alors il ne faut pas la remettre ça va poser des problèmes de contraintes
                Long nbExistingFonctions = fonctionRepository.nbSameFonctionsInStructure(fonctionDto.getPersonne(), fonctionDto.getStructure(), fonctionDto.getDiscipline(), fonctionDto.getFiliere());
                // Si on a pas du tout la fonction, on l'ajoute
                if(nbExistingFonctions == 0){
                    TypeFonctionFiliere filiere = typeFonctionFiliereRepository.findById(fonctionDto.getFiliere()).orElse(null);
                    Discipline discipline = disciplineRepository.findById(fonctionDto.getDiscipline()).orElse(null);
                    // Avant de l'ajouter, on vérifie qu'il ne faut pas changer la catégorie de la personne en fonction des profils associés au codeFillière de la fonction
                    final String codeFiliere = filiere.getCodeFiliere();
                    final String categoriePersonne = aPersonne.getCategorie().toString();
                    final Set<String> profilsAutorisesPersonne = profilsByCategory.get(categoriePersonne);
                    final String profilFonction = profilByFonction.get(codeFiliere);
                    final List<String> categoryNeeded = categoryByProfil.get(profilFonction);
                    boolean validCategory = true;
                    if(!profilsAutorisesPersonne.contains(profilFonction)){
                        log.info("Profil {} is needed for filiere {} but not authorized for category {}. List of profiles authorized for personne is : {}", profilFonction, codeFiliere, categoriePersonne, profilsAutorisesPersonne);
                        // On vérifie qu'on peut donner une nouvelle catégorie qui correspond à la fonction : si on ne sait pas choisir, il faudra résoudre le problème à la main
                        if(categoryNeeded.size() == 1){
                            final CategoriePersonne newCategory = CategoriePersonne.valueOf(categoryNeeded.getFirst());
                            log.info("Changing personne category from {} to {}", aPersonne.getCategorie(), newCategory);
                            try {
                                aPersonne = aPersonneCategoryRepository.changeCategory(aPersonne, newCategory);
                            } catch (Exception e) {
                                log.warn("Can't change category of personne {} to {}", aPersonne.getUid(), newCategory, e);
                                validCategory = false;
                            }
                        } else {
                            log.warn("Can't add fonction {} to user {} because of authorized profils. Resolution need to be done by hand.", fonctionDto, aPersonne.getUid());
                            validCategory = false;
                        }
                    }
                    // On ajoute la nouvelle fonction
                    if(validCategory){
                        fonctions.add(new Fonction(discipline, filiere, aStructure, aPersonne, source, fonctionDto.getDateFin(), fonctionDto.getDateDebut()));
                        ok = true;
                    }
                } else if (nbExistingFonctions == 1){
                    // Sinon si on a déjà la fonction, si on a une date de début/fin il faut la mettre à jour
                    Fonction fonction = fonctionRepository.findSameFonctionsInStructure(fonctionDto.getPersonne(), fonctionDto.getStructure(), fonctionDto.getDiscipline(), fonctionDto.getFiliere()).getFirst();
                    if((fonctionDto.getDateFin() != null && !fonctionDto.getDateFin().equals(fonction.getDateFin())) || (fonctionDto.getDateDebut() != null && !fonctionDto.getDateDebut().equals(fonction.getDateDebut()))){
                        log.debug("Function {}-{} already added for user {} in etab {}, updating dates...", fonctionDto.getFiliere(), fonctionDto.getDiscipline(), fonctionDto.getPersonne(), fonctionDto.getStructure());
                        fonction.setDateFin(fonctionDto.getDateFin());
                        fonction.setDateDebut(fonctionDto.getDateDebut());
                        fonctions.add(fonction);
                        ok = true;
                    } else {
                        log.warn("Try to add function {}-{} but already added for user {} in etab {}", fonctionDto.getFiliere(), fonctionDto.getDiscipline(), fonctionDto.getPersonne(), fonctionDto.getStructure());
                    }
                } else {
                    log.warn("Try to add function {}-{} but there are already more than one function for user {} in etab {}", fonctionDto.getFiliere(), fonctionDto.getDiscipline(), fonctionDto.getPersonne(), fonctionDto.getStructure());
                }
            }
            fonctionRepository.saveAll(fonctions);
        }

        if (!toDeleteAdditional.isEmpty()) {
            fonctionRepository.deleteAllById(toDeleteAdditional.stream()
                .map(fonction -> fonctionRepository.findId(fonction.getFiliere(), fonction.getDiscipline(), personneId, fonction.getStructure(), fonction.getSource()))
                .collect(Collectors.toList()));
            ok = true;
        }

        if (!toAddAdditional.isEmpty() || !toDeleteAdditional.isEmpty()) {
            fonctionRepository.flush();

            int officialFonctionsInStructure = (int) fonctionRepository.findByPersonne(personneId).stream()
                .filter(fonction -> !SourceUtils.isSourceOfficial(fonction.getSource()) && Objects.equals(fonction.getStructure(), structureId))
                .count();

            switch (requiredAction) {
                case attach:
                    if (isInStructure || toAddAdditional.isEmpty()) {
                        log.error("Unable to attach user {} to structure {}", aPersonne.getId(), aStructure.getId());
                        break;
                    }
                    aPersonneAStructureRepositoryCustom.insertInStructure(personneId, structureId);
                    break;
                case detach:
                    if (!isInStructure || toDeleteAdditional.isEmpty() || officialFonctionsInStructure > 0 || aPersonne.getListeStructures().size()==1) {
                        log.error("Unable to detach user {} of structure {}", aPersonne.getId(), aStructure.getId());
                        break;
                    }
                    aPersonneAStructureRepositoryCustom.deleteFromStructure(personneId, structureId);
                    break;
                default:
                    break;
            }
            aPersonne.setDateModification(LocalDateTime.now());
            aPersonneRepository.saveAndFlush(aPersonne);
        }

        // Vider les caches
        cacheInvalidationService.evictPersonneAndAssociatedStructures(personneId, structureId);

        return ok;
    }

    public long nbDiscipline(Long structureId, String filiereCode, String disciplineCode) {
        return fonctionRepository.count(QFonction.fonction.structure.id.eq(structureId)
            .and(QFonction.fonction.filiere.codeFiliere.eq(filiereCode))
            .and(QFonction.fonction.disciplinePoste.code.eq(disciplineCode)));
    }

}
