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

import fr.recia.manager.db.dto.groupe.EnseignementEtabDto;
import fr.recia.manager.db.entities.education.Enseignement;
import fr.recia.manager.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnseignementRepository<T extends Enseignement> extends AbstractRepository<T, Long> {
    @Query(value = "select distinct e.* from enseignement e " +
        "join apersonnes_agroupes_enseignements aae on e.id = aae.ENSEIGNEMENT_ID " +
        "join agroupeoffoncclassegroupe a on aae.AGROUPEOFFONCCLASSEGROUPE_ID = a.id " +
        "join classe c on a.id = c.id " +
        "where c.id = :classeId",
        nativeQuery = true)
    List<Enseignement> findEnseignementsByClasse(Long classeId);

    @Query(value = "select distinct e.* from enseignement e " +
        "join apersonnes_agroupes_enseignements aae on e.id = aae.ENSEIGNEMENT_ID " +
        "join agroupeoffoncclassegroupe a on aae.AGROUPEOFFONCCLASSEGROUPE_ID = a.id " +
        "join groupe g on a.id = g.id " +
        "join groupes_classes gc on g.id = gc.GROUPE_ID " +
        "where gc.CLASSE_ID = :classeId",
        nativeQuery = true)
    List<Enseignement> findEnseignementsGroupesByClasse(Long classeId);

    @Query(value = "select matiere,categorie,cn,a.id as idGr,e.id as idEns from agroupeoffoncclassegroupe agc join agroupeofapersonne aa on agc.id=aa.id "+
        "join agroupe a on a.id=aa.id join apersonnes_agroupes_enseignements aae on aae.AGROUPEOFFONCCLASSEGROUPE_ID=agc.id "+
        "join enseignement e on e.id=aae.ENSEIGNEMENT_ID where agc.etablissement_fk=:etabId",
        nativeQuery = true)
    List<EnseignementEtabDto> findEnseignementsByEtab(Long etabId);
}
