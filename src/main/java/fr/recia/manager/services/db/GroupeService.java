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

import fr.recia.manager.db.entities.groupe.Classe;
import fr.recia.manager.db.entities.groupe.Groupe;
import fr.recia.manager.db.entities.groupe.MappingAGroupeAPersonneEnseignement;
import fr.recia.manager.db.enums.CategorieGroupe;
import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.repositories.groupe.ClasseRepository;
import fr.recia.manager.db.repositories.groupe.GroupeRepository;
import fr.recia.manager.db.repositories.groupe.MappingAGroupeAPersonneEnseignementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GroupeService {

    @Autowired
    private ClasseRepository<Classe> classeRepository;
    @Autowired
    private GroupeRepository<Groupe> groupeRepository;
    @Autowired
    private MappingAGroupeAPersonneEnseignementRepository<MappingAGroupeAPersonneEnseignement> mappingAGroupeAPersonneEnseignementRepository;

    public List<String> getClassesOfPersonne(Long personneId, CategoriePersonne categoriePersonne, Long structureId){
        if(categoriePersonne.equals(CategoriePersonne.Eleve)){
            return classeRepository.findByPersonneId(personneId, structureId);
        }
        if(categoriePersonne.equals(CategoriePersonne.Enseignant)){
            return mappingAGroupeAPersonneEnseignementRepository.findByEnseignantIdAndCategorie(personneId, CategorieGroupe.Classe, structureId);
        }
        return new ArrayList<>();
    }

    public List<String> getGroupesOfPersonne(Long personneId, CategoriePersonne categoriePersonne, Long structureId){
        if(categoriePersonne.equals(CategoriePersonne.Eleve)){
            return groupeRepository.findByPersonneId(personneId, structureId);
        }
        if(categoriePersonne.equals(CategoriePersonne.Enseignant)){
            return mappingAGroupeAPersonneEnseignementRepository.findByEnseignantIdAndCategorie(personneId, CategorieGroupe.Groupe, structureId);
        }
        return new ArrayList<>();
    }

}
