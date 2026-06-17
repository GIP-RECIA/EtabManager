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

import fr.recia.manager.db.utils.IntConst;
import lombok.AllArgsConstructor;
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

/**
 * Table de jointure entre personne et service avec la source et une date de fin.
 * <DL>
 * <DT><b>Champs obligatoires :</b></DT>
 * <DD>personne, service, source</DD>
 * </DL>
 *
 * @author GIP RECIA - Cailbourdin Nathan Février 2024
 */
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "apersonnes_services")
@AssociationOverrides({
        @AssociationOverride(name = "pk.personne",
                joinColumns = @JoinColumn(name = "APERSONNE_ID")),
        @AssociationOverride(name = "pk.service",
                joinColumns = @JoinColumn(name = "SERVICE_ID")) })
public class MappingAPersonneService implements Serializable {

    @EmbeddedId
    private MappingAPersonneServiceId pk = new MappingAPersonneServiceId();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    @Basic
    @Column(length = IntConst.ISOURCE, name = "SOURCE", nullable = false)
    private String source;

}
