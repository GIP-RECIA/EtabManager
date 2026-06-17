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

package fr.recia.manager.web.dto.user;

import fr.recia.manager.db.enums.CategoriePersonne;
import fr.recia.manager.db.enums.Civilite;
import fr.recia.manager.web.dto.function.FonctionToModify;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserCreation {

    // Commun à tous
    private Long structureRattachement;
    private CategoriePersonne categoriePersonne;
    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private Civilite civilite;
    private LocalDateTime dateFin;
    private LocalDateTime dateDebut;
    // Tous sauf élève
    private String courriel;
    // Spécifique élève
    private String statut;
    private String regime;
    private boolean majeur;
    private boolean majeurAnticipe;
    private boolean transportScolaire;
    private Long mefEleve;
    private Long classe;
    private List<Long> enseignements;
    // Tous sauf élève
    private List<FonctionToModify> fonctions;
    // Spécifique enseignant
    private Long enseignementProf;
    private List<Long> groupesEns;

}
