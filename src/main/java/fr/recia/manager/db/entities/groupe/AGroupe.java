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

import fr.recia.manager.db.entities.common.AbstractTimedEntity;
import fr.recia.manager.db.enums.CategorieGroupe;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AGroupe extends AbstractTimedEntity {

    /**
     * Type de groupe.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I30)
    private CategorieGroupe categorie;
    /**
     * Année scolaire de validité de l'objet.
     * Année à la rentrée de septembre.
     */
    private LocalDate anneeScolaire;
    /**
     * Nom unique de groupe, peut servir comme identifiant au sein d'un établissement.
     */
    private String cn;
    /**
     * Description du groupe.
     */
    private String description;
    /**
     * Source ayant créé le groupe.
     */
    @Column(length = IntConst.ISOURCE)
    private String source;

    /**
     * Constructeur de l'objet AGroupe.java.
     *
     * @param cn        Nom unique de groupe, peut servir comme identifiant.
     * @param categorie Type de groupe.
     * @param source    Source ayant créé l'objet.
     */
    public AGroupe(final String cn, final CategorieGroupe categorie, final String source) {
        super();
        this.cn = cn;
        this.categorie = categorie;
        this.source = source;
    }

}
