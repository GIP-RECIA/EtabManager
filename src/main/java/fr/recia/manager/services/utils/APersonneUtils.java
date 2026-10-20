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

package fr.recia.manager.services.utils;

import fr.recia.manager.configuration.bean.CustomConfigProperties;
import fr.recia.manager.db.dto.personne.DatabasePersonneDto;
import fr.recia.manager.db.entities.personne.APersonne;

import java.util.List;

public class APersonneUtils {

    /**
     * Donne le nom du guichet externe d'une personne.
     * Retourne null si la personne s'authentifie sur notre LDAP
     */
    public static String getGuichet(APersonne aPersonne, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties){
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(aPersonne.getCleJointure().getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(aPersonne.getCategorie())){
                        return guichetProperty.getNom();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Donne le nom du guichet externe d'une personne.
     * Retourne null si la personne s'authentifie sur notre LDAP
     */
    public static String getGuichet(DatabasePersonneDto databasePersonneDto, List<CustomConfigProperties.LoginOfficeProperties> loginOfficeProperties){
        for(CustomConfigProperties.LoginOfficeProperties loginOfficeProperty : loginOfficeProperties){
            if(loginOfficeProperty.getSource().equals(databasePersonneDto.getSource())){
                for(CustomConfigProperties.LoginOfficeProperties.GuichetProperties guichetProperty : loginOfficeProperty.getGuichets()){
                    if(guichetProperty.getCategoriesPersonne().contains(databasePersonneDto.getCategorie())){
                        return guichetProperty.getNom();
                    }
                }
            }
        }
        return null;
    }

}
