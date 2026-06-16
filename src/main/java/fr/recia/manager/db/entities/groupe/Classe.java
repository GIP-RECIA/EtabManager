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

import fr.recia.manager.db.entities.education.MEF;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Classe extends AGroupeOfFoncClasseGroupe {

    /**
     * Mefs associé à la classe.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "classes_mefs",
        joinColumns = @JoinColumn(name = "CLASSE_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "MEF_ID", referencedColumnName = "ID")
    )
    private Set<MEF> mefs = new HashSet<>();

    /**
     * Constructeur de l'objet Classe.java.
     */
    public Classe() {
        super();
        this.setCategorie(CategorieGroupe.Classe);
    }

    /**
     * Constructeur de l'objet Classe.java.
     *
     * @param cn           Nom unique de la classe, peut servir comme identifiant.
     * @param membres      Liste des personnes membre de la classe.
     * @param proprietaire Etablissement ayant défini cette classe.
     * @param source       Source ayant créé l'objet.
     */
    public Classe(final String cn, final Set<MappingAGroupeAPersonne> membres,
                  final Etablissement proprietaire, final String source) {
        super(cn, CategorieGroupe.Classe, membres, proprietaire, source);
    }

}
