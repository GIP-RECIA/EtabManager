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
import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.APersonneAStructure;
import fr.recia.manager.db.entities.common.ExternalId;
import fr.recia.manager.db.entities.education.Discipline;
import fr.recia.manager.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.enums.ExternalIdSource;
import fr.recia.manager.db.enums.ForceEtat;
import fr.recia.manager.db.repositories.APersonneAStructureRepository;
import fr.recia.manager.db.repositories.personne.APersonneRepository;
import fr.recia.manager.ldap.LdapUser;
import fr.recia.manager.ldap.repository.LdapPeopleDao;
import fr.recia.manager.services.cache.CacheInvalidationService;
import fr.recia.manager.services.creation.PasswordGenerator;
import fr.recia.manager.web.dto.function.DisciplineDisplayDto;
import fr.recia.manager.web.dto.function.DisciplinesInFilliereDisplayDto;
import fr.recia.manager.web.dto.user.PersonneDetailDto;
import fr.recia.manager.web.dto.user.StructureForUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PersonneService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private LdapPeopleDao ldapPeopleDao;
    @Autowired
    private CacheInvalidationService cacheInvalidationService;
    @Autowired
    private GroupeService groupeService;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private PasswordGenerator passwordGenerator;


    public List<DatabasePersonneDto> searchPersonne(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdmin(name) : aPersonneRepository.findByNameLike(name);
    }

    public List<DatabasePersonneDto> searchPersonneInStaffCategories(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInStaffCategories(name) : aPersonneRepository.findByNameLikeInStaffCategories(name);
    }

    public List<DatabasePersonneDto> searchPersonneInEtab(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtab(name, structureIds) : aPersonneRepository.findByNameLikeInEtab(name, structureIds);
    }

    public List<DatabasePersonneDto> searchPersonneInEtabInStaffCategories(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabInStaffCategories(name, structureIds) : aPersonneRepository.findByNameLikeInEtabInStaffCategories(name, structureIds);
    }

    public List<DatabasePersonneDto> searchPersonneInEtabBySiren(String name, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabBySiren(name, sirens) : aPersonneRepository.findByNameLikeInEtabBySiren(name, sirens);
    }

    public List<DatabasePersonneDto> searchPersonneInEtabBySirenInStaffCategories(String name, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabBySirenInStaffCategories(name, sirens) : aPersonneRepository.findByNameLikeInEtabBySirenInStaffCategories(name, sirens);
    }

    public List<DatabasePersonneDto> searchPersonneNotInEtab(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtab(name, structureIds) : aPersonneRepository.findByNameLikeNotInEtab(name, structureIds);
    }

    public List<DatabasePersonneDto> searchPersonneNotInEtabInStaffCategories(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabInStaffCategories(name, structureIds) : aPersonneRepository.findByNameLikeNotInEtabInStaffCategories(name, structureIds);
    }

    public List<DatabasePersonneDto> searchPersonneNotInEtabButInSiren(String name, Set<Long> structureIds, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabButInSiren(name, structureIds, sirens) : aPersonneRepository.findByNameLikeNotInEtabButInSiren(name, structureIds, sirens);
    }

    public List<DatabasePersonneDto> searchPersonneNotInEtabButInSirenInStaffCategories(String name, Set<Long> structureIds, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabButInSirenInStaffCategories(name, structureIds, sirens) : aPersonneRepository.findByNameLikeNotInEtabButInSirenInStaffCategories(name, structureIds, sirens);
    }

    @Cacheable(value = "personnesByEtablissement")
    public List<DatabasePersonneDto> getPersonnes(Long structureId, boolean showUid) {
        log.trace("getPersonnes for {}", structureId);
        final List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(structureId);
        if (personnesIds.isEmpty()) return List.of();
        if (showUid) {
            return aPersonneRepository.findByPersonneIdsWithUid(new HashSet<>(personnesIds));
        } else {
            return aPersonneRepository.findByPersonneIdsWithoutUid(new HashSet<>(personnesIds));
        }
    }

    @Cacheable(value = "personne")
    public APersonne getPersonne(Long id) {
        log.trace("getPersonne for {}", id);
        return aPersonneRepository.findById(id).orElse(null);
    }

    /**
     * Réinitialise le mot de passe d'un compte
     */
    public boolean resetPersonne(APersonne aPersonne){
        log.trace("resetPersonne for {}", aPersonne.getId());
        // TODO : de qui on peut réinitialiser les mot de passe ?
        if(aPersonne.getCleJointure().getSource().startsWith("SarapisUi_") && aPersonne.getEtat().equals(Etat.Valide)){
            // Modifications en base
            aPersonne.setPassword(passwordGenerator.genPassword());
            aPersonne.setEtat(Etat.Invalide);
            aPersonneRepository.saveAndFlush(aPersonne);
            // Modifications dans le LDAP
            ldapPeopleDao.resetPersonne(aPersonne.getUid(), appProperties.getCustomConfig().getLdapResetPassword());
            // Invalidation du dache
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        } else {
            log.warn("Try to reset not local or invalid account {}", aPersonne.getId());
        }
        return false;
    }

    /**
     * Bloque une personne : attention il faut bloquer dans la BD et dans le LDAP aussi
     */
    public boolean lockPerson(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Valide)){
            aPersonne.setEtat(Etat.Bloque);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.lockPerson(aPersonne.getUid());
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        } else {
            log.warn("Try to lock person {} that is already locked", aPersonne.getId());
        }
        return false;
    }

    /**
     * Débloque une personne : attention il faut débloquer dans la BD et dans le LDAP aussi
     */
    public Optional<Etat> unlockPerson(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Bloque)){
            final Etat etatToRestore = Etat.Valide;
            aPersonne.setEtat(etatToRestore);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.unlockPerson(aPersonne.getUid());
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return Optional.of(etatToRestore);
        } else {
            log.warn("Try to unlock person {} that is not locked", aPersonne.getId());
        }
        return Optional.empty();
    }

    /**
     * Permet de forcer la suppression d'un compte déjà en suppression
     * Le compte sera supprimé au prochain passage de sarapis
     */
    public boolean forceDelete(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Delete) && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())){
            aPersonne.setForceEtat(ForceEtat.Deleted);
            Date date = new Date();
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        }
        log.warn("Person {} is not in delete state or is already deleted", aPersonne.getId());
        return false;
    }

    /**
     * Permet de passer un compte en suppression
     * Le compte passera en suppression au prochain passage de sarapis
     */
    public boolean putInDeleteState(APersonne aPersonne){
        if(!aPersonne.getEtat().equals(Etat.Delete) && !ForceEtat.Deleted.equals(aPersonne.getForceEtat()) && aPersonne.getCleJointure().getSource().startsWith("SarapisUi_")){
            ldapPeopleDao.putInDeleteState(aPersonne.getUid());
            aPersonne.setEtat(Etat.Delete);
            Date date = new Date();
            aPersonne.setDateAcquittement(date);
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        }
        log.warn("Person {} is already in delete state", aPersonne.getId());
        return false;
    }

    /**
     * Permet de retirer un compte de la suppression (temporairement)
     * Possible pour un compte en suppression (immédiat) ou pour un compte supprimé (à la prochaine synchro)
     */
    public Optional<Etat> undoDelete(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Delete)){
            Etat etatToRestore = Etat.Invalide;
            if(aPersonne.getValidationCharte() != null){
                etatToRestore = Etat.Valide;
            }
            if(aPersonne.getDateAcquittement().equals(aPersonne.getDateModification())){
                ldapPeopleDao.undoDelete(aPersonne.getUid());
            }
            aPersonne.setEtat(etatToRestore);
            // Si on sort de la suppression juste après l'avoir forcée
            aPersonne.setForceEtat(ForceEtat.NONE);
            LocalDate localDate = LocalDate.now().plusDays(10);
            aPersonne.setDateFin(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Date date = new Date();
            if(aPersonne.getDateAcquittement().equals(aPersonne.getDateModification())){
                aPersonne.setDateAcquittement(date);
            }
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return Optional.of(etatToRestore);
        }
        log.warn("Person {} is not in delete state", aPersonne.getId());
        return Optional.empty();
    }

    public PersonneDetailDto getFullPersonne(Long id, APersonne personne, boolean showUid, Set<String> allowedSirens){
        PersonneDetailDto personneDetailDto = new PersonneDetailDto(personne, showUid, appProperties.getCustomConfig().getLoginOffices());
        LdapUser ldapUser = ldapPeopleDao.getLdapUser(personne.getUid(), id);
        // Dans le cas ou la personne n'est pas dans le LDAP
        String sirenCourant = "";
        if(ldapUser != null){
            sirenCourant = ldapUser.getSirenCourant();
        }
        for(AStructure aStructure : personne.getListeStructures()){
            StructureForUserDto structureForUserDto = new StructureForUserDto(aStructure);
            if(allowedSirens.contains(aStructure.getSiren())){
                structureForUserDto.setAuthorizedForPrincipal(true);
            }
            personneDetailDto.getListeStructures().add(structureForUserDto);
            if(personne.getStructRattachement() != null){
                if(aStructure.getId()==personne.getStructRattachement().getId()){
                    structureForUserDto.setStructureRattachement(true);
                }
            } else {
                log.warn("structure de rattachement null pour la personne {} !", personne.getUid());
            }
            if(aStructure.getSiren().equals(sirenCourant)){
                structureForUserDto.setStructureCourante(true);
            }
            structureForUserDto.setClasses(groupeService.getClassesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
            structureForUserDto.setGroupesPedagogiques(groupeService.getGroupesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
        }
        // Affichage de l'id pronote uniquement si la personne est dans un groupe pronote
        if(ldapUser != null){
            List<String> groups = ldapUser.getGroups();
            Pattern patternPronoteGroup = Pattern.compile(appProperties.getCustomConfig().getPronoteGroupRegex());
            if(groups != null){
                for(String group : groups){
                    Matcher matcherPronoteGroup = patternPronoteGroup.matcher(group);
                    if(matcherPronoteGroup.matches()){
                        for(ExternalId externalId : personne.getExternalIds()){
                            if(externalId.getDestinataire().equals(ExternalIdSource.PRONOTE)){
                                personneDetailDto.setIdPronote(externalId.getId());
                            }
                        }
                    }
                }
            } else {
                log.warn("groupes null pour la personne {} !", personne.getUid());
            }
        }
        setAllFonctions(personneDetailDto, fonctionService.getPersonneFonctions(id));
        personneDetailDto.setRelations(relationService.getPersonneRelations(id));
        return personneDetailDto;
    }

    private void setAllFonctions(PersonneDetailDto personneDetailDto, List<FonctionDto> fonctions) {
        if (!fonctions.isEmpty()) {
            for(FonctionDto fonctionDto : fonctions){
                // Ici c'est acceptable de faire une boucle imbriquée car on a peu de structures dans la liste la pluspart du temps
                // Et peu de fonctions par structure aussi
                for(StructureForUserDto structureForUserDto : personneDetailDto.getListeStructures()){
                    if(fonctionDto.getStructure().equals(structureForUserDto.getId())){
                        final TypeFonctionFiliere typeFonctionFiliere = fonctionService.getTypeFonctionFiliere(fonctionDto.getFiliere());
                        // TODO : Dans le cas des CFA il n'y a pas de discipline
                        if(fonctionDto.getDiscipline() != null){
                            final Discipline discipline = fonctionService.getDiscipline(fonctionDto.getDiscipline());
                            if(!fonctionDto.getSource().startsWith(Constants.SARAPISUI_)){
                                boolean found = false;
                                for(DisciplinesInFilliereDisplayDto disciplinesInFilliereDisplayDto : structureForUserDto.getFonctions()){
                                    if(disciplinesInFilliereDisplayDto.getId().equals(typeFonctionFiliere.getId())){
                                        disciplinesInFilliereDisplayDto.getDisciplines().add(new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste(), fonctionDto.getDateDebut(), fonctionDto.getDateFin()));
                                        found = true;
                                    }
                                }
                                if(!found){
                                    DisciplinesInFilliereDisplayDto newDisciplinesInFilliereDisplayDto = new DisciplinesInFilliereDisplayDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelleFiliere());
                                    newDisciplinesInFilliereDisplayDto.getDisciplines().add(new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste(), fonctionDto.getDateDebut(), fonctionDto.getDateFin()));
                                    structureForUserDto.getFonctions().add(newDisciplinesInFilliereDisplayDto);
                                }
                            } else {
                                boolean found = false;
                                for(DisciplinesInFilliereDisplayDto disciplinesInFilliereDisplayDto : structureForUserDto.getAdditionalFonctions()){
                                    if(disciplinesInFilliereDisplayDto.getId().equals(typeFonctionFiliere.getId())){
                                        disciplinesInFilliereDisplayDto.getDisciplines().add(new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste(), fonctionDto.getDateDebut(), fonctionDto.getDateFin()));
                                        found = true;
                                    }
                                }
                                if(!found){
                                    DisciplinesInFilliereDisplayDto newDisciplinesInFilliereDisplayDto = new DisciplinesInFilliereDisplayDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelleFiliere());
                                    newDisciplinesInFilliereDisplayDto.getDisciplines().add(new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste(), fonctionDto.getDateDebut(), fonctionDto.getDateFin()));
                                    structureForUserDto.getAdditionalFonctions().add(newDisciplinesInFilliereDisplayDto);
                                }
                            }
                        } else {
                            if(!fonctionDto.getSource().startsWith(Constants.SARAPISUI_)){
                                structureForUserDto.getFonctions().add(new DisciplinesInFilliereDisplayDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelleFiliere()));
                            } else {
                                structureForUserDto.getAdditionalFonctions().add(new DisciplinesInFilliereDisplayDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelleFiliere()));
                            }
                        }
                    }
                }
            }
        }
    }

}
