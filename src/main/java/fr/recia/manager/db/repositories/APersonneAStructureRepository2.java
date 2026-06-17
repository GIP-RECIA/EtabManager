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

package fr.recia.manager.db.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class APersonneAStructureRepository2 {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void insertInStructure(Long idPersonne, Long idStructure) {
        entityManager.createNativeQuery("INSERT INTO apersonnes_astructures (APERSONNE_ID, ASTRUCTURE_ID) VALUES (?, ?)")
            .setParameter(1, idPersonne)
            .setParameter(2, idStructure)
            .executeUpdate();
    }

    @Transactional
    public void deleteFromStructure(Long idPersonne, Long idStructure) {
        entityManager.createNativeQuery("DELETE FROM apersonnes_astructures WHERE APERSONNE_ID = ? AND ASTRUCTURE_ID = ?")
            .setParameter(1, idPersonne)
            .setParameter(2, idStructure)
            .executeUpdate();
    }

}
