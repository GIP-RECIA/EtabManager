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

package fr.recia.manager.db.repositories.personne.impl;


import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategoriePersonne;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
// Implémentation spécifique pour gérer correctement le changement de catégorie d'une personne, car hibernate ne le gère pas nativement
public class APersonneCategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Change la catégorie de la personne d'une catégorie à une autre en mettant à jour les tables d'héritage
     * @param aPersonne La personne dont on veut changer la catégorie
     * @param newCategory La nouvelle catégorie à appliquer
     * @return La personne mise à jour pour continuer à s'en servir plus tard
     */
    @Transactional
    public APersonne changeCategory(APersonne aPersonne, CategoriePersonne newCategory){
        final long id = aPersonne.getId();
        final CategoriePersonne oldCategory = aPersonne.getCategorie();
        final String oldTable = getTableForCategory(oldCategory);
        final String newTable = getTableForCategory(newCategory);

        // Comme on va devoir décharger et recharger la personne, d'abord on s'assure qu'on est à jour
        entityManager.flush();

        // Mettre à jour les 3 tables
        entityManager.createNativeQuery("DELETE FROM " + oldTable + " WHERE id = :id").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("INSERT INTO " + newTable + " (id) VALUES (:id)").setParameter("id", id).executeUpdate();
        entityManager.createNativeQuery("UPDATE apersonne SET categorie = :categorie WHERE id = :id").setParameter("categorie", newCategory.toString()).setParameter("id", id).executeUpdate();

        // Détacher l'ancienne instance puis la recharger
        entityManager.detach(aPersonne);
        return entityManager.find(APersonne.class, id);
    }

    /**
     * Donne le nom d'une table à partir d'une catégorie
     */
    private String getTableForCategory(CategoriePersonne categorie) {
        // TODO : possible de faire autrement qu'avec un switch case - comment récupérer le nom des tables ?
        switch (categorie) {
            case Eleve:
                return "eleve";
            case Enseignant:
                return "enseignant";
            case Non_enseignant_collectivite_locale:
                return "nonenseignantcollectivitelocale";
            case Non_enseignant_service_academique:
                return "nonenseignantserviceacademique";
            case Non_enseignant_etablissement:
                return "nonenseignantetablissement";
            case Personne_relation_eleve:
                return "personnerelationeleve";
            case Personnel_exterieur:
                return "personnelexterieur";
            case Responsable_Entreprise:
                return "responsableentreprise";
            case Tuteur_stage:
                return "tuteurstage";
        }
        return null;
    }

}
