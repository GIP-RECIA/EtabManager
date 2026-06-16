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

package fr.recia.web.rest;

import fr.recia.manager.configuration.AppProperties;
import fr.recia.manager.db.entities.APersonneAStructure;
import fr.recia.manager.db.entities.education.Discipline;
import fr.recia.manager.db.entities.fonction.Fonction;
import fr.recia.manager.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.structure.Etablissement;
import fr.recia.manager.db.repositories.APersonneAStructureRepository;
import fr.recia.manager.db.repositories.education.DisciplineRepository;
import fr.recia.manager.db.repositories.fonction.FonctionRepository;
import fr.recia.manager.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.manager.db.repositories.personne.APersonneRepository;
import fr.recia.manager.db.repositories.structure.EtablissementRepository;
import fr.recia.manager.web.rest.StructureController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;

import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class StructureControllerTest {

    @Autowired
    private EtablissementRepository<Etablissement> etablissementRepository;
    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;
    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;

    private MockMvc mockEtablissementControllerMvc;

    @PostConstruct
    void setup() {
        AppProperties appProperties = new AppProperties();
        StructureController etablissementController = new StructureController();
        setField(etablissementController, "etablissementRepository", etablissementRepository);
        setField(etablissementController, "fonctionRepository", fonctionRepository);
        setField(etablissementController, "aPersonneRepository", aPersonneRepository);
        setField(etablissementController, "disciplineRepository", disciplineRepository);
        setField(etablissementController, "typeFonctionFiliereRepository", typeFonctionFiliereRepository);
        setField(etablissementController, "aPersonneAStructureRepository", aPersonneAStructureRepository);

        mockEtablissementControllerMvc = standaloneSetup(etablissementController).build();
    }

    @BeforeEach
    void init() {
    }

//  @Test
//  void getEtablissements() throws Exception {
//    mockEtablissementControllerMvc.perform(get("/api/structure/etablissement")
//        .accept(MediaType.APPLICATION_JSON))
//      .andDo(print())
//      .andExpect(status().isOk())
//      .andReturn();
//  }

//  @Test
//  void getEtablissement() throws Exception {
//    mockEtablissementControllerMvc.perform(get("/api/structure/etablissement/{id}")
//        .accept(MediaType.APPLICATION_JSON))
//      .andDo(print())
//      .andExpect(status().isOk())
//      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//      .andReturn();
//  }

}
