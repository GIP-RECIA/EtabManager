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

package fr.recia.manager.db.entities.groupe;

import fr.recia.manager.db.entities.application.Application;
import fr.recia.manager.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class RoleApplicatif extends AGroupeOfAPersonne {

	/** Relation unidirectionnelle.
	 * Liste des profils utiles au rôle applicatif. */
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "roles_applicatifs_profils",
			joinColumns =
				@JoinColumn(name = "ROLEAPPLICATIF_ID", referencedColumnName = "ID"),
			inverseJoinColumns =
				@JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID"))
	private Set<Profil> profils = new HashSet<>();

	/** Relation bidirectionnelle.
	 * Application pour laquelle le role applicatif est défini.*/
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "APPLICATION_ID")
	private Application proprietaire;

	public RoleApplicatif() {
		super();
		this.setCategorie(CategorieGroupe.Role_applicatif);
	}

	public RoleApplicatif(final String cn, final Set<MappingAGroupeAPersonne> membres, final Application application, final String source) {
		super(cn, CategorieGroupe.Role_applicatif, membres, source);
		this.proprietaire = application;
	}

	@Override
	public String toString() {
        return "RoleApplicatif [" + super.toString() + ", " + this.profils + "]";
	}

}
