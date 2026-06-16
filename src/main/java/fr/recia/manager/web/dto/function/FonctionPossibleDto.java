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

package fr.recia.manager.web.dto.function;

import fr.recia.manager.db.entities.education.Discipline;
import fr.recia.manager.db.entities.fonction.TypeFonctionFiliere;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FonctionPossibleDto {

    private FiliereDisplayDto filiere;
    private DisciplinePossibleDto discipline;

    public FonctionPossibleDto(TypeFonctionFiliere filiere, Discipline discipline) {
        this.filiere = new FiliereDisplayDto(filiere.getId(), filiere.getLibelleFiliere());
        // Pour les CFA
        if(discipline != null){
            this.discipline = new DisciplinePossibleDto(discipline.getId(), discipline.getDisciplinePoste());
        }
    }
}
