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
import fr.recia.manager.db.entities.personne.Eleve;
import fr.recia.manager.db.enums.CategorieRelation;
import fr.recia.manager.db.enums.TypeStage;
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

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AStageRelation extends AMappingRelation {

    /**
     * Type énuméré du type de la relation de stage.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I30)
    private TypeStage type;

    /**
     * @param source
     * @param tuteurStage
     * @param eleve
     * @param type
     * @param typeStage
     */
    public AStageRelation(final String source, final APersonne tuteurStage, final Eleve eleve,
                          final TypeStage type, final CategorieRelation typeStage) {
        super(source, tuteurStage, eleve, typeStage);
        this.type = type;
    }

}
