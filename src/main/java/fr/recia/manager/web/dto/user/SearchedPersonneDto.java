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

import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class SearchedPersonneDto {

    private Long id;
    private Etat etat;
    private boolean local;
    private String cn;
    private LocalDateTime dateSuppression;
    private String uid;
    private String email;

    public SearchedPersonneDto(DatabasePersonneDto databasePersonneDto) {
        this.id = databasePersonneDto.getId();
        this.etat = databasePersonneDto.getEtat();
        this.local = databasePersonneDto.getSource().startsWith("SarapisUi_");
        this.cn = databasePersonneDto.getCn();
        this.dateSuppression = databasePersonneDto.getDateSuppression();
        this.uid = databasePersonneDto.getUid();
        this.email = databasePersonneDto.getEmail();
    }

}
