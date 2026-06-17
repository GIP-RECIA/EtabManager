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

package fr.recia.manager.db.entities.fonction;

import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.CategorieFonction;
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
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FonctionDomaine extends AFonction {

    /**
     * Relation unidirectionnelle.
     * Domaines d'exercice.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "fonctions_domaines",
        joinColumns = @JoinColumn(name = "FONCTIONDOMAINE_ID", referencedColumnName = "ID"),
        inverseJoinColumns = @JoinColumn(name = "TYPEDOMAINE_ID", referencedColumnName = "ID")
    )
    private Set<TypeDomaine> domaines = new HashSet<>();
    /**
     * Relation unidirectionnelle.
     * Structure d'exercice des domaines.
     */
    @ManyToOne
    @JoinColumn(name = "astructure_fk", insertable = false, updatable = false)
    private AStructure structure;

    /**
     * Constructeur de l'objet FonctionDomaine.java.
     */
    public FonctionDomaine() {
        super();
        this.setCategorie(CategorieFonction.Domaine);
    }

    /**
     * Constructeur de l'objet FonctionDomaine.java.
     *
     * @param domaines  Domaines d'exercice.
     * @param structure Structure d'exercice des domaines.
     * @param personne  Personne exerçant ce domaine.
     * @param source    Source d'alimentation gérant cette fonction.
     */
    public FonctionDomaine(final Set<TypeDomaine> domaines, final AStructure structure,
                           final APersonne personne, final String source) {
        super(CategorieFonction.Domaine, personne, source);
        this.domaines = domaines;
        this.structure = structure;
    }

}
