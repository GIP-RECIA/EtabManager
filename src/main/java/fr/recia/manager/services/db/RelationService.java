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

import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.relation.AMappingRelation;
import fr.recia.manager.db.repositories.relation.AMappingRelationRepository;
import fr.recia.manager.web.dto.relation.RelationDto;
import fr.recia.manager.web.dto.user.CardPersonneDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RelationService {

    @Autowired
    private AMappingRelationRepository<AMappingRelation> amappingRelationRepository;

    @Cacheable(value = "relations")
    public List<RelationDto> getPersonneRelations(Long personneId){
        List<RelationDto> relationDtos = new ArrayList<>();
        List<AMappingRelation> relationsDst = amappingRelationRepository.findByPkPersonne1Id(personneId);
        for(AMappingRelation relation : relationsDst){
            relationDtos.add(new RelationDto(relation.getPk().getCategorie(), new CardPersonneDto(new DatabasePersonneDto(relation.getPk().getPersonne2())), false));
        }
        List<AMappingRelation> relationsSrc = amappingRelationRepository.findByPkPersonne2Id(personneId);
        for(AMappingRelation relation : relationsSrc){
            relationDtos.add(new RelationDto(relation.getPk().getCategorie(), new CardPersonneDto(new DatabasePersonneDto(relation.getPk().getPersonne1())), true));
        }
        return relationDtos;
    }


}
