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

import fr.recia.manager.db.dto.groupe.EnseignementDto;
import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.repositories.education.EnseignementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EnseignementService {

    @Autowired
    private EnseignementRepository<Enseignement> enseignementRepository;

    @Cacheable(value = "enseignementsByPersonneAndEtab")
    public List<String> getEnseignementsByEtabAndPersonne(Long etabId, Long personneId) {
        return enseignementRepository.findEnseignementsByEtabAndPersonne(etabId, personneId)
            .stream()
            .map(EnseignementDto::getMatiere)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }


}
