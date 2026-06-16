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

package fr.recia.manager.db.repositories;

import fr.recia.manager.db.entities.APersonneAStructure;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface APersonneAStructureRepository<T extends APersonneAStructure> extends AbstractRepository<T, Long> {

    @Query("SELECT DISTINCT apas.aPersonneId " +
        "FROM APersonneAStructure apas " +
        "WHERE apas.aStructureId = :id")
    List<Long> findPersonneByStructureId(Long id);

    @Query("SELECT COUNT(*) " +
        "FROM APersonneAStructure apas " +
        "WHERE apas.aPersonneId = :personneId AND apas.aStructureId = :structureId")
    Long isInStructure(Long personneId, Long structureId);

}
