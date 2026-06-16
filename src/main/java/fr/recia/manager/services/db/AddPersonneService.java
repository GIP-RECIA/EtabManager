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
import fr.recia.manager.db.entities.common.CleJointure;
import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.entities.education.MEF;
import fr.recia.manager.db.entities.education.MappingEleveEnseignement;
import fr.recia.manager.db.entities.gestion.AnneeScolaire;
import fr.recia.manager.db.entities.gestion.GenUID;
import fr.recia.manager.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import fr.recia.manager.db.entities.groupe.Classe;
import fr.recia.manager.db.entities.groupe.MappingAGroupeAPersonne;
import fr.recia.manager.db.entities.groupe.MappingAGroupeAPersonneEnseignement;
import fr.recia.manager.db.entities.groupe.MappingAGroupeAPersonneEnseignementId;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.personne.Eleve;
import fr.recia.manager.db.entities.personne.Enseignant;
import fr.recia.manager.db.entities.personne.Login;
import fr.recia.manager.db.entities.personne.NonEnseignantEtablissement;
import fr.recia.manager.db.entities.personne.NonEnseignantServiceAcademique;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Civilite;
import fr.recia.manager.db.enums.Etat;
import fr.recia.manager.db.enums.Sexe;
import fr.recia.manager.db.repositories.education.EnseignementRepository;
import fr.recia.manager.db.repositories.education.MEFRepository;
import fr.recia.manager.db.repositories.gestion.AnneeScolaireRepository;
import fr.recia.manager.db.repositories.gestion.GenUIDRepository;
import fr.recia.manager.db.repositories.groupe.AGroupeOfFoncClasseGroupeRepository;
import fr.recia.manager.db.repositories.groupe.ClasseRepository;
import fr.recia.manager.db.repositories.groupe.MappingAGroupeAPersonneEnseignementRepository;
import fr.recia.manager.db.repositories.groupe.MappingAGroupeAPersonneRepository;
import fr.recia.manager.db.repositories.personne.APersonneRepository;
import fr.recia.manager.db.repositories.personne.LoginRepository;
import fr.recia.manager.db.repositories.structure.AStructureRepository;
import fr.recia.manager.services.cache.CacheInvalidationService;
import fr.recia.manager.services.creation.NameCalculator;
import fr.recia.manager.services.creation.PasswordGenerator;
import fr.recia.manager.services.creation.UidFactory;
import fr.recia.manager.services.exceptions.EmailAlreadyExistsException;
import fr.recia.manager.services.structure.StructureLoader;
import fr.recia.manager.web.dto.function.FonctionAction;
import fr.recia.manager.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class AddPersonneService {

    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private GenUIDRepository<GenUID> genUIDRepository;
    @Autowired
    private AnneeScolaireRepository<AnneeScolaire> anneeScolaireRepository;
    @Autowired
    private LoginRepository<Login> loginRepository;
    @Autowired
    private MEFRepository<MEF> mefRepository;
    @Autowired
    private EnseignementRepository<Enseignement> enseignementRepository;
    @Autowired
    private ClasseRepository<Classe> classeRepository;
    @Autowired
    private MappingAGroupeAPersonneRepository<MappingAGroupeAPersonne> mappingAGroupeAPersonneRepository;
    @Autowired
    private MappingAGroupeAPersonneEnseignementRepository<MappingAGroupeAPersonneEnseignement> mappingAGroupeAPersonneEnseignementRepository;
    @Autowired
    private AGroupeOfFoncClasseGroupeRepository<AGroupeOfFoncClasseGroupe> aGroupeOfFoncClasseGroupeRepository;
    @Autowired
    private UidFactory uidFactory;
    @Autowired
    private NameCalculator nameCalculator;
    @Autowired
    private PasswordGenerator passwordGenerator;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private StructureLoader structureLoader;
    @Autowired
    private CacheInvalidationService cacheInvalidationService;

    /**
     * Création d'une personne dans la base sarapis
     * @param userCreation Le DTO venant du front qui contient les informations nécéssaires à la création
     */
    // TODO : gérer l'ajout de classes locales et groupes locaux, et de responsables d'un élève
    public APersonne addPersonne(UserCreation userCreation, boolean isAdminFonc) {
        log.debug("Trying to create local user {}", userCreation);
        // 1. Récupération de la date
        Instant date = new Date().toInstant();
        // 2. Récupération de l'année scolaire actuelle (on suppose que c'est la dernière)
        AnneeScolaire anneeScolaire = anneeScolaireRepository.findFirstByOrderByDateCreationDesc();
        final String anneeEnCours = anneeScolaire.getAnneeEnCours().toString().split("-")[0];
        final String codeAnnee = uidFactory.codeAnnee(anneeEnCours);
        log.debug("Annee en cours : {}", codeAnnee);
        // 3. Récupération de l'établissement
        AStructure aStructure = aStructureRepository.getReferenceById(userCreation.getStructureRattachement());
        String source = uidFactory.getSource(aStructure.getCleJointure().getSource());
        log.debug("Source : {}", source);
        // 3.1 Vérification de l'email avant création du compte pour certaines populations
        if(userCreation.getCategoriePersonne().equals(CategoriePersonne.Enseignant)
            || userCreation.getCategoriePersonne().equals(CategoriePersonne.Non_enseignant_etablissement)
            || userCreation.getCategoriePersonne().equals(CategoriePersonne.Non_enseignant_service_academique)){
            if(source.equals("SarapisUi_AC-ORLEANS-TOURS") || source.equals("SarapisUi_LA-CENTRE")){
                if(aPersonneRepository.doesEmailExists(userCreation.getCourriel()) > 0){
                    log.error("Email {} already exists ! Can't create local user", userCreation.getCourriel());
                    throw new EmailAlreadyExistsException("Email already exists");
                }
            }
        }
        // 4. Génération de l'UID
        // 4.1. Récupération du genUID correspondant en fonction du domaine de l'établissement
        List<String> domainsOfStructure = structureLoader.getDomainsOfStructure(aStructure.getSiren());
        String codeGenerateur;
        if (domainsOfStructure.size() == 1) {
            codeGenerateur = appProperties.getUidFactory().getDomainToCodeGenerateur().get(domainsOfStructure.get(0));
        } else {
            codeGenerateur = appProperties.getUidFactory().getDefaultCodeGenerateur();
        }
        log.debug("codeGenerateur : {}", codeGenerateur);
        // 4.2. Créer un nouveau genUID si il n'y a pas encore de genUID pour le codeGenerateur cette année
        GenUID genUID = genUIDRepository.findByCAndLAndXx(codeGenerateur, uidFactory.getCodeRegion(), codeAnnee).orElseGet(() -> {
            log.debug("No genUID : creating a new one");
            GenUID newGenUID = new GenUID();
            newGenUID.setDateCreation(Date.from(date));
            newGenUID.setDateModification(Date.from(date));
            newGenUID.setIiii(0);
            newGenUID.setXx(codeAnnee);
            newGenUID.setL(uidFactory.getCodeRegion());
            newGenUID.setC(codeGenerateur);
            return genUIDRepository.saveAndFlush(newGenUID);
        });
        log.debug("genUID : {}", genUID);
        // 4.3. Création de l'uid
        final int increment = genUID.getIiii() + 1;
        log.debug("increment {}", increment);
        String uid = uidFactory.uid(anneeEnCours, increment, codeGenerateur);
        log.debug("uid generated : {}", uid);
        // 4.4. Récupération de la clé de jointure créée
        String cle = uidFactory.clee(uid);
        log.debug("cle : {}", cle);
        // 5. Création de la personne avec les bons attributs
        CategoriePersonne catPer = userCreation.getCategoriePersonne();
        Civilite civilite = userCreation.getCivilite();
        APersonne apersonne = instanciatePersonne(catPer);
        apersonne.setDateCreation(Date.from(date));
        apersonne.setDateModification(Date.from(date));
        apersonne.setAnneeScolaire(anneeScolaire.getAnneeEnCours());
        apersonne.setCategorie(catPer);
        apersonne.setCleJointure(new CleJointure(source, cle));
        apersonne.setDateNaissance(userCreation.getDateNaissance());
        apersonne.setDisplayName(userCreation.getPrenom());
        apersonne.setCivilite(civilite);
        apersonne.setDateFin(userCreation.getDateFin());
        Set<AStructure> listeStructures = new HashSet<>();
        listeStructures.add(aStructure);
        apersonne.setListeStructures(listeStructures);
        if (apersonne.getCivilite() == Civilite.M) {
            apersonne.setSexe(Sexe.M);
        } else {
            apersonne.setSexe(Sexe.F);
        }
        // Le mail des eleves est géré différement
        if (!catPer.equals(CategoriePersonne.Eleve)) {
            apersonne.setEmail(userCreation.getCourriel());
        } else {
            apersonne.setEmailPersonnel(userCreation.getCourriel());
        }
        apersonne.setEtat(Etat.Invalide);
        apersonne.setGivenName(userCreation.getPrenom());
        apersonne.setSn(userCreation.getNom());
        apersonne.setUid(uid);
        apersonne.setStructRattachement(aStructure);
        apersonne.setDoForward(false);
        apersonne.setUuid(UUID.randomUUID().toString());
        // Champs calculés
        apersonne.setPassword(passwordGenerator.genPassword());
        apersonne.setCn(nameCalculator.cn(userCreation.getNom(), userCreation.getPrenom()));
        apersonne.setDisplayName(nameCalculator.display(userCreation.getNom(), userCreation.getPrenom()));
        // 6. Sauvegarder la personne
        aPersonneRepository.saveAndFlush(apersonne);
        log.debug("local personne {} created", uid);
        // Si c'est bon pour la personne alors on met aussi à jour le genuid
        genUID.setIiii(increment);
        genUIDRepository.saveAndFlush(genUID);
        // Gestion du login
        Login login = loginService.updateLogin(nameCalculator.login(userCreation.getNom(), userCreation.getPrenom()), apersonne);
        loginRepository.saveAndFlush(login);
        // Champs spécfiques pour chaque catégorie de personne
        updateSpecificFields(apersonne, userCreation, aStructure, isAdminFonc);
        // Vider les caches concernés
        cacheInvalidationService.evictPersonneAndAssociatedStructures(apersonne.getId(), aStructure.getId());
        return apersonne;
    }

    /**
     * Instancie le bon type d'objet en fonction de la catégorie de la personne
     */
    private APersonne instanciatePersonne(CategoriePersonne catPer) {
        APersonne apersonne;
        if (catPer == CategoriePersonne.Eleve) {
            apersonne = new Eleve();
        } else if (catPer == CategoriePersonne.Non_enseignant_etablissement) {
            apersonne = new NonEnseignantEtablissement();
        } else if (catPer == CategoriePersonne.Non_enseignant_service_academique) {
            apersonne = new NonEnseignantServiceAcademique();
        } else if (catPer == CategoriePersonne.Enseignant) {
            apersonne = new Enseignant();
        }
        // Type invalide
        else {
            throw new RuntimeException("Invalid CategoriePersonne");
        }
        return apersonne;
    }

    /**
     * Met à jour les champs spécifiques en fonction du profil
     */
    private void updateSpecificFields(APersonne apersonne, UserCreation userCreation, AStructure aStructure, boolean isAdminFonc) {
        if (apersonne.getCategorie() == CategoriePersonne.Eleve) {
            updateEleve((Eleve) apersonne, userCreation);
        }
        if (apersonne.getCategorie() == CategoriePersonne.Non_enseignant_etablissement) {
            updateNonEnsEtablissement((NonEnseignantEtablissement) apersonne, aStructure, userCreation, isAdminFonc);
        }
        if (apersonne.getCategorie() == CategoriePersonne.Non_enseignant_service_academique) {
            updateNonEnsServiceAcad((NonEnseignantServiceAcademique) apersonne, aStructure, userCreation, isAdminFonc);
        }
        if (apersonne.getCategorie() == CategoriePersonne.Enseignant) {
            updateEnseignant((Enseignant) apersonne, aStructure, userCreation, isAdminFonc);
        }
    }

    /**
     * Ajout des attributs spécifiques à un nonEnseignantEtablissement
     */
    private void updateNonEnsEtablissement(final NonEnseignantEtablissement nonEnseignantEtablissement, final AStructure aStructure, final UserCreation userCreation, boolean isAdminFonc) {
        log.debug("updating nonEnseignantEtablissement {}", nonEnseignantEtablissement.getUid());
        fonctionService.saveAdditionalFonctions(nonEnseignantEtablissement.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save, isAdminFonc);
    }

    /**
     * Ajout des attributs spécifiques à un nonEnseignantServiceAcademique
     */
    private void updateNonEnsServiceAcad(final NonEnseignantServiceAcademique nonEnseignantServiceAcademique, final AStructure aStructure, final UserCreation userCreation, boolean isAdminFonc) {
        log.debug("updating nonEnseignantServiceAcademique {}", nonEnseignantServiceAcademique.getUid());
        fonctionService.saveAdditionalFonctions(nonEnseignantServiceAcademique.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save, isAdminFonc);
    }

    /**
     * Ajout des attributs spécifiques à un enseignant
     */
    private void updateEnseignant(final Enseignant enseignant, final AStructure aStructure, final UserCreation userCreation, boolean isAdminFonc) {
        log.debug("updating enseignant {}", enseignant.getUid());
        fonctionService.saveAdditionalFonctions(enseignant.getId(), aStructure.getId(), userCreation.getFonctions(), new ArrayList<>(), FonctionAction.save, isAdminFonc);
        List<MappingAGroupeAPersonneEnseignement> personneEnseignements = new ArrayList<>();
        // Ajout de tous les groupes de l'enseignement
        for (Long groupId : userCreation.getGroupesEns()) {
            MappingAGroupeAPersonneEnseignement personneEnseignement = new MappingAGroupeAPersonneEnseignement();
            MappingAGroupeAPersonneEnseignementId pk = new MappingAGroupeAPersonneEnseignementId();
            pk.setEnseignant(enseignant);
            Enseignement enseignement = enseignementRepository.getReferenceById(userCreation.getEnseignementProf());
            pk.setEnseignement(enseignement);
            AGroupeOfFoncClasseGroupe aGroupeOfFoncClasseGroupe = aGroupeOfFoncClasseGroupeRepository.getReferenceById(groupId);
            pk.setGroupe(aGroupeOfFoncClasseGroupe);
            personneEnseignement.setPk(pk);
            personneEnseignement.setSource(enseignant.getCleJointure().getSource());
            personneEnseignements.add(personneEnseignement);
        }
        mappingAGroupeAPersonneEnseignementRepository.saveAllAndFlush(personneEnseignements);
    }

    /**
     * Ajout des attributs spécifiques à un élève
     */
    private void updateEleve(final Eleve eleve, final UserCreation userCreation) {
        log.debug("updating eleve {}", eleve.getUid());
        eleve.setStatut(userCreation.getStatut());
        eleve.setRegime(userCreation.getRegime());
        eleve.setMajeur(userCreation.isMajeur());
        eleve.setMajeurAnticipe(userCreation.isMajeurAnticipe());
        eleve.setTransport(userCreation.isTransportScolaire());
        MEF mef = mefRepository.getReferenceById(userCreation.getMefEleve());
        log.debug("mef for eleve is {}", mef.getId());
        eleve.setMef(mef);
        Set<MappingEleveEnseignement> mappingEleveEnseignements = new HashSet<>();
        for (Long enseignementId : userCreation.getEnseignements()) {
            Enseignement enseignement = enseignementRepository.getReferenceById(enseignementId);
            log.debug("Adding new enseignement {}", enseignement);
            mappingEleveEnseignements.add(new MappingEleveEnseignement(eleve.getCleJointure().getSource(), enseignement));
        }
        log.debug("All enseignements are {}", mappingEleveEnseignements);
        eleve.setEnseignements(mappingEleveEnseignements);
        aPersonneRepository.saveAndFlush(eleve);
        log.debug("Saved enseignements...");
        // On fait par le MappingAGroupeAPersonne car on ne peut pas faire via la classe
        Classe classe = classeRepository.getReferenceById(userCreation.getClasse());
        log.debug("Adding {} to class {}", eleve.getUid(), classe.getId());
        MappingAGroupeAPersonne mappingAGroupeAPersonne = new MappingAGroupeAPersonne(eleve.getCleJointure().getSource(), eleve, classe);
        mappingAGroupeAPersonneRepository.saveAndFlush(mappingAGroupeAPersonne);
        log.debug("Saved classe...");
    }

}
