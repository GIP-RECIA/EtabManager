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

public enum CategorieGroupe {
    /**
     * Groupe de type classe au sein d'un établissement.
     */
    Classe,
    /**
     * Groupe de type groupe au sein d'un établissement.
     */
    Groupe,
    /**
     * Groupe de type groupement d'établissment.
     */
    Groupement_etablissement,
    /**
     * Groupe de type profil, rassemblant des personnes d'un certain profile.
     */
    Profil,
    /**
     * Groupe de type relation avec un élève,
     * désignant ainsi toutes les personnes en relation avec un élève.
     */
    Relation_eleve,
    /**
     * Groupe de type rôle applicatif, permettant de définir des rôles
     * pour des groupes d'utilisateurs au sein d'une application.
     */
    Role_applicatif,
    /**
     * Groupe de fusion de comptes, interne à sarapis.
     */
    Fusion
}
