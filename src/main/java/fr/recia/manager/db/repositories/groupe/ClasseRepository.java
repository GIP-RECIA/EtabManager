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

package fr.recia.manager.db.repositories.groupe;

import fr.recia.manager.db.entities.groupe.Classe;
import fr.recia.manager.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasseRepository<T extends Classe> extends AbstractRepository<T, Long> {

    @Query("SELECT c.cn as cn " +
        "from Classe c " +
        "join c.membres m " +
        "where m.pk.personne.id = :personneId " +
        "and c.proprietaire.id = :etablissementId"
    )
    List<String> findByPersonneId(Long personneId, Long etablissementId);
}
