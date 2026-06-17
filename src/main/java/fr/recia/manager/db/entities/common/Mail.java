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

package fr.recia.manager.db.entities.common;

import fr.recia.manager.db.enums.MailType;
import fr.recia.manager.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Mail extends AbstractSimpleEntity {

    /**
     * Adresse mail.
     */
    @Column(nullable = false)
    private String adresse;
    /**
     * Type de l'adresse.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I20)
    private MailType type;
    /**
     * Source de l'alimentation.
     */
    @Column(nullable = false, length = IntConst.ISOURCE)
    private String source;
    /**
     * Si adresse validée
     */
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean validated;

}
