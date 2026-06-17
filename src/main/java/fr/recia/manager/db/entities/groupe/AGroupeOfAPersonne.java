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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AGroupeOfAPersonne extends AGroupe {

    /**
     * Relation bidirectionnelle. Liste des personnes membres du groupe.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.groupe")
    private Set<MappingAGroupeAPersonne> membres = new HashSet<>();

    /**
     * Constructeur de l'objet AGroupeOfAPersonne.java.
     *
     * @param cn        Nom unique de groupe, peut servir comme identifiant.
     * @param categorie Type de groupe.
     * @param membres   Liste des membres du groupe.
     * @param source    Source ayant créé l'objet.
     */
    public AGroupeOfAPersonne(final String cn, final CategorieGroupe categorie,
                              final Set<MappingAGroupeAPersonne> membres, final String source) {
        super(cn, categorie, source);
        this.membres = membres;
    }

}
