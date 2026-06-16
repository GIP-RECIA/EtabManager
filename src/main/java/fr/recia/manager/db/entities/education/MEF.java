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

package fr.recia.manager.db.entities.education;

import fr.recia.manager.db.entities.common.AbstractSimpleEntity;
import fr.recia.manager.db.enums.MEFType;
import fr.recia.manager.db.utils.IntConst;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "source"})})
@Getter
@Setter
@NoArgsConstructor
public class MEF extends AbstractSimpleEntity {

    /**
     * Année scolaire en cours du MEF.
     * Année à la rentrée de septembre.
     */
    @Temporal(TemporalType.DATE)
    private Date anneeScolaire;
    /**
     * code MEF, N_MEF => ENTEleveMEF.
     */
    @Column(nullable = false, length = IntConst.I128)
    private String code;
    /**
     * code MEF_STAT_11.
     */
    @Column(length = IntConst.I40)
    private String codeStat11;
    /**
     * Source d'alimentation du MEF.
     */
    @Column(nullable = false, length = IntConst.ISOURCE)
    private String source;
    /**
     * libelle MEF, N_MEF (10 caractères) si MEF national,
     * texte libre si MEF académique => ENTEleveLibelleMEF.
     */
    private String libelle;
    /**
     * Niveau de formation, N_MEF_STAT_4.
     */
    private String niveauFormation;
    /**
     * Filière, N_MEF_STAT_5.
     */
    private String filiere;
    /**
     * Niveau de formation de diplôme, N_NIVEAU_FORMATION_DIPLOME.
     **/
    private String niveauFormationDiplome;
    /**
     * Spécialité de formation, N_FORMATION_DIPLOME.
     */
    private String specialite;
    /**
     * Type du MEF, N_TYPE_MEF académique ou national.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = IntConst.I15)
    private MEFType type;

    /**
     * Relation bidirectionnelle.
     * Code MEF de rattachement (normalement code MEF national)
     * , si cas d'un MEF académique.
     */
    @ManyToOne
    @JoinColumn(name = "mef_principal_fk")
    private MEF rattachement;
    /**
     * Relation bidirectionnelle.
     */
    @OneToMany(mappedBy = "rattachement")
    private Set<MEF> sousMEFs = new HashSet<>();

    /**
     * Constructeur de l'objet MEF.java avec les champs utiles pour un enseignant.
     *
     * @param code    Code MEF N_MEF.
     * @param libelle Libelle MEF.
     * @param source  Source de création
     */
    public MEF(final String code, final String libelle, final String source) {
        super();
        this.code = code;
        this.libelle = libelle;
        this.source = source;
    }

    /**
     * Constructeurs de MEF avec les champs obligatoire pour définir le MEF d'un Eleve.
     *
     * @param code            Code MEF N_MEF.
     * @param libelle         Libellé MEF.
     * @param source          Source de création.
     * @param niveauFormation Niveau de formation N_MEF_STAT_4.
     * @param filiere         Filère N_MEF_STAT5.
     */
    public MEF(final String code, final String libelle, final String source, final String niveauFormation,
               final String filiere) {
        super();
        this.code = code;
        this.libelle = libelle;
        this.niveauFormation = niveauFormation;
        this.filiere = filiere;
    }

}
