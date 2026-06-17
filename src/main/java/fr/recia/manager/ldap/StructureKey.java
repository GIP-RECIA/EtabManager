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

package fr.recia.manager.ldap;

import fr.recia.manager.db.enums.CategorieStructure;
import fr.recia.manager.web.dto.ICompositeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2014
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StructureKey implements ICompositeKey<String, CategorieStructure>, Serializable {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -210670392798727508L;
    /**
     * The identifier of the context.
     */
    @NonNull
    private String keyId;

    /**
     * The type of the context.
     */
    @NonNull
    @Enumerated(EnumType.STRING)
    private CategorieStructure keyType;

}
