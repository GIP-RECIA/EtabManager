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

package fr.recia.manager.db.repositories.personne;

import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface APersonneRepository<T extends APersonne> extends AbstractRepository<T, Long> {

    @Query("SELECT count(*) " +
        "FROM APersonne ap " +
        "WHERE ap.email = :email")
    Long doesEmailExists(String email);

    // TODO : fix la recherche pour tous les autres types de recherche (doublons)

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.givenName, ap.sn, ap.login.nom, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE ap.id IN :ids " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByPersonneIdsWithUid(Set<Long> ids);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.givenName, ap.sn, ap.login.nom, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE ap.id IN :ids " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByPersonneIdsWithoutUid(Set<Long> ids);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie," +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE ap.id = :id")
    DatabasePersonneDto findByPersonneIdSimple(Long id);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLike(String name);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdmin(String name);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeNotInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeInEtabBySiren(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeNotInEtabButInSiren(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeInStaffCategories(String name);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND NOT EXISTS ( SELECT 1 FROM ap.listeStructures s WHERE s.id IN :etabId) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeNotInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeInEtabBySirenInStaffCategories(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat('%', :name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeNotInEtabButInSirenInStaffCategories(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminNotInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminInEtabBySiren(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminNotInEtabButInSiren(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminInStaffCategories(String name);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND NOT EXISTS (SELECT 1 FROM ap.listeStructures s WHERE s.id IN :etabId) " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminNotInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminInEtabBySirenInStaffCategories(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.manager.db.dto.personne.DatabasePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement, ap.dateSourceModification) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat('%', :name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.manager.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.manager.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<DatabasePersonneDto> findByNameLikeAdminNotInEtabButInSirenInStaffCategories(String name, Set<Long> etabId, Set<String> sirens);

}
