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

package fr.recia.manager.db.entities.gestion;

import fr.recia.manager.db.entities.common.AbstractSimpleEntity;
import fr.recia.manager.db.entities.groupe.RoleApplicatif;
import fr.recia.manager.db.entities.structure.AStructure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DroitsAttribut extends AbstractSimpleEntity {

	/** Nom de l'attribut. */
	private String cle;
	/** Autorisation de visibilité de la clé. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean visible;
	/** Autorisation de modification de la clé. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean modifier;
	/** Autorisation d'ajout de la clé. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean ajouter;
	/** Autorisation de suppression de la clé. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean supprimer;
	/** Autorisation de visibilité la clé à d'autres personnes. False par défaut.*/
	@Column(nullable = false, columnDefinition = "BIT")
	private boolean collectif;

	/** Relation unidirectionnelle.
	 * Structure pour laquelle est configuré ce type de droit. */
	@ManyToOne
	@JoinColumn(name = "structure_fk")
	private AStructure structure;

	/** Relation unidirectionnelle.
	 * RoleApplicatif auquel s'applique ce type de droit. */
	@ManyToOne
	@JoinColumn(name = "role_applicatif_fk")
	private RoleApplicatif roleApplicatif;

	@Override
	public String toString() {
        return "DroitAttribut [" + super.toString() + ", " + this.cle + ", " + this.ajouter + ", " + this.visible + ", " +
            this.modifier + ", " + this.supprimer + ", " + this.collectif + ", " + this.structure + ", " + this.roleApplicatif + "]";
	}

}
