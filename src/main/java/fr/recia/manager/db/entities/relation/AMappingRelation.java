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
import fr.recia.manager.db.enums.CategorieRelation;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.AssociationOverride;
import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "relations_apersonnes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AssociationOverrides({
    @AssociationOverride(name = "pk.personne1", joinColumns = @JoinColumn(name = "APERSONNE1_ID")),
    @AssociationOverride(name = "pk.personne2", joinColumns = @JoinColumn(name = "APERSONNE2_ID"))
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AMappingRelation implements Serializable {

    /**
     * The Source which insert the entry.
     */
    @Basic
    @Column(name = "SOURCE", length = IntConst.ISOURCE, nullable = false)
    private String source;
    /**
     * The pk
     */
    @EmbeddedId
    private MappingAPersonneAPersonneId pk = new MappingAPersonneAPersonneId();

    /**
     * Contructor of the object MappingAGroupeAPersonne.java.
     *
     * @param source
     * @param personne1
     * @param personne2
     */
    public AMappingRelation(final String source, final APersonne personne1,
                            final APersonne personne2, final CategorieRelation categoryRelation) {
        this.source = source;
        this.pk = new MappingAPersonneAPersonneId(personne1, personne2, categoryRelation);
    }

}
