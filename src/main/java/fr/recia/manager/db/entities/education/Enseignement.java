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

package fr.recia.manager.db.entities.education;

import fr.recia.manager.db.entities.common.AbstractSimpleEntity;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"code", "matiere", "source"})
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Enseignement extends AbstractSimpleEntity {

    /**
     * Code de la discipline.
     */
    @Column(/*nullable = false,*/length = IntConst.I20)
    private String code;
    /**
     * Année scolaire de validité de l'objet.
     * Année à la rentrée de septembre.
     */
    private LocalDate anneeScolaire;
    /**
     * Libellé de la matière enseignée.
     */
    @Column(nullable = false, length = IntConst.I128)
    private String matiere;
    /**
     * Source d'alimentation de la discipline de poste.
     */
    @Column(nullable = false, length = IntConst.ISOURCE)
    private String source;
    /**
     * Code matière de rattachement National, si matière académique.
     */
    @Column(length = IntConst.I20)
    private String codeRattach;

    /**
     * Constructeur de l'objet Enseignement.java.
     *
     * @param matiere Libellé de la matière enseignée.
     * @param source  Source d'alimentation de l'objet.
     */
    public Enseignement(final String matiere, final String source) {
        super();
        this.matiere = matiere;
        this.source = source;
    }

    /**
     * Constructeur de l'objet Enseignement.java.
     *
     * @param code    Code de la matière.
     * @param matiere Libellé de la matière enseignée.
     * @param source  Source d'alimentation de l'objet.
     */
    public Enseignement(final String code, final String matiere, final String source) {
        super();
        this.code = code;
        this.matiere = matiere;
        this.source = source;
    }

}
