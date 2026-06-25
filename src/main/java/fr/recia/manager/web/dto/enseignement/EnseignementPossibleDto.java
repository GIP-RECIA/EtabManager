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

package fr.recia.manager.web.dto.enseignement;

import fr.recia.manager.db.dto.groupe.EnseignementEtabDto;
import lombok.Data;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Data
public class EnseignementPossibleDto {
    private long id;
    private String libelle;
    private Set<ClasseEnseignementPossibleDto> classes;
    private Set<GroupeEnseignementPossibleDto> groupes;

    public EnseignementPossibleDto(EnseignementEtabDto enseignement){
        this.id = enseignement.getIdEns();
        this.libelle = enseignement.getMatiere();
        this.classes = new TreeSet<>(Comparator.comparing(ClasseEnseignementPossibleDto::getLibelle));
        this.groupes = new TreeSet<>(Comparator.comparing(GroupeEnseignementPossibleDto::getLibelle));
    }

    public void addClasse(EnseignementEtabDto enseignementEtabDto){
        this.classes.add(new ClasseEnseignementPossibleDto(enseignementEtabDto));
    }

    public void addGroupe(EnseignementEtabDto enseignementEtabDto){
        this.groupes.add(new GroupeEnseignementPossibleDto(enseignementEtabDto));
    }
}
