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

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class AlphanumericsTools {

    /**
     * Valeur de l'incrément.
     */
    private static final int INCREMENT = 64;

    /**
     * Valeur pour la base Alpha Numérique.
     */
    private static final int BASE_ALPHA_NUM = 36;

    /**
     * Méthode de conversion d'un int en base N.
     *
     * @param nb     le nombre à convertir.
     * @param base   la base de conversion, base 36 conseillée pour l'alphanumérique.
     * @param nbChar nombre de caractères que l'on doit obtenir.
     * @return <code>String</code> La chaine de caractère obtenue.
     */
    public static String toBaseN(final int nb, final int base, final int nbChar) {
        long inc = base;
        for (int i = 1; i < nbChar; i++) {
            inc = inc * base;
        }
        // on vérifie que l'on reste dans le cadre du nombre de caratères
        if (inc < (nb + 1)) {
            log.warn("Try to convert a number in base on too less characters: nb base nbchar = ");
        }
        int reste;
        int nombre = nb;
        final int max = 9;
        StringBuilder chaine = new StringBuilder();
        while (nombre >= base) {
            reste = nombre % base;
            nombre = (nombre - reste) / base;
            if (reste > max) {
                chaine.insert(0, ((char) (reste - max + INCREMENT)));
            } else {
                chaine.insert(0, reste);
            }
        }
        if (nombre > max) {
            chaine.insert(0, (char) (nombre - max + INCREMENT));
        } else {
            chaine.insert(0, nombre);
        }
        // Pour l'obtention d'un nombre de caratères égal à la demande
        for (int i = chaine.length(); i < nbChar; i++) {
            chaine.insert(0, "0");
        }

        return chaine.toString().toLowerCase();
    }

    /**
     * Méthode de conversion d'un entier en alphanumérique.
     *
     * @param nb     le nombre à convertir.
     * @param nbChar le nombre de caractère maximum à obtenir, null si nb trop grand.
     * @return <code>String</code> La chaine de caractère alphanumérique obtenue.
     */
    public static String toAlphanum(final int nb, final int nbChar) {
        return toBaseN(nb, BASE_ALPHA_NUM, nbChar);
    }

}
