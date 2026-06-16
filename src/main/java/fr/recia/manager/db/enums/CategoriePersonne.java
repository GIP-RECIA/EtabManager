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

package fr.recia.manager.db.enums;

public enum CategoriePersonne {
    /**
     * Un élève.
     */
    Eleve,
    /**
     * Un enseignant.
     */
    Enseignant,
    /**
     * Une personne non enseignante d'une collectivité locale.
     */
    Non_enseignant_collectivite_locale,
    /**
     * Une personne non enseignante faisant partie du personnel d'un établissement.
     */
    Non_enseignant_etablissement,
    /**
     * Une personne non enseignante d'un service académique.
     */
    Non_enseignant_service_academique,
    /**
     * Une personne étant uniquement une personne en custom mappings avec un élève.
     */
    Personne_relation_eleve,
    /**
     * Une personne du personnel extérieur.
     */
    Personnel_exterieur,
    /**
     * Un responsable d'entreprise.
     */
    Responsable_Entreprise,
    /**
     * Un tuteur de stage.
     */
    Tuteur_stage
}
