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

package fr.recia.manager.services.creation;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Slf4j
@Component
@Data
public class StringOperation {

    /**
     * REGEXP pour la suppression des caractères ne devant pas apparaitre dans le CN.
     */
    public static final String REGEX_CN = "[-']";

    /**
     * Pour la regexp de remplacement des multi espaces.
     */
    public static final String STRING_SPACE = " ";

    /**
     * REGEXP pour la suppression des espaces en trop.
     */
    public static final String REGEX_SPACES = "\\s{2,}";

    /**
     * Transforme une chaine pouvant contenir des accents dans une version sans accent.
     * Utilisation de la norme java 1.6.
     *
     * @param chaine Chaine a convertir sans accent
     * @return Chaine dont les accents ont été supprimé
     **/
    public String sansAccent(final String chaine) {
        // on normalise la chaine
        String temp = Normalizer.normalize(chaine, Normalizer.Form.NFC);
        // on remplace les caratères entrelacés
        temp = temp.replace("Æ", "AE");
        temp = temp.replace("æ", "ae");
        temp = temp.replace("Œ", "OE");
        temp = temp.replace("œ", "oe");
        // et on remplace les caractères avec accents par décomposition des caratères :
        // caractère primaire suivi du caractère d'accent et suppression des caractères non ascii
        temp = Normalizer.normalize(temp, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return temp;
    }

    /**
     * Méthode permettant de construire le CN d'une personne à partir du sn et du givenName.
     *
     * @param sn        Le nom de la personne.
     * @param givenName Le prénom de la personne.
     * @return <code>String</code>Le CN formaté.
     */
    public String cleanForCN(final String sn, final String givenName) {
        String sb = sansAccent(sn.replaceAll(REGEX_CN, STRING_SPACE)
            .replaceAll(REGEX_SPACES, STRING_SPACE)) +
            STRING_SPACE +
            sansAccent(givenName.replaceAll(REGEX_CN, STRING_SPACE)
                .replaceAll(REGEX_SPACES, STRING_SPACE).toUpperCase());
        return sb;
    }

}
