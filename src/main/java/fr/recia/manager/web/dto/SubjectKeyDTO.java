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

package fr.recia.manager.web.dto;

import fr.recia.manager.ldap.enums.SubjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectKeyDTO implements ICompositeKey<String, SubjectType>, Serializable {

    @NonNull
    private String keyId;
    @NonNull
    private SubjectType keyType;

    /**
     * @param keyId
     * @param keyType
     */
    public SubjectKeyDTO(@NonNull final String keyId, @NonNull final SubjectType keyType) {
        this.keyId = keyId;
        this.keyType = keyType;
    }

}
