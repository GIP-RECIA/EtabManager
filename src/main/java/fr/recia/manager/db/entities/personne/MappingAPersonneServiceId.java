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

package fr.recia.manager.db.entities.personne;

import fr.recia.manager.db.entities.common.TypeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MappingAPersonneServiceId implements Serializable {

    /** La personne qui possède le service */
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "APERSONNE_ID")
    private APersonne personne;

    /** Le service exercé par la personne */
    @ManyToOne
    @JoinColumn(name = "SERVICE_ID")
    private TypeService service;

    @Override
    public String toString() {
        return "MappingAPersonneService [apersonne=" + personne.getUid() + ", typeservice=" + service.getLibelleService() + "]";
    }

}
