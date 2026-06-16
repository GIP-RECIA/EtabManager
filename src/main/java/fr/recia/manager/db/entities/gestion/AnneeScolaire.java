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

package fr.recia.manager.db.entities.gestion;

import fr.recia.manager.db.entities.common.AbstractEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AnneeScolaire extends AbstractEntity {

    /**
     * Informe de l'année en cours, une seule insertion par année possible,
     * année à la rentrée (de septembre).
     */
    @Column(unique = true, nullable = false)
    @Temporal(TemporalType.DATE)
    private Date anneeEnCours;
    /**
     * Date exacte du passage à l'année suivante pour les insertions.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date passageAnneeSuivante;
    /**
     * Date de fin des autorisations des accés pour les utilisateurs de l'année scolaire précédente.
     * Si null on y place la date de passageAnneeSuivante.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date finAutorisation;
    /**
     * Booleen indiquant l'insertion du complet des établissements en début d'année scolaire.
     */
    @Column(nullable = false, columnDefinition = "BIT")
    private boolean etabMAJ;

    /**
     * Constructeur de l'objet AnneeScolaire.java.
     *
     * @param anneeEnCours         Année scolaire en cours, année à la rentrée (de septembre).
     * @param passageAnneeSuivante Date de passage des insertions à l'année suivante.
     * @param finAutorisation      Date de fin des autorisations des accés
     *                             pour les utilisateurs de l'année scolaire précédente.
     */
    public AnneeScolaire(final Date anneeEnCours, final Date passageAnneeSuivante, final Date finAutorisation) {
        super();
        this.anneeEnCours = anneeEnCours;
        this.passageAnneeSuivante = passageAnneeSuivante;
        this.finAutorisation = finAutorisation;
    }

}
