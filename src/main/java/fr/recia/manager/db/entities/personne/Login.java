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

import fr.recia.manager.db.entities.common.AbstractEntity;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"APERSONNE_LOGIN"}),
    @UniqueConstraint(columnNames = {"APERSONNE_ALIAS"})
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Login extends AbstractEntity {

    /**
     * Nom de login.
     */
    @Column(unique = true, nullable = false, length = IntConst.I100)
    private String nom;

    /**
     * Relation bidirectionnelle.
     * Personne ayant ce login.
     */
    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "apersonne_login")
    private APersonne apersonneLogin;
    /**
     * Relation bidirectionnelle.
     * Personne ayant cet alias.
     */
    @OneToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "apersonne_alias")
    private APersonne apersonneAlias;
    /**
     * Relation bidirectionnelle.
     * Personne ayant eu ce login/alias.
     */
    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "apersonne_old_alias")
    private APersonne apersonneOldAlias;

    /**
     * Constructeur de l'objet Login.java.
     *
     * @param nom Nom de login.
     */
    public Login(final String nom) {
        super();
        this.nom = nom;
    }

}
