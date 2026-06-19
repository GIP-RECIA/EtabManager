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

package fr.recia.manager.db.entities.common;

import fr.recia.manager.db.entities.personne.MappingAPersonneService;
import fr.recia.manager.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"libelleService", "source"}) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class TypeService extends AbstractSimpleEntity {

    /**
     * Nom du service.
     */
    @Column(nullable = false)
    private String libelleService;
    /**
     * Source de l'alimentation.
     */
    @Column(nullable = false, length = IntConst.ISOURCE)
    private String source;
    /**
     * Relation bidirectionnelle. Liste des personnes qui possèdent ce service.
     */
    @OneToMany(mappedBy = "pk.service", fetch = FetchType.LAZY)
    private Set<MappingAPersonneService> personnes = new HashSet<>();

}
