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

public enum TypeStage {
    /**
     * Enseignant étant responsable de l'élève en stage.
     */
    Enseignant_responsable,
    /**
     * Enseignant étant le tuteur de l'élève en stage.
     */
    Enseignant_tuteur,
//  /**
//   * Personne de l'entreprise étant responsable de l'élève en stage.
//   */
//  Responsable,
    /**
     * Personne de l'entreprise étant le tuteur de l'élève en stage.
     */
    Tuteur
}
