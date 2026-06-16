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

public enum TelephoneType {
    /**
     * Téléphone de type fax.
     */
    fax,
    /**
     * Téléphone de type fixe personnel.
     */
    fixe_perso,
    /**
     * Téléphone de type fixe professionnel.
     */
    fixe_pro,
    /**
     * Téléphone de type mobile.
     */
    mobile,
    /**
     * Téléphone de type mobile pour les SMS.
     */
    sms
}
