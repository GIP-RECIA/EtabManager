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

package fr.recia.manager.db.repositories.fonction;

import fr.recia.manager.db.dto.fonction.FonctionDto;
import fr.recia.manager.db.entities.fonction.Fonction;
import fr.recia.manager.db.repositories.AbstractRepository;
import fr.recia.manager.web.dto.function.FonctionPossibleDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonctionRepository<T extends Fonction> extends AbstractRepository<T, Long> {

    @Query("SELECT DISTINCT new fr.recia.manager.db.dto.fonction.FonctionDto(f.filiere.id, f.disciplinePoste.id, f.source, " +
        "f.structure.id, f.dateFin) " +
        "FROM Fonction f " +
        "WHERE f.personne.id = :id ")
    List<FonctionDto> findByPersonne(Long id);

    @Query("SELECT DISTINCT new fr.recia.manager.web.dto.function.FonctionPossibleDto(f.filiere,f.disciplinePoste) " +
        "FROM Fonction f "+
        "LEFT JOIN f.disciplinePoste d " +
        "WHERE f.source = :source ")
    List<FonctionPossibleDto> findPossibleFonctionsBySource(String source);

    @Query("SELECT COUNT(f.id) " +
        "FROM Fonction f " +
        "WHERE f.personne.id = :id " +
        "AND f.structure.id = :structureId " +
        "AND f.disciplinePoste.id = :disciplineId " +
        "AND f.filiere.id = :filiereId")
    Long nbSameFonctionsInStructure(Long id, Long structureId, Long disciplineId, Long filiereId);

    @Query("SELECT f " +
        "FROM Fonction f " +
        "WHERE f.personne.id = :id " +
        "AND f.structure.id = :structureId " +
        "AND f.disciplinePoste.id = :disciplineId " +
        "AND f.filiere.id = :filiereId")
    List<Fonction> findSameFonctionsInStructure(Long id, Long structureId, Long disciplineId, Long filiereId);

    @Query("SELECT DISTINCT new fr.recia.manager.db.dto.fonction.FonctionDto(f.filiere.id, f.disciplinePoste.id, f.source) " +
        "FROM Fonction f " +
        "WHERE f.disciplinePoste IS NOT NULL " +
        "AND f.filiere IS NOT NULL " +
        "AND f.categorie = 'Fonction' " +
        "ORDER BY f.filiere.libelleFiliere")
    List<FonctionDto> findWithoutSource();

    @Query(value = "select distinct apas.APERSONNE_ID " +
        "from apersonnes_astructures apas " +
        "inner join apersonne ap on ap.id = apas.APERSONNE_ID " +
        "where apas.ASTRUCTURE_ID = :structureId " +
        "and (" +
        "apas.APERSONNE_ID not in (" +
        "select distinct af.personne_fk " +
        "from afonction af " +
        "inner join fonction f on f.id = af.id " +
        "inner join apersonne ap on ap.id = af.personne_fk " +
        "where f.astructure_fk = :structureId" +
        ") " +
        "or apas.APERSONNE_ID not in (" +
        "select distinct af.personne_fk " +
        "from afonction af " +
        "inner join fonction f on af.id = f.id " +
        "inner join typefonctionfiliere tff on f.filiere_fk = tff.id " +
        "where f.astructure_fk = :structureId " +
        "group by af.personne_fk " +
        "having count(f.filiere_fk) > 0" +
        ") " +
        ") " +
        "and ap.categorie in ('Enseignant', 'Non_enseignant_collectivite_locale', 'Non_enseignant_etablissement', 'Non_enseignant_service_academique')",
        nativeQuery = true)
    List<Long> findPersonnesWithoutFunctions(Long structureId);

    @Query("SELECT new fr.recia.manager.db.dto.fonction.FonctionDto(f.personne.id, f.filiere.id, f.disciplinePoste.id, " +
        "f.source) " +
        "FROM Fonction f " +
        "WHERE f.structure.id = :structureId " +
        "ORDER BY f.filiere.libelleFiliere")
    List<FonctionDto> findByStructureId(Long structureId);

    @Query("SELECT f.id " +
        "FROM Fonction f " +
        "WHERE f.filiere.id = :filiereId " +
        "AND f.disciplinePoste.id = :disciplineId " +
        "AND f.personne.id = :personneId " +
        "AND f.structure.id = :structureId " +
        "AND f.source = :source")
    Long findId(Long filiereId, Long disciplineId, Long personneId, Long structureId, String source);

}
