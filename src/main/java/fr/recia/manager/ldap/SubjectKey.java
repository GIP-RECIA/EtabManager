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

import fr.recia.manager.ldap.enums.SubjectType;
import fr.recia.manager.web.dto.ICompositeKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2014
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class SubjectKey implements ICompositeKey<String, SubjectType>, Serializable {

    /**
     * Serial Version id.
     */
    private static final long serialVersionUID = 687644117634464074L;

    /**
     * This field corresponds to the database column subject_id.
     */
    @NonNull
    private String keyId;

    /**
     * This field corresponds to the database column subject_type.
     */
    @NonNull
    @Enumerated(EnumType.STRING)
    private SubjectType keyType;

}
