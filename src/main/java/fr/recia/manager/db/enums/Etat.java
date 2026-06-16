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

public enum Etat {
    /**
     * Objet dans un état invalide, cas ou l'utilisateur n'aurait pas encore valider son compte.
     */
    Invalide,
    /**
     * Objet dans un état valide, l'utilisateur a validé son compte.
     */
    Valide,
    /**
     * Objet dans un état bloqué, un administrateur a bloqué le compte de l'utilisateur.
     */
    Bloque,
    /**
     * Objet dans un état de suppression exporté, la source d'alimentation a demandé la suppression du compte.
     */
    Delete,
    /**
     * Objet dans un état incertain, les attributs obligatoires ne sont pas renseignés.
     */
    Incertain,
    /**
     * Objet dans un état incertain suite à une erreur d'export lors du delete.
     */
    Incertain_Export_Delete,
    /**
     * Objet dans un état incertain suite à une erreur d'export lors de la modification.
     */
    Incertain_Export_Modify,
    /**
     * Objet dans un état incertain suite à une erreur d'export lors de l'ajout.
     */
    Incertain_Export_Add,
    Deleting
}
