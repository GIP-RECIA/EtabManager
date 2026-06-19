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

import fr.recia.manager.db.entities.personne.Eleve;
import fr.recia.manager.db.entities.personne.Enseignant;
import fr.recia.manager.db.enums.CategorieRelation;
import fr.recia.manager.db.enums.TypeStage;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "ENSRESPSTAGE")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class EnsRespStageRelation extends AStageRelation {

    /**
     * @param source
     * @param tuteurStage
     * @param eleve
     */
    public EnsRespStageRelation(final String source, final Enseignant tuteurStage, final Eleve eleve) {
        super(source, tuteurStage, eleve, TypeStage.Enseignant_responsable, CategorieRelation.EnsRespStage);
    }

}
