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

package fr.recia.manager.db.dto;

import fr.recia.manager.services.alert.FonctionAlertType;
import fr.recia.manager.web.dto.function.DisciplineAlertDto;
import fr.recia.manager.web.dto.function.FiliereAlertDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AlertDto {

    private FiliereAlertDto filiere;
    private DisciplineAlertDto discipline;
    private int min;
    private int max;
    private int nbActuel;
    private AlertType level;
    private FonctionAlertType type;
    private boolean action;

}
