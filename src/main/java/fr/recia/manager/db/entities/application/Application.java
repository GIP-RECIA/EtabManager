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

package fr.recia.manager.db.entities.application;

import fr.recia.manager.db.entities.common.AbstractEntity;
import fr.recia.manager.db.entities.groupe.Profil;
import fr.recia.manager.db.entities.groupe.RoleApplicatif;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.enums.CategorieApplication;
import fr.recia.manager.db.utils.IntConst;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Application extends AbstractEntity {

	//Attibuts
	/** Catégorie de l'application. */
	@Enumerated(EnumType.STRING)
	@Column(length = IntConst.I40)
	private CategorieApplication categorie;
	/** Description de l'application. */
	private String description;
	/** Identifiant unique de l'application. */
	@Column(unique = true, length = IntConst.I40)
	private String identifiant;
	/** Nom unique de l'application, pouvant servir de login. */
	@Column(unique = true, length = IntConst.I100)
	private String nom;
	/** Password de l'application. */
	private String password;

	//Relations
	/** Relation bidirectionnelle. */
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "applications_profils", joinColumns = @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID"))
	private Set<Profil> profils = new HashSet<>();

	/** Relation unidirectionnelle. */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinColumn(name = "apersonne_fk")
	private APersonne proprietaire;

	/** Relation bidirectionnelle. */
	@OneToMany(mappedBy = "proprietaire", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	private Set<RoleApplicatif> rolesApplicatifs = new HashSet<>();

	@Override
	public String toString() {
        return "Application [" + super.toString() + ", " + this.categorie + ", " + this.identifiant + ", " + this.nom + ", " + this.description + ", " + this.password + "]";
	}

}
