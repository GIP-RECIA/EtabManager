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

import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GroupementEtablissements extends AGroupe {

    /**
     * Relation bidirectionnelle.
     * Liste des Etablissements membres du groupe.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "groupements_etablissements",
        joinColumns = @JoinColumn(name = "GROUPEMENTETABLISSEMENT_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "ETABLISSEMENT_ID", referencedColumnName = "ID")
    )
    private Set<Etablissement> membres = new HashSet<>();

    /**
     * Constructeur de l'objet GroupementEtablissements.java.
     */
    public GroupementEtablissements() {
        super();
        this.setCategorie(CategorieGroupe.Groupement_etablissement);
    }

    /**
     * Constructeur de l'objet GroupementEtablissements.java.
     *
     * @param cn      Nom unique de groupe, peut servir comme identifiant.
     * @param membres Liste des Etablissements membres du groupe.
     * @param source  Source ayant créé l'objet.
     */
    public GroupementEtablissements(final String cn, final Set<Etablissement> membres, final String source) {
        super(cn, CategorieGroupe.Groupement_etablissement, source);
        this.membres = membres;
    }

}
