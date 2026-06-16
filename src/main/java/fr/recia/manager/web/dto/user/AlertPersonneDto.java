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
package fr.recia.manager.web.dto.user;

import fr.recia.manager.db.dto.gestion.DatabaseIncertainDto;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class AlertPersonneDto {

    private Long id;
    private Etat etat;
    private boolean local;
    private String cn;
    private Date dateSuppression;

    public AlertPersonneDto(APersonne aPersonne) {
        this.id = aPersonne.getId();
        this.etat = aPersonne.getEtat();
        this.local = aPersonne.getCleJointure().getSource().startsWith("SarapisUi_");
        this.cn = aPersonne.getCn();
        if (etat == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())) {
            this.etat = Etat.Deleting;
            this.dateSuppression = aPersonne.getDateModification();
        }
    }

    public AlertPersonneDto(DatabaseIncertainDto databaseIncertainDto) {
        this.id = databaseIncertainDto.getId();
        this.etat = databaseIncertainDto.getEtat();
        this.local = databaseIncertainDto.getSource().startsWith("SarapisUi_");
        this.cn = databaseIncertainDto.getCn();
        if (etat == Etat.Delete && databaseIncertainDto.getDateModification().equals(databaseIncertainDto.getDateAcquittement())) {
            this.etat = Etat.Deleting;
            this.dateSuppression = databaseIncertainDto.getDateModification();
        }
    }

}
