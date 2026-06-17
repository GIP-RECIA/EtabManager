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
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.AStructure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Incertain extends AbstractSimpleEntity {

    /**
     * Nom de l'attribut xml de la source posant problème.
     */
    private String attribut;
    /**
     * Création automatique de la date de création de l'objet lors de la construction.
     */
    private LocalDateTime dateCreation;
    /**
     * Valeur pouvant poser problème.
     */
    private String value;
    /**
     * Texte explicatif.
     */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String texte;
    /**
     * Indique le type du champ posant problème,
     * vrai si champ obligatoire, faux si facultatif.
     * Si faux la personne ou la structure n'est pas dans un état incertain.
     */
    @Column(nullable = false, columnDefinition = "BIT")
    private boolean obligatoire;

    /**
     * Relation bidirectionnelle.
     * Personne incertaine.
     */
    @ManyToOne
    private APersonne incertainPers;
    /**
     * Relation bidirectionnelle.
     * Structure Incertaine.
     */
    @ManyToOne
    private AStructure incertainStruct;

    /**
     * Constructeur de l'objet Incertain.java.
     *
     * @param attribut    Attribut posant problème.
     * @param value       Valeur de l'attribut si renseigné.
     * @param obligatoire Vrai si le champ est obligatoire faux si facultatif.
     * @param incertain   Personne ayant des attributs à problème, pouvant bloqué l'état à incertain.
     * @param texte       Texte facultatif pour indiquer le type de problème.
     */
    public Incertain(final String attribut, final String value, final boolean obligatoire,
                     final APersonne incertain, final String texte) {
        super();
        this.attribut = attribut;
        this.obligatoire = obligatoire;
        this.incertainPers = incertain;
        this.texte = texte;
        this.value = value;
    }

    /**
     * Constructeur de l'objet Incertain.java.
     *
     * @param attribut    Attribut posant problème.
     * @param value       Valeur de l'attribut si renseigné.
     * @param obligatoire Vrai si le champ est obligatoire faux si facultatif.
     * @param incertain   Structure ayant des attributs à problème, pouvant bloqué l'état à incertain.
     * @param texte       Texte facultatif pour indiquer le type de problème.
     */
    public Incertain(final String attribut, final String value, final boolean obligatoire,
                     final AStructure incertain, final String texte) {
        super();
        this.attribut = attribut;
        this.obligatoire = obligatoire;
        this.incertainStruct = incertain;
        this.texte = texte;
        this.value = value;
    }

    /**
     * Constructeur de l'objet Incertain.java.
     * Cas où la structure est indéfinie et que des personne y sont rattachées
     * ou par la définition d'une classe appartenant à la structure.
     *
     * @param attribut        Attribut posant problème.
     * @param value           Valeur de l'attribut si renseigné.
     * @param obligatoire     Vrai si le champ est obligatoire faux si facultatif.
     * @param incertainPers   Personne ayant des attributs à problème, pouvant bloqué l'état à incertain.
     * @param incertainStruct Structure ayant des attributs à problème, pouvant bloqué l'état à incertain.
     * @param texte           Texte facultatif pour indiquer le type de problème.
     */
    public Incertain(final String attribut, final String value, final boolean obligatoire, final APersonne incertainPers,
                     final AStructure incertainStruct, final String texte) {
        super();
        this.attribut = attribut;
        this.obligatoire = obligatoire;
        this.incertainPers = incertainPers;
        this.incertainStruct = incertainStruct;
        this.texte = texte;
    }

    /**
     * Setter automatique du membre dateCreation lors de la creation de l'objet.
     */
    @PrePersist
    public void prePersistOps() {
        LocalDateTime d = LocalDateTime.now();
        if (this.dateCreation == null) {
            this.dateCreation = d;
        }
    }

}
