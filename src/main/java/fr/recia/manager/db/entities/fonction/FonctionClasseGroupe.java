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

import fr.recia.manager.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategorieFonction;
import fr.recia.manager.db.enums.TypeClasse;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FonctionClasseGroupe extends AFonction {

    /**
     * Type énuméré du type de fonction.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I30)
    private TypeClasse type;

    /**
     * Relation bidirectionnelle.
     * Classe ou groupe concerné.
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_groupe_fk")
    private AGroupeOfFoncClasseGroupe classeGroupe;

    /**
     * Constructeur de l'objet FonctionClasseGroupe.java.
     */
    public FonctionClasseGroupe() {
        super();
        this.setCategorie(CategorieFonction.Classe);
    }

    /**
     * Constructeur de l'objet FonctionClasseGroupe.java.
     *
     * @param type         Type énuméré du type de fonction.
     * @param personne     Personne ayant cette fonction.
     * @param classeGroupe Classe ou groupe concerné.
     * @param source       Source d'alimentation gérant cette fonction.
     */
    public FonctionClasseGroupe(final TypeClasse type, final APersonne personne,
                                final AGroupeOfFoncClasseGroupe classeGroupe, final String source) {
        super(CategorieFonction.Classe, personne, source);
        this.type = type;
        this.classeGroupe = classeGroupe;
    }

}
