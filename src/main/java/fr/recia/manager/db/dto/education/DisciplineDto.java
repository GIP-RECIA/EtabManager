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

package fr.recia.manager.db.dto.education;

import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.web.dto.user.CardPersonneDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class DisciplineDto {

    private Long id;
    private String libelle;
    private Set<CardPersonneDto> personnes;
    private Set<CategoriePersonne> categories;

    // Constructeur utilisé par la requête en BD
    public DisciplineDto(Long id, String code, String libelle, String source) {
        this.id = id;
        this.libelle = libelle;
        this.personnes = new TreeSet<>(
                Comparator.comparing(CardPersonneDto::getCn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                    .thenComparing(CardPersonneDto::getId));
        this.categories = new HashSet<>();
    }

    public DisciplineDto(DisciplineDto disciplineDto) {
        this.id = disciplineDto.getId();
        this.libelle = disciplineDto.getLibelle();
        this.personnes = new TreeSet<>(
            Comparator.comparing(CardPersonneDto::getCn, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(CardPersonneDto::getId));
        this.categories = new HashSet<>();
    }

}
