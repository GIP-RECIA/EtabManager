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

package fr.recia.manager.db.entities.relation;

import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategorieRelation;
import fr.recia.manager.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MappingAPersonneAPersonneId implements Serializable {

    /**
     * The personne to associate to the personne2.
     */
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "APERSONNE1_ID", nullable = false)
    private APersonne personne1;
    /**
     * The personne to associate to the personne2.
     */
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "APERSONNE2_ID", nullable = false)
    private APersonne personne2;

    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I20)
    private CategorieRelation categorie;

}
