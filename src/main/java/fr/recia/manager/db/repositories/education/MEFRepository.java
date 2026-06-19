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

package fr.recia.manager.db.repositories.education;

import fr.recia.manager.db.entities.education.MEF;
import fr.recia.manager.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MEFRepository<T extends MEF> extends AbstractRepository<T, Long> {
    @Query(value = "select distinct m.* from fonctionmef f join fonctions_mefs fm on f.id = fm.FONCTIONMEF_ID "+
        " join mef m on m.id = fm.MEF_ID where f.etablissement_fk = :etablissementId and m.mef_principal_fk = m.id", nativeQuery = true)
    List<MEF> findMefsByEtablissement(Long etablissementId);
}
