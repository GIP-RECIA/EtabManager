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

package fr.recia.manager.services.db;

import fr.recia.manager.db.entities.personne.APersonne;
import fr.recia.manager.db.entities.personne.Login;
import fr.recia.manager.db.repositories.personne.LoginRepository;
import fr.recia.manager.services.exceptions.LoginTooHighException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private LoginRepository<Login> loginRepository;

    /**
     * Trouve et modifie le login d'une personne.
     * Le login passé en paramêtre ne doit pas avoir de compteur à la fin
     * Il doit être de la forme prenom.nom
     */
    public Login updateLogin(final String loginPrefix, final APersonne aPersonne) {
        Pattern p = Pattern.compile(loginPrefix + "(\\d*)");
        Matcher m;
        Instant date = new Date().toInstant();
        List<Login> loginList = loginRepository.findByNomLike(loginPrefix + "%");
        String newLogin = loginPrefix;
        int nbMax = 0;
        int nb;
        boolean[] cptUtilise = new boolean[100];
        if (loginList != null) {
            for (Login l : loginList) {
                m = p.matcher(l.getNom());
                if (m.matches()) {
                    if ("".equals(m.group(1))) {
                        nb = 0;
                    } else {
                        nb = Integer.parseInt(m.group(1));
                    }
                    if (nb < 100) {
                        cptUtilise[nb++] = true;
                    }

                    if (nbMax < nb) {
                        nbMax = nb;
                    }
                }
            }
        }
        if (nbMax > 0) {
            if (nbMax < 100) {
                newLogin = String.format("%s%d", newLogin, nbMax);
            } else {
                // on cherche le premier disponible.
                for (int i = 0; i < cptUtilise.length; i++) {
                    if (!cptUtilise[i]) {
                        nbMax = i;
                        newLogin = String.format("%s%d", newLogin, i);
                        break;
                    }
                }
                if (nbMax > 100) {
                    throw new LoginTooHighException("Login too high");
                }
            }
        }
        Login login = new Login(newLogin);
        login.setApersonneLogin(aPersonne);
        login.setDateCreation(LocalDateTime.from(date));
        login.setDateModification(LocalDateTime.from(date));
        return login;
    }

}
