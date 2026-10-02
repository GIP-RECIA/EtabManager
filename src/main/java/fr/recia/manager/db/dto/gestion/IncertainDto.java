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

package fr.recia.manager.db.dto.gestion;

import fr.recia.manager.configuration.Constants;
import lombok.Data;


@Data
public class IncertainDto {

    private String attribut;
    private String value;
    private String texte;
    private boolean obligatoire;

    public IncertainDto(DatabaseIncertainDto databaseIncertainDto){
        this.attribut = databaseIncertainDto.getAttribut();
        this.value = databaseIncertainDto.getValue();
        this.texte = sanitizeText(databaseIncertainDto.getTexte());
        this.obligatoire = databaseIncertainDto.isObligatoire();
    }

    private String sanitizeText(String texte) {
        final int separatorIndex = texte.indexOf(Constants.INCERTAIN_DELIMITER);
        if (separatorIndex >= 0) {
            texte = texte.substring(0, separatorIndex);
        }
        final int prefixIndex = texte.indexOf(Constants.INCERTAIN_PREFIX);
        if (prefixIndex >= 0) {
            texte = texte.substring(prefixIndex + Constants.INCERTAIN_PREFIX.length());
        }
        return texte;
    }
}
