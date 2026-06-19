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
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.enums.CategorieFonction;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class FonctionMEF extends AFonction {

    /**
     * Relation unidirectionnelle.
     */
    @ManyToOne
    @JoinColumn(name = "etablissement_fk")
    private Etablissement etablissement;

    /**
     * Relation unidirectionnelle.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fonctions_mefs",
        joinColumns = @JoinColumn(name = "FONCTIONMEF_ID", referencedColumnName = "ID")
    )
    private Set<MappingFonctionMEFMEF> mefs = new HashSet<>();

    /**
     * Constructeur de l'objet FonctionMEF.java.
     */
    public FonctionMEF() {
        super();
        this.setCategorie(CategorieFonction.MEF);
    }

    /**
     * Constructeur de l'objet FonctionMEF.java.
     *
     * @param mefs          Liste des mefs.
     * @param etablissement l'établissement associé à l'enseignement de ces mef
     * @param personne      la personne ayant la fonction d'enseigner associé à ce mefs.
     * @param source        Source d'alimentation gérant cette fonction.
     */
    public FonctionMEF(final Set<MappingFonctionMEFMEF> mefs, final Etablissement etablissement,
                       final APersonne personne, final String source) {
        super(CategorieFonction.MEF, personne, source);
        this.mefs = mefs;
        this.etablissement = etablissement;
    }

}
