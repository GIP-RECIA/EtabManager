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

package fr.recia.manager.db.entities.structure;

import fr.recia.manager.db.entities.common.CleJointure;
import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class ServiceAcademique extends AStructure {

    /**
     * Numéro UAI, ancien RNE.
     */
    @Column(unique = true, length = IntConst.IUAI)
    private String uai;
    /**
     * Nom de l'académie ou code.
     */
    private String academie;

    /**
     * Constructeur de l'objet ServiceAcademique.java.
     */
    public ServiceAcademique() {
        super();
        this.setCategorie(CategorieStructure.Service_academique);
    }

    /**
     * Constructeur de l'objet ServiceAcademique.java.
     *
     * @param uai         Numéro UAI, ancien RNE.
     * @param nom         Nom unique de la stucture.
     * @param siren       Numéro de SIRET/SIREN unique de la structure.
     * @param cleJointure Clé de jointure unique de la structure.
     * @param academie    Nom de l'académie ou code.
     */
    public ServiceAcademique(final String uai, final String nom, final String siren,
                             final CleJointure cleJointure, final String academie) {
        super(CategorieStructure.Service_academique, nom, siren, cleJointure);
        this.academie = academie;
        this.uai = uai;
    }

}
