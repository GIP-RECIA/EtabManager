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

package fr.recia.manager.db.repositories.gestion;

import fr.recia.manager.db.dto.gestion.DatabaseIncertainDto;
import fr.recia.manager.db.entities.gestion.Incertain;
import fr.recia.manager.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncertainRepository<T extends Incertain> extends AbstractRepository<T, Long> {
    // Query JPQL native pour réduire le temps d'éxécution par un facteur 1000
    @Query("select new fr.recia.manager.db.dto.gestion.DatabaseIncertainDto(i.attribut, i.value, i.texte, i.obligatoire, "+
        "p.id, p.etat, p.cleJointure.source, p.cn, p.dateModification, p.dateAcquittement) "+
        "from Incertain i "+
        "join i.incertainPers p "+
        "where p.structRattachement.id = :structureId")
    List<DatabaseIncertainDto> findDistinctByIncertainPersStructRattachementId(Long structureId);
}
