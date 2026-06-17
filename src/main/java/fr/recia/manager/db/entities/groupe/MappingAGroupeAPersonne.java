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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "apersonnes_agroupes")
@AssociationOverrides({
    @AssociationOverride(
        name = "pk.personne",
        joinColumns = @JoinColumn(name = "APERSONNE_ID")),
    @AssociationOverride(
        name = "pk.groupe",
        joinColumns = @JoinColumn(name = "AGROUPEOFAPERS_ID"))
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MappingAGroupeAPersonne implements Serializable {

    /**
     * The Source which insert the entry.
     */
    @Basic
    @Column(name = "SOURCE", length = IntConst.ISOURCE, nullable = false)
    private String source;

    /** Donne l'information de la date de fin de l'objet. */
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    /**
     * The pk
     */
    @EmbeddedId
    private MappingAGroupeAPersonneId pk = new MappingAGroupeAPersonneId();

    /**
     * Contructor of the object MappingAGroupeAPersonne.java.
     *
     * @param source
     * @param groupe
     * @param personne
     */
    public MappingAGroupeAPersonne(final String source, final APersonne personne, final AGroupeOfAPersonne groupe) {
        this.source = source;
        this.pk = new MappingAGroupeAPersonneId(personne, groupe);
    }

}
