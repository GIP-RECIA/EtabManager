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

package fr.recia.manager.db.entities.fonction;

import fr.recia.manager.db.entities.education.MEF;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Parent;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MappingFonctionMEFMEF {

    /**
     * Entity parent fonctionMEF
     */
    @Parent
    @Column(name = "FONCTIONMEF_ID", nullable = false)
    private FonctionMEF fonctionMEF;
    /**
     * Source d'alimentation de l'association.
     */
    @Basic
    @Column(length = IntConst.ISOURCE, name = "SOURCE", nullable = false)
    private String source;
    /**
     * Si l'enseignant est responsable de formation.
     */
    @Basic
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false", name = "RESPONSABLE")
    private boolean isResponsable;
    /**
     * Relation avec le MEF.
     */
    @OneToOne
    @JoinColumn(name = "MEF_ID", nullable = false)
    private MEF mef;

    /**
     * Contructor of the object MappingFonctionMEFMEF.java.
     *
     * @param source
     * @param mef
     * @param isResponsable
     */
    public MappingFonctionMEFMEF(final String source, final MEF mef, final boolean isResponsable) {
        super();
        this.source = source;
        this.mef = mef;
        this.isResponsable = isResponsable;
    }

}
