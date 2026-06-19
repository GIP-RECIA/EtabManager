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

import fr.recia.manager.db.entities.common.AbstractTimedEntity;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategorieFonction;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public abstract class AFonction extends AbstractTimedEntity {

    /**
     * Categorie de la fonction.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I20)
    private CategorieFonction categorie;
    /**
     * Source d'alimentation de la fonction.
     */
    @Column(length = IntConst.ISOURCE)
    private String source;

    /**
     * Relation bidirectionnelle.
     *
     * @see APersonne
     */
    @ManyToOne
    @JoinColumn(name = "personne_fk")
    private APersonne personne;

    @Temporal(TemporalType.DATE)
    private Date dateFin;

    /**
     * Constructeur de l'objet AFonction.java.
     *
     * @param categorie Categorie de la fonction.
     * @param personne  Personne ayant cette fonction.
     * @param source    Source d'alimentation gérant cette fonction.
     */
    public AFonction(final CategorieFonction categorie, final APersonne personne, final String source) {
        super();
        this.categorie = categorie;
        this.source = source;
        this.personne = personne;
    }

    /**
     * Constructeur de l'objet AFonction.java.
     *
     * @param categorie Categorie de la fonction.
     * @param personne  Personne ayant cette fonction.
     * @param source    Source d'alimentation gérant cette fonction.
     * @param dateFin   Date de fin de la fonction.
     */
    public AFonction(final CategorieFonction categorie, final APersonne personne, final String source, final Date dateFin) {
        this.categorie = categorie;
        this.source = source;
        this.personne = personne;
        this.dateFin = dateFin;
    }

}
