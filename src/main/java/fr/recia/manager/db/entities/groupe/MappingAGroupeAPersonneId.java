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

import fr.recia.manager.db.entities.personne.APersonne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
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
public class MappingAGroupeAPersonneId implements Serializable {

    /**
     * The personne to associate to the groupe.
     */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "APERSONNE_ID", nullable = false)
    private APersonne personne;

    /**
     * The groupe to associate with the person.
     */
    @ManyToOne
    @JoinColumn(name = "AGROUPEOFAPERS_ID", nullable = false)
    private AGroupeOfAPersonne groupe;

}
