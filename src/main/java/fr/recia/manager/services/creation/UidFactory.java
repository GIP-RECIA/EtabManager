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

import fr.recia.manager.configuration.AppProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UidFactory {

    private String codeRegion;
    private String nomSource;
    private int nbLettreAnn;
    private int nbLettreTotal;
    private int nbLettreInc;
    private int baseConvertion;

    public UidFactory(AppProperties appProperties) {
        this.codeRegion = appProperties.getUidFactory().getCodeRegion();
        this.nomSource = appProperties.getUidFactory().getNomSource();
        this.nbLettreAnn = appProperties.getUidFactory().getNbLettreAnn();
        this.nbLettreTotal = appProperties.getUidFactory().getNbLettreTotal();
        this.nbLettreInc = appProperties.getUidFactory().getNbLettreInc();
        this.baseConvertion = ('Z' > 'z' ? 'Z' : 'z') + 1;
    }

    public String codeAnnee(final String annee) {
        // si l'annee est du type 2009/2010, c'est 2009 qui nous interesses
        int idx = annee.indexOf('/');
        if (idx < nbLettreAnn) {
            // cas ou on n'a pas de /
            idx = annee.length();
            if (idx < nbLettreAnn) {
                return null;
            }
        }
        return annee.substring(idx - nbLettreAnn, idx);
    }

    private boolean calculNbLettreInc(final String codeGenerateur) {
        if (nbLettreInc > 0) {
            return true;
        }
        if (codeRegion != null && codeGenerateur != null) {
            nbLettreInc = nbLettreTotal - nbLettreAnn - getCodeRegion().length() - codeGenerateur.length();
        } else {
            nbLettreInc = 0;
        }
        return nbLettreInc > 0;
    }

    public String uid(final String annee, final int increment, final String codeGenerateur) {
        String uidS;
        if (calculNbLettreInc(codeGenerateur)) {
            if (nbLettreAnn > 0) {
                String codeAnnee = codeAnnee(annee);
                if (codeAnnee == null) {
                    return null;
                }
                uidS = String.format("%s%s%s%s",
                    codeRegion,
                    codeAnnee,
                    codeGenerateur,
                    AlphanumericsTools.toAlphanum(increment, nbLettreInc));
            } else {
                uidS = String.format("%s%s%s",
                    codeRegion,
                    codeGenerateur,
                    AlphanumericsTools.toAlphanum(increment, nbLettreInc));

            }
            return uidS;
        }
        return null;
    }

    /**
     * convertie la clee en uid
     *
     * @param clee
     * @return uid
     */
    public String uid(final String clee) {
        long clef = Long.parseLong(clee);
        char uid[] = new char[nbLettreTotal];

        for (int i = nbLettreTotal; --i >= 0; ) {
            int c = (int) (clef % baseConvertion);
            uid[i] = (char) c;
            clef = clef / baseConvertion;
        }

        return new String(uid);
    }

    /**
     * Calcul de la clee a partir de l'uid
     */
    public String clee(final String uid) {
        long clee = 0;
        for (int i = 0; i < uid.length(); i++) {
            int val = uid.charAt(i);
            clee = (baseConvertion * clee) + val;
        }
        return String.valueOf(clee);
    }

    public String getSource(String suffixSource) {
        return String.format("%s_%s", nomSource, suffixSource);
    }

}
