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
import fr.recia.manager.db.entities.structure.AStructure;
import fr.recia.manager.db.enums.CategorieGroupe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Profil extends AGroupeOfAPersonne {

	/** Définition de la règle de peuplement. */
	private String reglePeuplement;

		//Relations
	/** Relation bidirectionnelle.
	 * Structure ayant défini le profil. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "structure_fk")
	private AStructure proprietaire;

	/** Relation bidirectionnelle.
	 * Listes des applications ayant besoin de ce profil.*/
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "profils")
	private Set<Application> applications = new HashSet<>();

	public Profil() {
		super();
		this.setCategorie(CategorieGroupe.Profil);
	}

	public Profil(final String cn, final Set<MappingAGroupeAPersonne> membres,
			final String reglePeuplement, final String source) {
		super(cn, CategorieGroupe.Profil, membres, source);
		this.reglePeuplement = reglePeuplement;
	}

	@Override
	public String toString() {
        return "Profil [" + super.toString() + ", " + this.reglePeuplement + "]";
	}

}
