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

package fr.recia.manager.db.entities.groupe;

import fr.recia.manager.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FusionPersonne extends AGroupeOfAPersonne {

    /**
     * Constructeur de l'objet FusionPersonne.java.
     */
    public FusionPersonne() {
        super();
        this.setCategorie(CategorieGroupe.Fusion);
    }

    /**
     * Constructeur de l'objet FusionPersonne.java.
     *
     * @param cn      Nom du groupe de fusion, peut servir à définir le compte principal.
     * @param membres Liste des personnes fusionnant.
     * @param source  Source ayant créé l'objet.
     */
    public FusionPersonne(final String cn, final Set<MappingAGroupeAPersonne> membres, final String source) {
        super(cn, CategorieGroupe.Fusion, membres, source);
    }

}
