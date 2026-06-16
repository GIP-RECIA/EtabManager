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

package fr.recia.manager.db.dto.fonction;

import fr.recia.manager.db.dto.education.DisciplineDto;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class TypeFonctionFiliereDto {

    private Long id;
    private String libelle;
    private Set<DisciplineDto> disciplines;
    // Liste des personnes directement dans la filière sans être dans une discipline (pour les CFA)
    private Set<DatabasePersonneDto> personnesWithoutDiscipline;

    // Constructeur utilisé par la requête en BD
    public TypeFonctionFiliereDto(Long id, String codeFiliere, String libelle, String source) {
        this.id = id;
        this.libelle = libelle;
        this.disciplines = new TreeSet<>(Comparator.comparing(DisciplineDto::getLibelle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        this.personnesWithoutDiscipline = new TreeSet<>(
            Comparator.comparing(DatabasePersonneDto::getSn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(DatabasePersonneDto::getCn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(DatabasePersonneDto::getId));
    }

    public TypeFonctionFiliereDto(TypeFonctionFiliereDto typeFonctionFiliereDto) {
        this.id = typeFonctionFiliereDto.getId();
        this.libelle = typeFonctionFiliereDto.getLibelle();
        this.disciplines = new TreeSet<>(Comparator.comparing(DisciplineDto::getLibelle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        this.personnesWithoutDiscipline = new TreeSet<>(
            Comparator.comparing(DatabasePersonneDto::getSn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(DatabasePersonneDto::getCn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(DatabasePersonneDto::getId));
    }

}
