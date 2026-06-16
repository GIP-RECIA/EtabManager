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

public enum CategorieFonction {
    /**
     * Prof principal, délégué de classe, délégué vie scolaire, délégué remplaçant1, délégué remplaçant2.
     */
    Classe,
    /**
     * Domaines d'exercices.
     */
    Domaine,
    /**
     * Domaines d'exercices au sein d'un groupement d'établissements.
     */
    Domaine_Groupement,
    /**
     * Fonction dans une structure, avec le type de fonction filière et discipline de poste associée.
     */
    Fonction,
    /**
     * Enseignant qui enseigne pour un MEF dans un établissement.
     */
    MEF
}
