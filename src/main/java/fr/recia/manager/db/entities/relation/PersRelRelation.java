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

package fr.recia.manager.db.entities.relation;

import fr.recia.manager.db.entities.personne.Eleve;
import fr.recia.manager.db.entities.personne.PersonneRelationEleve;
import fr.recia.manager.db.enums.CategorieRelation;
import fr.recia.manager.db.enums.LienParente;
import fr.recia.manager.db.enums.ResponsableLegal;
import fr.recia.manager.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue(value = "PERSREL")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PersRelRelation extends AMappingRelation {

    /**
     * Type énuméré du type de lien de parenté.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I30)
    private LienParente lienParente;
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean responsableFinancier;
    /**
     * Type énuméré du type de la relation de stage.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I15)
    private ResponsableLegal responsableLegal;
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean contact;
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean paiement;
    /* La relation est une adresse de domiciliation. */
    @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
    private boolean adresse;

    /**
     * @param source
     * @param persRelEleve
     * @param eleve
     * @param lienParente
     * @param responsableFinancier
     * @param responsableLegal
     * @param contact
     * @param paiement
     * @param adresse
     */
    public PersRelRelation(final String source, final PersonneRelationEleve persRelEleve, final Eleve eleve,
                           final LienParente lienParente, final boolean responsableFinancier,
                           final ResponsableLegal responsableLegal, final boolean contact,
                           final boolean paiement, final boolean adresse) {
        super(source, eleve, persRelEleve, CategorieRelation.PersRel);
        this.lienParente = lienParente;
        this.responsableFinancier = responsableFinancier;
        this.responsableLegal = responsableLegal;
        this.contact = contact;
        this.paiement = paiement;
        this.adresse = adresse;
    }

}
