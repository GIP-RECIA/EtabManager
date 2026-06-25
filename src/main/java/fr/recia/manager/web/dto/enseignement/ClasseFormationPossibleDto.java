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

package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.ClasseDto;
import fr.recia.manager.db.entities.education.Enseignement;
import lombok.Data;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
public class ClasseFormationPossibleDto {
    private long id;
    private String libelle;
    private Set<EnseignementFormationPossibleDto> enseignements;

    public ClasseFormationPossibleDto(ClasseDto classe){
        this.id = classe.getId();
        this.libelle = classe.getCn();
        this.enseignements = new TreeSet<>(Comparator.comparing(EnseignementFormationPossibleDto::getLibelle));
    }

    public void addEnseignement(Enseignement enseignement){
        EnseignementFormationPossibleDto enseignementFormationPossibleDto = new EnseignementFormationPossibleDto(enseignement);
        this.enseignements.add(enseignementFormationPossibleDto);
    }
}
