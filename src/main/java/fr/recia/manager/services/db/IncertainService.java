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

import fr.recia.manager.db.dto.gestion.DatabaseIncertainDto;
import fr.recia.manager.db.dto.gestion.IncertainDto;
import fr.recia.manager.db.dto.gestion.IncertainPersonneDto;
import fr.recia.manager.db.entities.gestion.Incertain;
import fr.recia.manager.db.repositories.gestion.IncertainRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IncertainService {

    @Autowired
    private IncertainRepository<Incertain> incertainRepository;

    @Cacheable(value = "incertains")
    public Collection<IncertainPersonneDto> getIncertains(Long etabId) {
        Map<Long, IncertainPersonneDto> incertainsByPersonne = new HashMap<>();
        List<DatabaseIncertainDto> incertains = incertainRepository.findDistinctByIncertainPersStructRattachementId(etabId);
        for (DatabaseIncertainDto databaseIncertainDto : incertains) {
            if(!incertainsByPersonne.containsKey(databaseIncertainDto.getId())){
                incertainsByPersonne.put(databaseIncertainDto.getId(), new IncertainPersonneDto(databaseIncertainDto));
            }
            incertainsByPersonne.get(databaseIncertainDto.getId()).getIncertains().add(new IncertainDto(databaseIncertainDto));
        }
        return incertainsByPersonne.values();
    }
}
